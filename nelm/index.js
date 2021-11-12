var axios = require( 'axios' );

const FormData = require( 'form-data' );
const JSZip = require( 'jszip' );
const { Readable } = require( 'stream' );
var client;
function NELM( client ) {

    this.client = client;

    this.toZip = function ( object ) {

        let zip = new JSZip();
        let sourceFolder = zip.folder( 'source' );

        sourceFolder.file( object.source.source.name + '.json', JSON.stringify( object.source.source, null, 2 ) );
        if ( object.schemas != null ) {
            let schemaFolder = zip.folder( 'schemas' );
            object.schemas.forEach( function ( schema ) {
                schemaFolder.file( schema.name + '.json', JSON.stringify( schema, null, 2 ) );
            } );
        }
        if ( object.accountProfiles != null ) {
            let apFolder = zip.folder( 'accountProfiles' );
            object.accountProfiles.forEach( function ( ap ) {
                let name = ap.name;
                if ( name.replace( /\s/g, '' ) != ap.usage ) {
                    name = name + '.' + ap.usage;
                }
                apFolder.file( name + '.json', JSON.stringify( ap, null, 2 ) );
            } );
        }
        if ( object.correlationConfig != null ) {
            let ccFolder = zip.folder( 'correlationConfig' );
            ccFolder.file( 'correlationConfig.json', JSON.stringify( object.correlationConfig, null, 2 ) );
        }
        if ( object.connectorFiles ) {
            let filesFolder = zip.folder( 'connectorFiles' );
            for ( let [filename, content] of Object.entries( object.connectorFiles ) ) {
                filesFolder.file( filename, Buffer.from( content, 'base64' ) );
            }
        }
        return zip;

    }

}


NELM.prototype.getPage = function ( off, lst ) {

    let offset = 0;
    if ( off != null ) {
        offset = off;
    }

    let list = [];
    if ( lst != null ) {
        list = lst;
    }

    let limit = 100;

    let url = this.client.apiUrl + '/v3/non-employee-sources?limit=' + limit + '&offset=' + offset + '&count=true';
    let that = this;

    return this.client.get( url )
        .then( function ( resp ) {
            count = resp.headers['x-total-count'];
            list = list.concat( resp.data );
            offset += resp.data.length;
            if ( list.length < count ) {
                return that.getPage( offset, list );
            }
            return Promise.resolve( list );
        }, function ( err ) {
            console.log( 'getPage.reject' );
            console.log( url );
            console.log( err );
            return Promise.reject( {
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            } );
        } );


}

function setName( client, object ) {
    return client.Identities.get( object.id ).then( identity => {
        object.name = identity.name;
    } ).catch( err => {
        if ( err.status == 404 ) {
            err.identity = object.id;
            throw err;
        }
    } );
}

function setIdentityId( client, object, target ) {
    return client.Identities.get( object.name ).then( identity => {
        object.id = identity.id;
        target.push( object );
    } ).catch( err => {
        if ( err.status == 404 ) {
            err.identity = object.name;
            throw err;
        }
    } );
}

NELM.prototype.list = function list( options = {} ) {

    let that = this;
    return this.getPage().then( sources => {

        if ( !options.clean ) {
            return sources;
        }

        let promiseList = [];
        for ( nelmSource of sources ) {
            if ( nelmSource.approvers.length > 0 ) {

                for ( approver of nelmSource.approvers ) {
                    promiseList.push( setName( that.client, approver ) );
                }
            }
            if ( nelmSource.accountManagers.length > 0 ) {
                for ( accountManager of nelmSource.accountManagers ) {
                    promiseList.push( setName( that.client, accountManager ) );
                }
            }
            if ( nelmSource.managementWorkgroup ) {
                promiseList.push( that.client.Workgroups.get( nelmSource.managementWorkgroup.id ).then( identity => {
                    nelmSource.managementWorkgroup.name = identity.name;
                } ).catch( err => {
                    console.log( 'wkg' );
                    console.log( err );
                } ) );
            }
        }
        return Promise.all( promiseList ).then( ok => {
            return JSON.parse( JSON.stringify( sources, ( k, v ) => ( k === 'id' ) || ( k === 'created' ) || ( k === 'modified' ) || ( k === 'sourceId' ) ? undefined : v ) );
        } );
    } )

}

