var client;

function IdentityProfiles( client ) {

    this.client = client;


}

function getSourceId( client, name ) {
    return client.Sources.getByName( name ).then( src => {
        return Promise.resolve( src.id );
    } )
}

/* Extracts all 'sourceName' properties from an object */
function getSourceNames( sourceNames, object ) {

    for ( key of Object.keys( object ) ) {
        if ( key == 'sourceName' ) {
            if ( !sourceNames.includes( object[key] ) ) {
                sourceNames.push( object[key] );
            }
        } else {
            if ( typeof object[key] === 'object' && object[key] != null ) {
                return getSourceNames( sourceNames, object[key] );
            }
        }
    }
    return sourceNames;

}

function timeout( ms ) {
    return new Promise( resolve => setTimeout( resolve, ms ) );
}

async function createProfileWithRetries( client, name, id ) {

    let url = client.apiUrl + '/cc/api/profile/create';
    let retries = 60;

    for ( let i = 0; i < retries; i++ ) {
        console.log(`(${i}) - create '${name}' on '${id}'`);
        let newProfile = await client.post( url, { name: name, sourceId: id }, { formEncoded: true } ).then( response => {
            console.log(`${name} - Identity Profile created ok`);
            return response.data;
        } ).catch( err => {
            if ( err.detailcode && err.detailcode == 1211 ) {
                console.log( `identity data is updating; waiting on create for profile ${name}` );
                return null;
            } else if ( err.status && err.status == 500 
                     && err.data.formatted_msg && err.data.formatted_msg.includes("Field error in object 'com.cloudmasons.IdentityProfile' on field 'priority'") ) {
                console.log( `Race condition; another IdentityProfile takes the same priority ${name}` );
                return null;
            } else {
                console.log(`${i} - createWithRetries( ${name}, ${id} ) - Error ${JSON.stringify(err)}`);
                throw err;
            }
        } );
        if (newProfile) return newProfile;
        console.log(`${i} ${name} - waiting..`);
        await timeout( 30000 );
    }
    throw {
        "url": url,
        "detailcode": 1211,
        "status": 400,
        "statusText": `creating Identity Profile ${name}: Timeout waiting for identity data to update.`
    }
}
// Iterate through an object. If we find a sourceName attribute, create or assign a corresponding sourceId object
// Wanted to put promises straight in the object, but apparently that doesn't work

function iterateSourceNames( client, sourceMap, promiseMap, obj ) {

    if ( obj.sourceName ) {
        if ( !promiseMap[obj.sourceName] ) {
            let src = obj.sourceName;
            promiseMap[src] = getSourceId( client, src ).then( id => {
                console.log( `gotSourceID: ${id}` )
                sourceMap[src] = id;
            } );
        }
    }

    Object.keys( obj ).forEach( key => {

        if ( typeof obj[key] === 'object' ) {
            iterateSourceNames( client, sourceMap, promiseMap, obj[key] );
        }
    } )
}

