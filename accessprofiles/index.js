const sortArray = require('sort-array');

var client;

function AccessProfiles( client ) {

    this.client=client;

}

AccessProfiles.prototype.getPage=function(off, lst, query="*", options={} ) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;

    let url=this.client.apiUrl+'/v3/search?limit='+limit+'&offset='+offset+'&count=true';
    let that=this;
    
    let payload={
        "query": {
            "query": query,
        },        
        "indices": [ "accessprofiles" ]        
    };

    if (options.includeNested) {
        payload.includeNested = options.includeNested;
    }

    return this.client.post(url, payload)
        .then( function (resp) {
        count=resp.headers['x-total-count'];
        list=list.concat(resp.data);
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(offset, list, query);
        }
        return Promise.resolve(list);
    }, function ( err ) {
        console.log('getPage.reject');
        console.log( url );
        console.log( err );
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });

}

AccessProfiles.prototype.listv3 = function list () {

    return this.getPage().then( theList => {
        return sortArray( theList, { by: 'name' } );
    }, err => {
        return Promise.reject( err );
    });

}

AccessProfiles.prototype.listv2=function() {
        
    let url=this.client.apiUrl+'/v2/access-profiles';
    let that=this;

    return this.client.get(url)
        .then(
        function (resp) {
            console.log('AccessProfiles.list');
            console.log(JSON.stringify(resp.data, null, 2));
            return Promise.resolve(resp.data);
        }
        , function (err) {
            console.log('AccessProfile');
            console.log(JSON.stringify(err, null, 2));
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });

}

AccessProfiles.prototype.list=function( options ) {
    if ( options && options.useV2 ) {
        return this.listv2();
    } else {
        return this.listv3();
    }
}

AccessProfiles.prototype.search = function( name ) {

    return this.getPage( 0, [], '"'+name+'"' );

}

AccessProfiles.prototype.getByName = function( name, options={} ) {

    let that=this;
    return this.search( name ).then( results => {
        if ( results.length==0 ) {
            return Promise.reject({
                url: 'AccessProfiles.getByName',
                status: -1,
                statusText: 'Access profile \''+name+'\' not found'
            })
        }
        if ( results.length>1 ) {
            return Promise.reject({
                url: 'AccessProfiles.getByName',
                status: -1,
                statusText: 'Ambiguous name \''+name+'\''
            })
        }
        // We did a search, which uses beta(~=v3) API. If options specified v2,
        // we need to return a 'get'
        if ( options.useV2 ) {
            return that.getv2( results[0].id, options );
        }
        return results[0];
    }, err => {
        return Promise.reject( err );
    });

}

AccessProfiles.prototype.get = function get ( tagId, options={} ) {

    if ( options.useV2 ) {
        return this.getByName( tagId, options ); // V2 API get is by ID; so use GetByName to to a v3 search then return v2 get
    } else {
        return this.getv3( tagId, options );
    }
}
 
AccessProfiles.prototype.getv2 = function get ( tagId, options ) {
    let url=this.client.apiUrl+'/v2/access-profiles/'+tagId;
    
    var that=this;
    
    let ret;
    promise=this.client.get(url);
    
    promise = promise.then( resp => {
        ret=resp.data;
        return this.client.Identities.getv2(ret.ownerId).then(
            identity => {
                ret.ownerName=identity.alias;
                return Promise.resolve(ret);
            }, err => {
                return Promise.reject( err );
            }
        );
    }, err => {
        return Promise.reject( err );
    });

    promise = promise.then( resp => {
        return this.client.Entitlements.list( {
            sourceName: resp.sourceName,
            entitlements: resp.entitlements })
        .then( ents => {
            newEnts=[];
            ents.forEach( ent => {
                newEnts.push( {
                    attribute: ent.attribute,
                    value: ent.value
                });
            });
            resp.entitlements=newEnts;
            return resp;
        }, err => {
            return Promise.reject( {
                url: 'AccessProfile.get.Entitlements',
                status: -1,
                statsText: 'Get Entitlements Failed'
            })
        }

        )
    })

    promise = promise.then( ret => {
        let promise=Promise.resolve();
        if (options!=null){
            // Clean the source
            if (options.clean) {
                ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                    ( (k === 'id') || (k === 'created') || (k === 'modified')
                    || (k === 'sourceId') || (k === 'ownerId') ) ? undefined : v)
                );
            }
            if (options.tokenize) {
                ret = that.client.SDKUtils.tokenize(ret.name, ret, options.tokens);
            }
        }
        return Promise.resolve( ret );
    }, err => {
        return Promise.reject({
            url: url,
            status: err.response.status|-1,
            statusText: err.response.statusText|err.message
        })
    });

    return promise;
}