NELM.prototype.getByName = function ( name, options ) {

    // TODO: This is a horrible way to do it. It does a list and then
    // potentially another GET. 

    let that = this;
    return this.list().then( function ( list ) {
        let source;
        if ( list != null ) {
            let foundSrc;
            for ( let src of list ) {
                if ( src.name == ( name ) ) {
                    source = src;
                }
            };
        }
        if ( source ) {
            if ( options ) {
                return that.get( source.sourceId, options );
            } else {
                return source;
            }
        }
        let errMsg = `NELM.getByName: source '${name}' not found`;
        console.log( errMsg );
        throw {
            url: 'NELM',
            status: -1,
            statusText: errMsg
        };
    }, err => {
        console.log( `NELM.getByName: source '${name}' error` );
        console.log( err );
        throw err;
    } );
}

NELM.prototype.get = function get( id, options = {} ) {


    console.log( `NELM.get( ${id}, ${JSON.stringify( options )})` );
    let url = this.client.apiUrl + '/v3/non-employee-sources/' + id;

    let that = this;

    let nelmSource;

    return this.client.get( url )
        .then( resp => {
            if ( options == null || !options.clean ) {
                return resp.data;
            }
            let tokens = [];

            let promiseList = [];
            nelmSource = resp.data;
            if ( nelmSource.approvers.length > 0 ) {

                for ( approver of nelmSource.approvers ) {
                    promiseList.push( setName( that.client, approver ) );
                }
            }
            if ( nelmSource.accountManagers.length > 0 ) {
                for ( accountManager of nelmSource.accountManagers ) {
                    promiseList.push( setName( that.client, accountManager ) );
                }
            }
            if ( nelmSource.managementWorkgroup ) {
                promiseList.push( that.client.Workgroups.get( nelmSource.managementWorkgroup.id ).then( identity => {
                    nelmSource.managementWorkgroup.name = identity.name;
                } ).catch( err => {
                    console.log( 'wkg' );
                    console.log( err );
                } ) );
            }

            return Promise.all( promiseList );
        } ).then( ok => {

            let ret = {
                source: nelmSource
            }

            if ( options.tokenize ) {
                obj = that.client.SDKUtils.tokenize( ret.source.name, ret.source, options.tokens );
                ret.source = obj.object;
                tokens = tokens.concat( obj.tokens );
            }

            /*
            * Other than the NELM source, everything else (schemas etc) are part of the underlying flat file
            * source that is created when the NELM source is created. So let's just call Sources to get that,
            * then swap out the source object.
            */
            // If we aren't exporting, just return the source
            if ( options.export ) {
                return that.client.Sources.get( id, options ).then( source => {
                    source.source = ret.source;
                    return source;
                } ).catch( err => {
                    console.log( '---rejected---' );
                    console.log( err.response.statusText );
                    return Promise.reject( {
                        url: url,
                        status: err.response.status,
                        statusText: err.response.statusText
                    } );
                } );
            } else {
                return ret.source;
            }

        } ).then( retval => {
            if ( options.clean ) {
                // Clean the source
                retval = JSON.parse( JSON.stringify( retval, ( k, v ) =>
                    ( ( k === 'id' ) || ( k === 'sourceId' ) || ( k === 'created' ) || ( k === 'modified' ) ) ? undefined : v )
                )
            }
            return retval;
        } ).catch( err => {
            if ( !err.module ) {
                err.module = 'NELM.get';
            }
            throw err;
        } );
}

/* Create a new source
 * object is an object like
 * {
 *      source:  { <source definition> },
 *      schemas: [ schemas ]
 * }
 */