function iterateInsertSourceIds( sourceMap, obj ) {

    if ( !obj ) return;

    if ( obj.authoritativeSource ) {
        obj.authoritativeSource.id = sourceMap[obj.authoritativeSource.name];
    }

    if ( obj.sourceName ) {
        obj.sourceId = sourceMap[obj.sourceName];
    }

    Object.keys( obj ).forEach( key => {

        if ( typeof obj[key] === 'object' ) {
            iterateInsertSourceIds( sourceMap, obj[key] );
        }
    } )

}
IdentityProfiles.prototype.list = function list( options = {} ) {

    if ( options.useV2 ) {
        return this.listV2( options );
    }

    let url = this.client.apiUrl + '/v3/identity-profiles';
    let that = this;

    let retval = [];

    return this.client.get( url )
        .then( function ( resp ) {
            let list = [];
            resp.data.forEach( function ( itm ) {
                // TODO: TEMPORARY: API is broken and doesn't return name of authoritative source
                // go look it up
                let srcPromise = that.client.Sources.get( itm.authoritativeSource.id, options );
                list.push( srcPromise.then( src => {
                    itm.authoritativeSource.name = src.name;
                    if ( !options || !options.clean ) {
                        retval.push( itm );
                    }
                    retval.push( JSON.parse(
                        JSON.stringify( itm, ( k, v ) =>
                            ( ( k === 'id' ) || ( k === 'sourceId' ) || ( k === 'created' ) || ( k === 'modified' ) ) ? undefined : v )
                    ) );
                } ) );
            } );
            return Promise.all( list ).then( all => {
                return retval;
            } );
        }
        ).catch( reject => {
            console.log( `IdentityProfiles list failed: ${reject}` );
            throw reject;
        } );


}
IdentityProfiles.prototype.listV2 = function list( options = {} ) {

    let url = this.client.apiUrl + '/cc/api/profile/list';
    let that = this;

    let retval = [];

    return this.client.get( url )
        .then( function ( resp ) {
            let list = [];
            resp.data.forEach( function ( itm ) {
                list.push( that.getV2( itm.id, options ).then( profile => {
                    if ( !options || !options.clean ) {
                        retval.push( profile );
                    } else {
                        let cleaned = JSON.parse(
                            JSON.stringify( profile, ( k, v ) =>
                                ( ( k === 'externalId' ) || ( k === 'created' ) || ( k === 'modified' )
                                    || ( k === 'lastAggregated' ) || ( k === 'sinceLastAggregated' ) || ( k === 'lastUpdated' )
                                ) ? undefined : v )
                        );
                        // The idea of this bit is to remove 'id' from all the attribute transforms *unless* it's a reference
                        // (uses a transform). Right now the only things I can see that use 'id' that aren't references are rules.
                        // Adjust this as other use cases become known
                        for ( attr in cleaned.attributeConfig.attributeTransforms ) {
                            if ( attr.type == 'rule' ) {
                                delete attr.attributes.id;
                            }
                        }
                        retval.push( cleaned );
                    }
                } ) );
            } );
            return Promise.all( list ).then( all => {
                return retval;
            } );
        }
        ).catch( reject => {
            console.log( `IdentityProfiles list failed: ${reject}` );
            throw reject;
        } );


}

IdentityProfiles.prototype.get = function get( id, options = {} ) {

    if ( options.useV2 ) {
        return this.getV2( id, options );
    }

    let url = this.client.apiUrl + '/v3/identity-profiles/' + id;

    let that = this;
    return this.client.get( url )
        // This is a temporary hack because of https://sailpoint.atlassian.net/browse/DEVREL-96
        .then( resp => {
            return that.client.Sources.get( resp.data.authoritativeSource.id ).then( src => {
                resp.data.authoritativeSource.name = src.name;
                return resp;
            } );
        } )
        .then( function ( resp ) {
            if ( options == null || !options.clean ) {
                return resp.data;
            }
            // Clean the source
            let ret = JSON.parse( JSON.stringify( resp.data, ( k, v ) => {
                return ( ( k === 'id' ) || ( k == 'sourceId' ) || ( k === 'created' ) || ( k === 'modified' ) ) ? undefined : v;
            }
            ) );

            if ( options.tokenize ) {
                return that.client.SDKUtils.tokenize( ret.name, ret, options.tokens );
            }


            return ret;
        }, function ( err ) {
            console.log( '---rejected---' );
            if ( err.response ) {
                console.log( err.response.statusText );
                throw {
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                };
            } else {
                console.log( err );
                throw ( err );
            }
        } );
}