AccessProfiles.prototype.getv3 = function get ( tagId, options={} ) {
    
    // let url=this.client.apiUrl+'/v2/access-profiles/'+tagId;
    let url=this.client.apiUrl+'/v3/search';
    let payload={
        query: {
            query: tagId
        },
        "indices": [ "accessprofiles" ]
    };
    if (options.includeNested) {
        payload.includeNested = options.includeNested;
    }
    
    var that=this;
    return this.client.post(url, payload)
    .then( function (resp) {
        let ret=resp.data[0];
        let promise=Promise.resolve();
        // Clean the source
        if (options.clean) {
            ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                ( (k === 'id') || (k === 'created') || (k === 'modified') ) ? undefined : v)
            );
        }
        if (options.tokenize) {
            ret = that.client.SDKUtils.tokenize(ret.name, ret, options.tokens);
        }
        return Promise.resolve(ret);
    },
    function ( err ) {
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        })
    });
}

AccessProfiles.prototype.create = function( json, defaultOwner, options = { useV2: true } ) {

    if ( options && options.useV2 ) {
        return this.createv2( json, defaultOwner );
    } else {
        return this.createv3( json, defaultOwner );
    }
}

AccessProfiles.prototype.createv2 = function( json, defaultOwner ) {
    
    let url=this.client.apiUrl+'/v2/access-profiles';
    //TODO: Cache looked up identities
    // Sanity check
    if ( json==null ) {
        return Promise.reject({
            url: 'AccessProfiles.createv2',
            status: -1,
            statusText: 'No Access Profile specified for creation'
        });
    }
    if ( defaultOwner==null ) {
        return Promise.reject({
            url: 'AccessProfiles.createv2',
            status: -1,
            statusText: 'No Default Owner specified'
        });
    }
    if ( json.name==null ) {
        return Promise.reject({
            url: 'AccessProfiles.createv2',
            status: -1,
            statusText: 'No name specified for creation'
        });
    }
    if (json.entitlements==null || json.entitlements.length==0) {
        console.log(`no entitlements in ${JSON.stringify(json)}`);
        return Promise.reject({
            url: 'AccessProfiles.createv2',
            status: 100,
            statusText: `WARN: Skipping Access Profile ${json.name}, no entitlements specified`
        });
    }

    let promise=Promise.resolve();
    
    if (json.ownerId==null) {
        promise=this.client.Identities.get( json.ownerName || json.owner.name , { useV2: true} ) // Export is V3
        .then( identity => {
            json.ownerId=identity.id;
            return Promise.resolve();
        }).catch( err => {
            if ( err.status==404) {
                console.log(`Access profile owner ${json.ownerName || json.owner.name} not found - falling back to ${defaultOwner}`);
                return this.client.Identities.get( defaultOwner ).then( defaultIdentity => {
                    // console.log(`found default identity ${JSON.stringify(defaultIdentity)}`);
                    json.ownerId = defaultIdentity.id;
                }).catch ( err => {
                    console.log(`Couldn't find default owner ${defaultOwner}`);
                    throw err;
                });
            } else {
                console.log('non-404 error looking for owner');
                throw err;
            }
        });
    }
    if (json.sourceId==null) {
        promise=promise.then( ok => {
            return this.client.Sources.getByName( json.sourceName || json.source.name )
            .then( source => {
                json.sourceId=source.connectorAttributes.cloudExternalId;
                return Promise.resolve();
            }, err => {
                return Promise.reject( err );
            })
        }, err => {
            return Promise.reject( err );
        });
    }

    promise = promise.then( ok => {
        // convert entitlements to IDs
        let entitlementlist="";
        let first = true;
        json.entitlements.forEach(entitlement=> {
            if (first) {
                first=false;
            } else {
                entitlementlist+=" OR ";
            }
            entitlementlist+=`\"${entitlement.value}\"`;
        });
        let query = {
            "query": {
                "query": `source.name:\"${json.source.name}\" AND (${entitlementlist})`
            },
            "queryResultFilter": {
                "includes": [
                    "id",
                    "value"
                ]
            },
            "indices": [
                "entitlements"
            ],
            "includeNested": false
        }
        // console.log(`Query: ${JSON.stringify(query)}`);
        return this.client.post(this.client.apiUrl+'/v3/search', query).then( entitlements => {
            
            let entitlementIDs=[];

            entitlements.data.forEach( entitlement => {
                entitlementIDs.push(entitlement.id);    
            } );
            json.entitlements=entitlementIDs;
        } );
    })
    
    return promise.then( () => {
        let allowedKeys=['name', 'description', 'ownerId', 'sourceId', 'entitlements', 'approvalSchemes',
        'revokeRequestApprovalSchemes', 'requestCommentsRequired', 'deniedCommentsRequired', 'disabled' ];
        Object.keys(json).forEach( key => {
            // Strip out attributes that aren't in Neil's example
            if (!allowedKeys.includes( key )) {
                delete json[key];
            }
        });
        if ( json.entitlements.length==0 ) {
            console.log(`WARN: Skipping Access Profile ${json.name}, no entitlements found`);
            throw {
                url: 'AccessProfiles.createv2',
                status: 100,
                statusText: `WARN: Skipping Access Profile ${json.name}, no entitlements resolved`
            };
        }
        return this.client.post(url, json).then( resp => {
            return {
                result: "ok",
                message: resp.data
            }
        });
    }, err => {
        console.log(`Error creating Access profile ${json.name}: ${JSON.stringify(err)}`);
        throw err;
    });
    
}

