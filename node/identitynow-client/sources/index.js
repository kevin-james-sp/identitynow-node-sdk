var client;

function Sources( client ) {

    this.client=client;


}

Sources.prototype.getPage=function(off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;

    let url=this.client.apiUrl+'/beta/sources?limit='+limit+'&offset='+offset+'&count=true';
    let that=this;

    return this.client.get(url)
        .then( function (resp) {
        count=resp.headers['x-total-count'];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(offset, list);
        }
        return Promise.resolve(list);
    }, function (reject) {
        console.log('getPage.reject');
        console.log(reject);
    });


}


Sources.prototype.list = function list () {

    return this.getPage();

}

Sources.prototype.get = function get ( id, options ) {
    
    let url=this.client.apiUrl+'/beta/sources/'+id;
    
    let that=this;
    return this.client.get(url)
        .then( function (resp) {
            if (options==null || !options.clean) {
                return Promise.resolve(resp.data);
            }
            // Clean the source
            let ret={
                source: JSON.parse(JSON.stringify(resp.data, (k,v) => 
                ( (k === 'id') || (k === 'created') || (k === 'modified') ) ? undefined : v)
            )
            };
            // If we aren't exporting, just return the source
            if (!options.export) {
                return Promise.resolve(ret.source);
            }
            
            // array of things we need to wait for
            promises=[];
            // Go get the other objects
            // Schemas
            ret.schemas=[];
            let sourceid=resp.data.id;
            resp.data.schemas.forEach( function(schema) {
                promises.push( that.client.Schemas.get( sourceid, schema.id ).then( function (resp) {
                        ret.schemas.push(resp);
                    })
                );
            })
            // Password Policies
            // Account Correlation Config
            console.log(ret.schemas);
            return Promise.all(promises).then( function() {
                return ret;
            }, function(err) {
                console.log('---rejected---');
                console.log(err.response.statusText);
                return Promise.reject({
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                });
            });
        } );
}

/* Create a new source
 * object is an object like
 * {
 *      source:  { <source definition> },
 *      schemas: [ schemas ]
 * }
 */

Sources.prototype.create = function( object ) {

    // Do sanity check
    // 1. Object must contain a 'source'
    if (object==null || object.source==null) {
        return Promise.reject({
            errorMessage: 'Create payload must include source'
        })
    }

    // Some notes working with the API
    // owner, cluster, and accountCorrelationConfig must be specified by ID; name is ignored
    // cloudExternalID is honored if present
    // accountCorrelationConfig and cloudExternalID are generated if required

    let source=object.source;
    let schemas=object.schemas;

    // 1. No IDs or create/modified dates are allowed; we will look up IDs as needed
    JSON.stringify(source, (k,v) => {
        if ( (k === 'id') || (k === 'created') || (k === 'modified') ) {
            return Promise.reject({
                status: -1,
                statusText: "invalid key '"+k+"' must be stripped out"
            })
        }
    });

    // Do it. First the source. Prior, we need to    
    // Check for Owner; Look up the ID
    if (source.owner==null||source.owner.name==null) {
        return Promise.reject("Source owner must be specified by name");
    }    
    let promises=[];
    promises.push(this.client.Identities.get(source.owner.name).then(
        function (identity) {
            if (identity==null) {
                return Promise.reject("Owner '"+source.owner.name+"' not found'");
            }
            source.owner.id=identity.id;
        }, function ( reject ) {
            return Promise.reject(reject);
        }

    ));

    // Check for cluster (if specified); look up the ID
    if (source.cluster!=null) {
        promises.push(this.client.Clusters.getByName(source.cluster.name).then(
            function (cluster) {
                if (cluster==null) {
                    return Promise.reject("Cluster '"+source.cluster.name+"' not found'");
                }
                source.cluster.id=cluster.configuration.clusterExternalId;
            }, function ( reject ) {
                return Promise.reject(reject);
            }

        ));
    }

    // Check for account correlation config. if specified in 'object', send it. Otherwise, look up the ID
    if (source.accountCorrelationConfig!=null) {
        promises.push(this.client.CorrelationConfigs.getByName(source.cluster.name).then(
            function (cluster) {
                if (cluster==null) {
                    return Promise.reject("Cluster '"+source.cluster.name+"' not found'");
                }
                source.cluster.id=cluster.id;
            }, function ( reject ) {
                return Promise.reject(reject);
            }

        ));
    }

    var that=this;

    return Promise.all(promises).then( function () {

        console.log('all promises resolved');
        let url=that.client.apiUrl+'/beta/sources';
        return that.client.post(url, source).then( function( resp ) {
            let appId=resp.data.id;
            if (schemas!=null) {
                promises=[];
                schemas.forEach( function (schema) {
                    promises.push(that.client.Schemas.create(appId, schema).then(
                        function (sch) {
                            return Promise.resolve(sch);
                        }, function ( reject ) {
                            return Promise.reject(reject);
                        }
                    ));                                
                })
            }
            return Promise.all(promises).then( function() {
                    return resp;
                }, function( err ){
                    return Promise.reject(err);
                });
        }, function( err ) {
            return Promise.reject(err);
        });
    });
}

module.exports = Sources;