IdentityProfiles.prototype.getV2 = function get( id, options ) {

    let url = `${this.client.apiUrl}/cc/api/profile/get/${id}`;

    let that = this;
    return this.client.get( url )
        .then( function ( resp ) {
            if ( options == null || !options.clean ) {
                return resp.data;
            }
            // Clean the source
            let ret = JSON.parse( JSON.stringify( resp.data, ( k, v ) => {
                return ( ( k === 'id' ) || ( k == 'sourceId' ) || ( k === 'created' ) || ( k === 'modified' )
                    || ( k == 'applicationId' ) || ( k == 'applicationName' ) ) ? undefined : v;
            }
            ) );

            if ( options.tokenize ) {
                return that.client.SDKUtils.tokenize( ret.name, ret, options.tokens );
            }


            return ret;
        }, function ( err ) {
            console.log( '---rejected---' );
            if ( err.response ) {
                console.log( err.response.statusText );
                throw {
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                };
            } else {
                console.log( err );
                throw ( err );
            }
        } );
}

/**
 * Creates a new Identity Profile
 * First, we need to make sure we have all the source IDs for authoritativeSource and the sourceNames in the transforms.
 * we need to put the applicationId into each attribute. Once we've updated it, we need to
 * do a refresh.
 * 
 */
IdentityProfiles.prototype.create = function ( profile, options = {} ) {

    let url = this.client.apiUrl + '/v3/identity-profiles';
    if ( options.useV2 ) {
        return this.createV2( profile, options );
    }
    let that = this;

    if ( !profile.name ) {
        throw {
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a name'
        };
    }
    if ( !profile.authoritativeSource.name ) {
        throw {
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile authoritativeSource must have a name'
        };
    }

    // enumerate all the sources. I couldn't find a better way to do this, since promises on object properties
    // arent resolved. So make a map to put the results in, and a map of promises. Resolve the promises (each promise
    // will call the source getByName function and then put the result into the sourceMap) and then substitute in the
    // results. I *must* be missing something here with promises.

    let sourceMap = {};
    let promiseMap = {};

    if ( profile.identityAttributeConfig.attributeTransforms ) {
        iterateSourceNames( this.client, sourceMap, promiseMap, profile.identityAttributeConfig.attributeTransforms );
    }

    let prom = Promise.all( Object.values( promiseMap ) ).then( result => {

        iterateInsertSourceIds( sourceMap, profile );

        if ( profile.description == null ) {
            delete profile.description;
        }
        if ( profile.owner == null ) {
            delete profile.owner;
        }

        console.log( `profile with IDs: ${JSON.stringify( profile )}` );
        return profile;
    } );

    prom = prom.then( profile => {

        return that.client.post( url, profile ).then( response => {
            let newprofile = response.data;
            console.log( 'ID Profile updated - refreshing' );
            // Was getting a 405 response with no content type set
            return this.client.post( `${url}/${newprofile.id}/refresh-identities`, null, { contentType: "application/json" } );
        } ).catch( error => {
            console.log( '----------Failed' );
            console.log( error );
        } );
    } );

    return prom;

}

/**
 * Creates a new Identity Profile
 * This goes through a two step process
 * First, it creates a new profile from the name and source id (looked up by source name)
 * This creates a skeleton. Now we need to add in the attributes and do an update. However,
 * we need to put the applicationId into each attribute. Once we've updated it, we need to
 * do a refresh.
 * 
 */
