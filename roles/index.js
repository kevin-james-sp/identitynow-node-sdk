var client;
// var attributesToExclude;

function Roles( client ) {
    
    this.client=client;
    this.attributesToExclude=['id', 'created', 'modified', 'synced', 'pod', 'org', 'email', 'accessProfileCount', 'segmentCount'];


}

Roles.prototype.getPage=function( off, lst, query='*', options ) {
        
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
    if (options?.exact) {
        query = `name.exact:"${query}"`
    }
    let payload={
        queryType: "SAILPOINT",
        indices: [ "roles" ],
        query: {
            query: query
        }
    }
    let that=this;
    console.log(JSON.stringify(payload));
    
    return this.client.post(url, payload)
    .then( resp => {
        count=resp.headers['x-total-count'];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(offset, list);
        }
        return Promise.resolve(list);
    }).catch( err => {
        throw {
            url: url,
            status: err.status,
            statusText: err.statusText || err.detailcode
        };
    });
    
    
}


Roles.prototype.list = function list ( options ) {
    if ( options?.useV3 ) {
        return this.listv3( options );
    } else {
        return this.listv2( options );
    }
}

Roles.prototype.listv2 = function list ( options ) {
    
    let promise = this.client.get(`${this.client.apiUrl}/cc/api/role/list`).then( results => {
        let ret=[];
        if ( options?.clean ) {
            results.data.items.forEach( result => {
                let role = JSON.parse(JSON.stringify(result, (k,v) => 
                    ( this.attributesToExclude.includes(k) ? undefined : v) )
                );
                ret.push( role );
            } );
        } else {
            ret=results.data.items;
        }
        return ret;
    });

    if ( options?.dereferenceAccessProfiles!=false ) {

        promise = promise.then( roles => {

            // Access profile id=>access profile name
            let promises = [];
            
            for ( let j=0; j<roles.length; j++ ) {
                let role = roles[j];
                for (let i=0; i<role.accessProfileIds.length; i++) {
                    role.accessProfileNames=[];
                    promises.push( this.client.AccessProfiles.get( role.accessProfileIds[i] ).then( profile => {
                        role.accessProfileNames.push( profile.name );
                    }) );
                }
                delete role.accessProfileIds;
            }
            return Promise.all( promises ).then( done => {
                return roles;
            });
        });
        
    }

    return promise;
}

Roles.prototype.listv3 = function list ( options ) {
    
    return this.getPage().then( results => {
        if (options && options.clean ) {
            let ret=[];
            results.forEach( result => {
                ret.push(
                    JSON.parse(JSON.stringify(result, (k,v) => 
                        ( this.attributesToExclude.includes(k) ? undefined : v) )
                    )
                )
            });
            return ret;
        }
        return results;
    })
    
}

Roles.prototype.get = function get( id, options ) {
    if ( options?.useV3 ) {
        return this.getv3( id, options );
    } else {
        return this.getv2( id, options );
    }
}

Roles.prototype.search = function( name, options ) {

    return this.getPage( 0, [], name, options );

}
Roles.prototype.getByName = function( name, options={} ) {

    let that=this;
    return this.search( name, { exact: true } ).then( results => {
        console.log(JSON.stringify(results));
        if ( results.length==0 ) {
            return Promise.reject({
                url: 'Roles.getByName',
                status: -1,
                statusText: 'Role \''+name+'\' not found'
            })
        }
        if ( results.length>1 ) {
            return Promise.reject({
                url: 'Roles.getByName',
                status: -1,
                statusText: 'Ambiguous name \''+name+'\''
            })
        }
        // We did a search, which uses beta(~=v3) API. If options specified v2,
        // we need to return a 'get'
        if ( !options?.useV3 ) {
            return that.getv2( results[0].id, options );
        }
        return results[0];
    }, err => {
        return Promise.reject( err );
    });

}
Roles.prototype.getv2 = function get ( id, options ) {

    let url = `${this.client.apiUrl}/cc/api/role/get/${id}`;

    let that=this;
    return this.client.get( url ).then( resp => {
        if (resp.data.length!=0) {
           let ret=resp.data;
            if (options?.clean){
                ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                    ( that.attributesToExclude.includes(k) ? undefined : v) )
                );
            }
            if (options?.export) {
            }
            return ret;
        } else {
            return Promise.reject({
                url: 'Roles.getv2',
                status: -1,
                statusText: `Role with id '${id}' not found`
            });
        }
    }).then( role => {
        if ( options?.dereferenceAccessProfiles==false ) {
            return role;
        }
        // Access profile id=>access profile name
        let accessProfileNames = [];
        let promises = [];
        for (let i=0; i<role.accessProfileIds.length; i++) {
            promises.push( this.client.AccessProfiles.get( role.accessProfileIds[i] ).then( profile => {
                accessProfileNames.push( profile.name );
            }) );
        }
        return Promise.all( promises ).then( done => {
            delete role.accessProfileIds;
            role.accessProfileNames = accessProfileNames;
            return role;
        })
    }).catch( err => {
        console.log('roles.get');
        console.log(err);
        return Promise.reject(err);
    });
}