AccessProfiles.prototype.createv3 = function( json, defaultOwner ) {
    throw {
        url: 'AccessProfiles.createv3',
        status: -1,
        statusText: 'AccessProfile.create not in v3 API yet'
    }
}

AccessProfiles.prototype.deleteByName = function( name, options = {}) {

    if (options.useV2==null) {
        options.useV2=true;
    }
    let that=this;
    this.search( name ).then( profiles => {
        if ( profiles.length==0 ) {
            return Promise.reject({
                url: 'AccessProfiles.deleteByName',
                status: -1,
                statusText: 'AccessProfile \''+name+'\' not found'       
            });
        }
        if ( profiles.length>1 && !options.multiple) {
            return Promise.reject({
                url: 'AccessProfiles.deleteByName',
                status: -1,
                statusText: 'AccessProfile name \''+name+'\' is ambiguous'       
            });            
        }
        let promises=[];
        profiles.forEach( profile => {
            promises.push( that.delete( profile.id ));
        });
        return Promise.all( promises );
    }, err => {
        console.log(`Error deleting Access profile ${name}: ${err}`);
        throw err;
    });

}


AccessProfiles.prototype.delete = function( id, options = { useV2: true } ) {

    if (options.useV2) {
        return this.deletev2( id );
    }
    return this.deletev3( id );
}

AccessProfiles.prototype.deletev2 = function( id ) {
    
    let url=this.client.apiUrl+"/v2/access-profiles/"+id;
    return this.client.delete( url );
    
}

AccessProfiles.prototype.deletev3 = function( id ) {
    
    throw {
        url: 'AccessProfiles.deletev3',
        status: -1,
        statusText: 'AccessProfile.delete not in v3 API yet'
    }

}

module.exports = AccessProfiles;