IdentityProfiles.prototype.createV2 = function ( profile, options = {} ) {

    let that = this;
    // The inital (v2) create call needs a name and source ID (old format)

    console.log( `${profile.name} - IdentityProfiles.create (V2)` );
    console.log( `${profile.name} - ${JSON.stringify( profile )}` );
    // console.log( profile );
    if ( !profile.name ) {
        throw {
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a name'
        };
    }
    if ( !profile.source.name ) {
        throw {
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a sourceName'
        };
    }

    let rulePromises = [];

    let sourcePromises = [];
    let sourceNames = [];
    let sources = {};

    // Get IDs for all the sources we need
    for ( xform of profile.attributeConfig.attributeTransforms ) {
        sourceNames = getSourceNames( sourceNames, xform );
    }

    if ( options.debug ) {
        console.log( `${profile.name} - : sourceNames list = ${JSON.stringify( sourceNames )}` );
    }

    for ( sourceName of sourceNames ) {
        // get source ID
        sourcePromises.push( that.client.Sources.getByName( sourceName ).then( attrSource => {
            console.log( `${profile.name} - : ID for ${attrSource.name} is ${attrSource.id})` );
            sources[attrSource.name] = attrSource.id;
        } )
        );
    }

    return Promise.all( sourcePromises ).then( ok => {
        if ( options.debug ) {
            console.log( `${profile.name} - source IDs:` );
            console.log( `${profile.name} - ${JSON.stringify( sources )}` );
        }
        return this.client.Sources.getByName( profile.source.name )
            .then( source => {
                let sourceId = source.connectorAttributes.cloudExternalId;
                let newSourceId = source.id;
                if ( options.debug ) {
                    console.log( `${profile.name} -  id=${sourceId}, newId=${newSourceId}` );
                    console.log( `${profile.name} - { name: ${profile.name}, sourceId: ${sourceId} }` );
                    console.log( `---------------\nProfile: ${JSON.stringify( profile )}\n---------------` );
                }
                return createProfileWithRetries( that.client, profile.name, sourceId ).then( newProfile => {
                    console.log( `${profile.name} - Identity Profile created` );
                    console.log( JSON.stringify( newProfile ) );
                    if ( profile.attributeConfig ) {
                        if ( !newProfile.attributeConfig ) {
                            newProfile.attributeConfig = {};
                        }
                        /*
                        * KMJ - Right now we're going to assume that any required attributes for this
                        * Identity profile (the empty ones that get returned by the create call) have
                        * corresponding definitions in the profile we're deploying, so let's clear out
                        * all the transforms that got passed back. Otherwise we need to do some logic
                        * to update what gets passed back with bits from the passed in profile, and
                        * right now that appears to be overkill..
                        */

                        // if (!newProfile.attributeConfig.attributeTransforms) {
                        newProfile.attributeConfig.attributeTransforms = [];
                        // }
                        profile.attributeConfig.attributeTransforms.forEach( attr => {
                            if ( attr.type == 'accountAttribute' ) {
                                attr.attributes.applicationId = sources[attr.attributes.sourceName];
                            } else if ( attr.type == 'reference' ) {
                                attr.attributes.input.attributes.applicationId = sources[attr.attributes.sourceName];
                            } else if ( attr.type == 'rule' ) {
                                console.log( `searching for rule ${attr.attributes.name}` );
                                rulePromises.push( that.client.Rules.getByName( attr.attributes.name ).then( rule => {
                                    if ( !rule ) {
                                        throw ( `Couldn't find rule ${attr.attributes.name} for Id Profile attribute ${attr.name} on ${profile.name}` );
                                    }
                                    console.log( `${profile.name} - rule: adding ID ${rule.id}` );
                                    attr.attributes.id = rule.id;
                                } ) )
                            }
                            newProfile.attributeConfig.attributeTransforms.push( attr );
                        } );
                    }
                    return Promise.all( rulePromises ).then( rules => {

                        if ( options.debug ) {
                            console.log( `${profile.name} - Updating profile with:` )
                            console.log( `${profile.name} - ${JSON.stringify( newProfile )}` );
                        }
                        delete newProfile.priority;
                        return this.client.post( this.client.apiUrl + '/cc/api/profile/update/' + newProfile.id, newProfile ).then( ok => {
                            console.log( `${profile.name} - ID Profile updated - refreshing` );
                            return this.client.post( this.client.apiUrl + '/cc/api/profile/refresh/' + newProfile.id );
                        } ).catch( err => {
                            console.log( `${profile.name} - IDProfile.create: error updating ${profile.name}` );
                            console.log( `${profile.name} - ${JSON.stringify( profile )}` );
                            console.log( `${profile.name} - ${JSON.stringify( err )}` );
                            throw err;
                        } );
                    } );
                } ).catch( err => {
                    console.log( `${profile.name} - IDProfile.create: error creating ${profile.name}` );
                    console.log( `${profile.name} - ${JSON.stringify( profile )}` );
                    console.log( `${profile.name} - ${JSON.stringify( err )}` );
                    throw err;
                } );

            } );

    } );


}