Roles.prototype.getv3 = function get ( id, options ) {
    
    let url=this. client.apiUrl+'/v3/search';
    let payload={
        "queryType": "SAILPOINT",
        "indices": "roles",
        "query": {
            "query": `name.exact:"${id}"`
        }
    }
    
    var that=this;
    return this.client.post(url, payload)
    .then( resp => {
        if (resp.data.length==0) {
            throw {
            url: url,
            status: -1,
            statusText: "Role '"+id+"' not found"
            };
        }
        if (resp.data.length>1) {
            throw {
                url: url,
                status: -1,
                statusText: "Multiple results for Role '"+id+"'"
            };
        }
        let ret=resp.data[0];
        if (options!=null){
            // Clean the source
            if (options.clean) {
                ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                    ( that.attributesToExclude.includes(k) ? undefined : v) )
                );
            }
            if (options.export) {
            }
        }
        return Promise.resolve(ret);
    }).catch( err =>  {
        if ( options.debug ) {
            console.log('roles.get');
            console.log(err);
        }
        return Promise.reject(err);
    } );
}

Roles.prototype.create = function create( json, defaultOwner, options = {} ) {

    let that=this;

    let newRole={};
    
    if ( json==null ) {
        return Promise.reject({
            url: 'Roles.create',
            status: -1,
            statusText: 'No Access Profile specified for creation'
        });
    }
    if ( defaultOwner==null ) {
        return Promise.reject({
            url: 'Roles.create',
            status: -1,
            statusText: 'No Default Owner specified'
        });
    }
    // Use displayName for creation; name has [cloudRole - xxxx] appended to it
    if ( json.displayName==null ) {
        return Promise.reject({
            url: 'Roles.create',
            status: -1,
            statusText: 'No displayName specified for creation'
        });
    }

    let promises = [];
    promises.push( this.client.post( `${this. client.apiUrl}/cc/api/role/create`, 
      { name: json.displayName, description: json.description },
      { formEncoded: true }
    ).then( response=> {
        return response.data
    }).catch( err => {
        return Promise.reject({
            url: 'Roles.create',
            status: -1,
            statusText: err.statusText
        });
    })
    );
    if ( json.accessProfileNames ) {
        json.accessProfileIds = [];
        for ( let i=0; i<json.accessProfileNames.length; i++ ) {
            promises.push( this.client.AccessProfiles.getByName( json.accessProfileNames[i] ).then( profile => {
                json.accessProfileIds.push( profile.id );
            }).catch( err=> {
                console.log(`Warning: access profile ${json.accessProfileNames[i]} not found for role ${json.displayName}`);
            })
            );
        };
        delete json.accessProfileNames;
    }
    
    return Promise.all( promises ).then( result => {
        let newRole = result[0]; // Only promise in the array that returns a result (the new role created);

        // merge old role over new role, with caveats
        // keep the new 'name'
        delete json.name;

        newRole = { ...newRole, ...json };

        if ( options.debug ) {
            console.log( 'updating with');
            console.log( JSON.stringify( newRole, null, 2));
        }
        return that.client.post( `${this. client.apiUrl}/cc/api/role/update`, newRole );
    }).then( updated => {
        if ( options.debug ) {
            console.log('Updated:');
            console.log( JSON.stringify( updated.data ) );
        }
        return updated.data;
    }).catch( err => {
        return Promise.reject({
            url: 'Roles.create',
            status: -1,
            statusText: err.statusText
        });
    });
}

module.exports = Roles;