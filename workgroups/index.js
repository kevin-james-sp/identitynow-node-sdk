var client;

/**
 * Workgroups
 * 
 * Workgroups are still a v2 API. Also, the members and connections are not returned as part of the workgroup object,
 * so we need to make several API calls to get the whole object
 * These will be returned (and need to be supplied for create) as an object:
 * {
 *   "workgroup": <workgroup object>,
 *   "members": <array of members>,
 *   "connections": <array of connections>
 * }
 * 
 * 
 * @param {*} client 
 */

function Workgroups( client ) {

    this.client = client;


}

function clean( group ) {
    wg = Object.assign( {}, group );
    delete wg.id;
    delete wg.owner.id;
    delete wg.owner.displayName;
    delete wg.owner.emailAddress;
    delete wg.created;
    delete wg.modified;
    delete wg.connectionCount;
    delete wg.memberCount;
    return wg;
}

Workgroups.prototype.list = function ( options = {} ) {

    let url = `${this.client.apiUrl}/v2/workgroups/?offset=0&limit=250&sort=%5B%7B%22property%22%3A%22name%22%2C%22direction%22%3A%22ASC%22%7D%5D`;
    let that = this;

    return this.client.get( url )
        .then(
            function ( resp ) {
                if (!options.full) return resp.data;
                let groups = Object.assign( [], resp.data );
                let ret=[];
                let promises=[];
                groups.forEach( group => {
                    promises.push( that.get( group.id, options ).then( fullGroup => {
                        ret.push( fullGroup );
                    }) );
                });
                return Promise.all( promises ).then( done => {
                    return ret;
                })
            }
        );


}

Workgroups.prototype.getByName = function getByName( name, options = {} ) {

    if ( !name ) {
        return Promise.reject( {
            url: "Workgroups.getByName",
            status: -1,
            statusText: "getByName: name must be specified"
        } );
    }

    return this.list().then( list => {

        for ( i = 0; i < list.length; i++ ) {
            let wg = list[i];
            if ( wg.name == name ) {
                return this.get( wg.id, options );
            }
        }
        return null;
    } )

}

Workgroups.prototype.get = function get( workgroupId, options = {} ) {

    if ( !workgroupId ) {
        throw "Workgroups.get: workgroup ID must be provided"
    }

    let url = `${this.client.apiUrl}/v2/workgroups/${workgroupId}`;
    let that = this;

    let ret = {};

    let promises = [];

    return this.client.get( url )
        .then(
            function ( resp ) {
                if ( resp.data.memberCount > 0 ) {
                    promises.push( that.client.get( `${url}/members` ).then( resp => {
                        if ( options.clean ) {
                            ret.members = [];
                            for ( i = 0; i < resp.data.length; i++ ) {
                                ret.members.push( resp.data[i].alias );
                            }
                        } else {
                            ret.members = resp.data;
                        }
                    } ) )
                }
                if ( resp.data.connectionCount > 0 ) {
                    promises.push( that.client.get( `${url}/connections` ).then( resp => {
                        if ( options.clean ) {
                            ret.connections = [];
                            for ( i = 0; i < resp.data.length; i++ ) {
                                let conn = Object.assign( {}, resp.data[i]);
                                delete( conn.objectId );
                                delete( conn.workgroupId );
                                ret.connections.push( conn );
                            }
                        } else {
                            ret.connections = resp.data;
                        }
                    } ) )
                }

                let wg = Object.assign( {}, resp.data );
                if ( options && options.clean ) {
                    wg = clean(wg);
                }
                if ( options.tokenize ) {
                    obj = that.client.SDKUtils.tokenize( wg.name, wg, options.tokens );
                    return Promise.resolve( obj );
                }
                ret.workgroup = wg;
                return Promise.all( promises ).then( done => {
                    return ret;
                } )
            }
            , function ( err ) {
                if ( err.response ) {
                    return Promise.reject( {
                        url: url,
                        status: err.response.status,
                        statusText: err.response.statusText
                    } )
                } else {
                    console.log( `Workgroups.get failed: ${JSON.stringify( err )}` );
                }
            }
        );
}

Workgroups.prototype.create = function ( workgroup ) {

    // Sanity check
    if ( !workgroup ) {
        return Promise.reject( {
            url: url,
            status: -1,
            statusText: 'Workgroup must be specified'
        } );
    }

    if ( !workgroup.owner || !workgroup.owner.name ) {

        return Promise.reject( {
            url: url,
            status: -1,
            statusText: 'Workgroup owner name must be specified'
        } );

    }

    let url = this.client.apiUrl + '/v2/workgroups'

    let that = this;

    let promise = this.client.Identities.get( workgroup.owner.name );

    return promise.then( owner => {

        workgroup.owner.id = owner.id;

        return this.client.post( url, schema ).then( resp => {
            return resp.data.id;
        } );
    }
        , function ( err ) {
            return Promise.reject( err );
        }
    );
}

///
// Not yet translated - this is the copy/pasted code for schemas
///
// Workgroups.prototype.update = function get ( appId, schemaId, newSchema, options ) {
//     let url=this.client.apiUrl+'/v3/sources/'+appId+'/schemas/'+schemaId;
//     newSchema.id=schemaId;
//     return this.client.put( url, newSchema )
//     .then(
//         resolve=> { return resolve; },
//         err=> {
//             console.log('error update schema');
//             return Promise.reject( err );
//         }
//     );
// }


// Not yet translated - still copy/pasted schema code
// action is POST /v2/workgroups/bulk-delete with 
// { ids: [ array of ids ]}
// Workgroups.prototype.delete = function get ( appId, schemaId ) {

//     let url=this.client.apiUrl+'/v3/sources/'+appId+'/schemas/'+schemaId;

//     return this.client.delete(url)
//         .then( 
//         function (resp) {
//             return Promise.resolve(resp);
//         }, function (err) {
//             console.log('schemas.delete: '+schemaId);
//             console.log(url);
//             console.log(JSON.stringify(err));
//             return Promise.reject({
//                 url: url,
//                 status: err.response.status,
//                 statusText: err.response.statusText
//             })
//         }
//         );
// }

module.exports = Workgroups;