IdentityProfiles.prototype.createOld = function ( profile, options = {} ) {

    let url = this.client.apiUrl + '/cc/api/profile/create';
    let that = this;
    // The inital (v2) create call needs a name and source ID (old format)

    if ( !profile.name ) {
        throw {
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a name'
        }
    }
    if ( !profile.source.name ) {
        throw {
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a source name'
        }
    }

    // If we need to resolve the name to source ID..
    let sourceIDPromise;
    if ( profile.source.id ) {
        sourceIDPromise = Promise.resolve( profile.source.id );
    } else {
        sourceIDPromise = this.client.Sources.getByName( profile.source.name )
            .then( source => {
                return source.connectorAttributes.cloudExternalId;
            }, err => {
                return Promise.reject( err );
            } );
    }

    sourceIDPromise = sourceIDPromise.then( id => {
        profile.source.id = id;
        // do the create
        return this.client.post( url, { name: profile.name, sourceId: profile.source.id }, { formEncoded: true } ).then( response => {
            console.log( JSON.stringify( response.data, null, 2 ) );
            return response.data;
        }, err => {
            console.log( JSON.stringify( err, null, 2 ) );
            console.log( 'Error creating Identity Profile: ' + err.statusText );
            return Promise.reject( err );
        } );
    }, err => {
        return Promise.reject( err );
    } );

    sourceIDPromise = sourceIDPromise.then( newProfile => {
        console.log( "do something here with body and profile" );
        // Looks like we need to remove attributeConfig from the new profile, as it won't be merged properly
        // (indeterminate overwrite of attributes on 'attributeTransforms' e.g. ids being left)
        delete newProfile.attributeTransforms;
        // Probably need to do something with configuredStates as well
        let mergedProfile = {
            ...newProfile,
            ...profile
        }

        // Define all the promises we need
        let allPromises = [];
        // attribute configs
        mergedProfile.attributeConfig.attributeTransforms.forEach( xform => {
            if ( xform.type == 'rule' ) {
                if ( xform.attributes.id == null ) {
                    allPromises.push( that.client.Rules.getByName( xform.attributes.name ).then( rule => {
                        xform.attributes.id = rule.id;
                    }, err => {
                        return Promise.reject( err );
                    } )
                    );
                }
            }
            else if ( xform.type == 'accountAttribute' ) {
                allPromises.push( that.client.Sources.getByName( xform.attributes.sourceName ).then( source => {
                    xform.attributes.applicationId = source.id;
                }, err => {
                    return Promise.reject( err );
                } )
                );
            }
            else if ( xform.type == 'reference' ) {
                // TODO: Validate transform exists
                allPromises.push( that.client.Sources.getByName( xform.attributes.input.attributes.sourceName ).then( source => {
                    xform.attributes.input.attributes.applicationId = source.id;
                }, err => {
                    return Promise.reject( err );
                } )
                );
            }
        } );
        if ( mergedProfile.credentialService != null ) {
            if ( mergedProfile.credentialService.id == null ) {
                allPromises.push( that.client.Sources.getByName( mergedProfile.credentialService.name ).then( source => {
                    mergedProfile.credentialService.id = source.externalId;
                }, err => {
                    return Promise.reject( err );
                } )
                );
            }
        }
        return Promise.all( allPromises ).then( () => {
            console.log( JSON.stringify( mergedProfile, null, 2 ) );
            // Do the update here
            return mergedProfile;
        } );
    }, err => {
        return Promise.reject( err );
    } )

    return sourceIDPromise;
}


IdentityProfiles.prototype.update = function ( id ) {
    if ( !id ) {
        throw {
            url: 'IdentityProfile.update',
            status: -1,
            statusText: 'id required'
        }
    }
    let url = `${this.client.apiUrl}/cc/api/profile/update/${id}`;
    return this.client.post( url );
}
module.exports = IdentityProfiles;