NELM.prototype.create = function ( nelmDefinition ) {

    // Do sanity check
    // 1. Object must contain a 'source'
    if ( nelmDefinition == null || nelmDefinition.source == null ) {
        return Promise.reject( error( 'NELM.create', 'Create payload must include source' ) );
    }

    // Some notes working with the API
    // owner, cluster, and accountCorrelationConfig must be specified by ID; name is ignored
    // cloudExternalID is honored if present
    // accountCorrelationConfig and cloudExternalID are generated if required

    // source will be in nelmDefinition.source under its key name, e.g.
    // { 
    //   source: {
    //     'Active Directory': {
    //        ...
    //     }
    //   }
    // }
    // There will be only one of these (TODO: Implement check?)

    let definedSchemas = nelmDefinition.schemas;
    let accountProfile = nelmDefinition.accountProfile;
    let identityProfiles = nelmDefinition.identityProfiles;

    // 1. No IDs or create/modified dates are allowed; we will look up IDs as needed
    let source = JSON.parse( JSON.stringify( Object.values( nelmDefinition.source )[0], ( k, v ) => {
        if ( ( k === 'id' ) || ( k === 'created' ) || ( k === 'modified' ) ) {
            return undefined;
        } else {
            return v;
        }
    } ) );

    // Do it. First the source. Prior, we need to    
    // Check for Owner; Look up the ID
    if ( source.owner == null ) {
        throw ( error( 'NELM.create', "Source owner must be specified by name" ) );
    }

    let promises = [];

    let setOwnerPromise = this.client.Identities.get( source.owner ).then( identity => {
        source.owner = identity.id;
        return "ok";
    } )
        .catch( err => {
            console.log( `getting owner for NELM source: ${err}` );
            throw err;
        } );

    newApprovers = [];
    newAccountManagers = [];

    if ( source.approvers ) {
        for ( approver of source.approvers ) {
            promises.push( setIdentityId( this.client, approver, newApprovers ).catch( err => {
                if ( err.status == 404 ) {
                    console.log( `Warning: couldn't find approver ${err.identity} for NELM source ${source.name}` )
                } else {
                    throw err;
                }
            } ) );
        }
    }

    if ( source.accountManagers ) {
        for ( accountManager of source.accountManagers ) {
            promises.push( setIdentityId( this.client, accountManager, newAccountManagers ).catch( err => {
                if ( err.status == 404 ) {
                    console.log( `Warning: couldn't find accountManager ${err.identity} for NELM source ${source.name}` )

                } else {
                    throw err;
                }
            } ) );
        }
    }

    var that = this;

    // The above will do all the lookups of IDS etc in order to create the source. The following
    // is to add/update all the things that require the source to first exist..

    return setOwnerPromise.then( ok => {
        return Promise.all( promises ).then( ok => {

            console.log( `newApprovers=${JSON.stringify( newApprovers, null, 2 )}` );
            source.approvers = newApprovers;

            console.log( `newAccountManagers=${JSON.stringify( newAccountManagers, null, 2 )}` );
            source.accountManagers = newAccountManagers;

            // create the nelm source

            let url = that.client.apiUrl + '/v3/non-employee-sources';
            let appId = '';
            let appType = '';
            let oldId='';
            let nelmSource;
            let appSource;

            console.log( `Creating Source: ${JSON.stringify( source, null, 2 )}` );
            return that.client.post( url, source ).then( resp => {
                nelmSource=resp.data;
                appId = nelmSource.sourceId;
                return that.client.Sources.get( appId );
            }).then( fullSource=> {
                appSource = fullSource;
                oldId = fullSource.connectorAttributes.cloudExternalId;
                return oldId;
            }).then( oldId=> {            
                let appName = nelmSource.name;
                let stageTwoPromises = [];
                if ( definedSchemas != null ) {
                    Object.values( definedSchemas ).forEach( function ( schema ) {
                        // Do we need to replace an automatically generated schema?                  
                        if ( appSource.schemas != null ) {
                            let currentSchemaId = null;
                            appSource.schemas.forEach( value => {
                                if ( value.name == schema.name ) {
                                    currentSchemaId = value.id;
                                }
                            } );

                            let promise = Promise.resolve();
                            if ( currentSchemaId != null ) {
                                stageTwoPromises.push( that.client.Schemas.update( appId, currentSchemaId, schema )
                                    .then( resolve => { return Promise.resolve() },
                                        err => {
                                            console.log( JSON.stringify( err, null, 2 ) );
                                            return Promise.reject( err );
                                        } ) );
                            } else {
                                stageTwoPromises.push( promise.then(
                                    ok => {
                                        that.client.Schemas.create( appId, schema ).then(
                                            function ( sch ) {
                                                console.log( 'sch: ' + sch );
                                                return Promise.resolve( sch );
                                            }, function ( reject ) {
                                                console.log( reject );
                                                return Promise.reject( reject );
                                            }
                                        )
                                    }, reject => {
                                        console.log( 'reject: ' );
                                        return Promise.reject( reject );
                                    }
                                ) );
                            }
                        }
                    } );
                }
                if ( accountProfile != null ) {
                    stageTwoPromises.push( that.client.AccountProfiles.update( appId, accountProfile ) );
                }
                if ( nelmDefinition.correlationConfig && nelmDefinition.correlationConfig.correlationConfig ) {
                    console.log( 'Replacing Correlation Config' );
                    // API returns 'null' for empty correlation config
                    // POST expects '[]' for empty correlation config
                    if ( nelmDefinition.correlationConfig.correlationConfig.attributeAssignments == null ) {
                        nelmDefinition.correlationConfig.correlationConfig.attributeAssignments = [];
                    }
                    stageTwoPromises.push( that.client.post( that.client.apiUrl + '/cc/api/source/update/' + appSource.connectorAttributes.cloudExternalId,
                        {
                            correlationConfig: JSON.stringify( nelmDefinition.correlationConfig.correlationConfig )
                        }, { formEncoded: true }
                    ).then(
                        resolve => {
                            return Promise.resolve( resolve );
                        }, reject => {
                            return Promise.reject( reject );
                        }
                    )
                    );
                }
                return Promise.all( stageTwoPromises ).then( resp => {
                    console.log( 'stage Two promises resolved' );
                    return resp;
                } ).then( resp => {
                    // check for Identity profiles
                    let ipPromises = [];
                    if ( identityProfiles ) {
                        identityProfiles.forEach( identityProfile => {
                            console.log( `NELM: creating Identity Profile ${identityProfile.name}` );
                            ipPromises.push( that.client.IdentityProfiles.create( identityProfile, { useV2: true } ) );
                        } )
                    }
                    return Promise.all( ipPromises );
                } ).then( resp => {
                    return {
                        status: "success",
                        type: "NELM",
                        name: appName,
                        id: appId
                    }
                } );
            } );
        } ).catch( err => {
            return Promise.reject( err );
        } );
    } );
}

NELM.prototype.testConnection = function ( id ) {

    return this.get( id ).then( source => {

        let externalID = source.connectorAttributes.cloudExternalId;
        let url = `${this.client.apiUrl}/cc/api/source/testConnection/${externalID}`;
        return this.client.post( url ).then( response => {
            return response.data;
        } );

    } );

}

NELM.prototype.aggregateOldID = function ( id, config = {} ) {

    let url = `${this.client.apiUrl}/cc/api/source/loadAccounts/${id}`;
    let parms = {};
    if ( config.disableOptimization ) {
        parms.disableOptimization = config.disableOptimization;
    }
    return this.client.post( url, parms );
}

NELM.prototype.aggregateFileByName = function ( name, contents ) {

    let promise = this.getByName( name );


    promise = promise.then( source => {
        let oldID = source.connectorAttributes.cloudExternalId;
        let url = `${this.client.apiUrl}/cc/api/source/loadAccounts/${oldID}`;

        let formdata = [{
            'type': 'boolean',
            'name': 'disableOptimization',
            'value': 'true'
        },
        {
            'type': 'file',
            'name': 'file',
            'filename': 'users.csv',
            'value': contents
        }];

        return this.client.post( url, formdata, {
            multipart: true
        }
        )
    } );

    return promise;

}


function error( module, message ) {
    return {
        status: -1,
        statusMessage: message,
        module: module
    }
}

const pause = ( duration ) => new Promise( res => setTimeout( res, duration ) );

const backoff = ( retries, fn, delay = 500 ) =>
    fn().catch( err => retries > 1
        ? pause( delay ).then( () => backoff( retries - 1, fn, delay * 2 ) )
        : Promise.reject( err ) );


module.exports = NELM;