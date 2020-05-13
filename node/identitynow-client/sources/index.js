var axios = require('axios');

const JSZip=require('jszip');

var client;

function Sources( client ) {

    this.client=client;

    this.toZip = function( object ) {
    
        let zip=new JSZip();
        let sourceFolder=zip.folder('source');

        sourceFolder.file(object.source.source.name+'.json', JSON.stringify(object.source.source, null, 2));
        if (object.schemas!=null) {
            let schemaFolder=zip.folder('schemas');
            object.schemas.forEach( function( schema ){
                schemaFolder.file(schema.name+'.json', JSON.stringify(schema, null, 2));
            });
        }
        if (object.accountProfiles!=null) {
            let apFolder=zip.folder('accountProfiles');
            object.accountProfiles.forEach( function( ap ){
                let name=ap.name;
                if (name.replace(/\s/g, '')!=ap.usage){
                    name=name+'.'+ap.usage;
                }
                apFolder.file(name+'.json', JSON.stringify(ap, null, 2));
            });    
        }
        if (object.correlationConfig!=null) {
            let ccFolder=zip.folder('correlationConfig');
            ccFolder.file('correlationConfig.json', JSON.stringify(object.correlationConfig, null, 2));
        }
        if (object.connectorFiles) {
            let filesFolder=zip.folder('connectorFiles');
            for (let [filename, content] of Object.entries(object.connectorFiles)) {     
                filesFolder.file(filename, Buffer.from(content, 'base64'));
            }
        }
        return zip;
    
    }
    
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
        list=list.concat(resp.data);
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(offset, list);
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


Sources.prototype.list = function list () {

    return this.getPage();

}

// Get a Cloud Connector file from a source
// The URL looks like this:
// https://sppcbu-va-images.s3.amazonaws.com/cook/neil-test/connectorFiles/117767/sqljdbc4.jar
// So we need:
// - pod.      Get from token
// - tenant    Get from token
// - connector id (old CC ID)
// - filename
Sources.prototype.getCCFile = function getCCFile( pod, tenant, sourceId, filename ) {

    let url='https://sppcbu-va-images.s3.amazonaws.com/'+pod+'/'+tenant+'/connectorFiles/'+sourceId+'/'+filename;

    return axios.get( url , {
        responseType: 'arraybuffer'
    });
    
}

Sources.prototype.getZip = function getZip( id ) {
    
    let that=this;
    return this.get(id, {
        clean: true,
        export: true
    }).then( object => {
        return that.toZip( object );
    }, err => {
        return Promise.reject( err );
    });
}

Sources.prototype.getByName = function ( name, options ){

    // TODO: This is a horrible way to do it. It does a list and then
    // potentially another GET. 

    let that=this;
    return this.list().then( function( list ){
        let source;
        if (list!=null) {
            let foundSrc;
            for( let src of list) {
                if (src.name==(name)) {
                    source=src;
                }
            };
        }
        if (source) {
            if ( options ) {
                return that.get( source.id, options );
            } else {
                return Promise.resolve( source );
            }
        }
        console.log(' source not found');
        return Promise.reject({
            url: 'Sources',
            status: -1,
            statusText: 'Source not found'
        });
    }, function( err ){
        return Promise.reject( err );
    });
}

Sources.prototype.get = function get ( id, options = [] ) {
    
    let url=this.client.apiUrl+'/beta/sources/'+id;
    
    let that=this;
    return this.client.get(url)
        .then( resp => {
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
            
            // put source into 'source' object
            let src=ret;
            ret={};
            ret.source=src;
            // array of things we need to wait for
            promises=[];
            // Go get the other objects
            // Schemas
            ret.schemas=[];
            let sourceid=resp.data.id;
            resp.data.schemas.forEach( function(schema) {
                promises.push( that.client.Schemas.get( sourceid, schema.id, options ).then( function (resp) {
                        ret.schemas.push(resp);
                    })
                );
            })
            //let sourceExtId=resp.data.connectorAttributes.cloudExternalId;
            // Account Profiles
            promises.push( that.client.AccountProfiles.list( sourceid, options ).then( function ( resp )
                {
                    if (resp.length>0) {
                        ret.accountProfiles=resp;
                    }
                }, function ( err ) {
                    return Promise.reject({
                        url: url,
                        status: err.response.status,
                        statusText: err.response.statusText
                    });    
                }            
            ));
                // Password Policies
            // Account Correlation Config
            let cloudExtId=resp.data.connectorAttributes.cloudExternalId;
            promises.push( that.client.get( that.client.apiUrl + '/cc/api/source/get/'+ cloudExtId ).then( function( resp ){
                ret.correlationConfig={};
                ret.correlationConfig.correlationConfig=resp.data.correlationConfig;
            }, err => {
                return Promise.reject({
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                })
            })
            );

            // If we're exporting, get the Custom Connector files as base64 into the json
            if (resp.data.connectorAttributes.connector_files) {
                ret.connectorFiles={};
                resp.data.connectorAttributes.connector_files.split(',').forEach( filename => {
                    promises.push( that.getCCFile(that.client.pod, that.client.tenant, resp.data.connectorAttributes.cloudExternalId, filename )
                                        .then( data => {
                                            ret.connectorFiles[filename]=Buffer.from(data.data, 'binary').toString('base64');
                                            return Promise.resolve();
                                        }, err => {
                                            console.log('Skipping custom connector file  '+filename+', err='+err.response.status);
                                            return Promise.resolve();
                                        })
                                 );
                });
            }

            return Promise.all(promises).then( function() {
                if (!options.zip) {
                    return ret;
                }
                return that.toZip( ret );
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

Sources.prototype.addFile = function( id, filename, contents ) {

    return Promise.reject({
        url: 'Upload.file',
        status: -1,
        statusText: 'Not yet implemented'
    })

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

    // source will be in object.source under its key name, e.g.
    // { 
    //   source: {
    //     'Active Directory': {
    //        ...
    //     }
    //   }
    // }
    // There will be only one of these (TODO: Implement check?)

    let source=Object.values(object.source)[0];
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
    if (source.cluster==null||source.cluster.name==null) {
        return Promise.reject("Source cluster must be specified by name");
    }    
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
    var that=this;

    // The above will do all the lookups of IDS etc in order to create the source. The following
    // is to add/update all the things that require the source to first exist..

    return Promise.all(promises).then( function () {

        let url=that.client.apiUrl+'/beta/sources';
        if (source.accountCorrelationConfig) {
            // If there is an accountCorrelationConfig specified on the source,
            // the POST call will return a 404 since we haven't created it yet.
            // So we need to null it out here, and the we upload the config later on..
            source.accountCorrelationConfig=null;

        };
        return that.client.post(url, source).then( function( resp ) {
            let appId=resp.data.id;
            promises=[];
            if (schemas!=null) {
                Object.values(schemas).forEach( function (schema) {
                    // Do we need to replace an automatically generated schema?                  
                    if (resp.data.schemas!=null) {
                        let currentSchemaId=null;
                        resp.data.schemas.forEach( value => {
                            if (value.name==schema.name) {
                                currentSchemaId=value.id;
                            }
                        });
                        
                        let promise=Promise.resolve();
                        if (currentSchemaId!=null) {
                            console.log('replacing '+currentSchemaId);
                            promises.push(that.client.Schemas.update(appId, currentSchemaId, schema)
                            .then( resolve => { return Promise.resolve() },
                            err => {
                                console.log(JSON.stringify(err, null, 2));
                                return Promise.reject( err );
                            }));
                        } else {                        
                            promises.push(promise.then( 
                                ok  => {
                                    that.client.Schemas.create(appId, schema).then(
                                        function ( sch ) {
                                            console.log('sch: '+sch);
                                            return Promise.resolve(sch);
                                        }, function ( reject ) {
                                            console.log(reject);
                                            return Promise.reject(reject);
                                        }
                                    )
                                }, reject => {
                                    console.log('reject: ');                                
                                    return Promise.reject(reject);
                                }
                            ));
                        }                          
                    }
                });
            }
            if (object.correlationConfig && object.correlationConfig.correlationConfig) {
                console.log('Replacing Correlation Config');
                // API returns 'null' for empty correlation config
                // POST expects '[]' for empty correlation config
                if (object.correlationConfig.correlationConfig.attributeAssignments==null) {
                    object.correlationConfig.correlationConfig.attributeAssignments=[];
                }
                promises.push( that.client.post( that.client.apiUrl+'/cc/api/source/update/'+resp.data.connectorAttributes.cloudExternalId,
                    {
                        correlationConfig: JSON.stringify(object.correlationConfig.correlationConfig)
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
            if (object.connectorFiles!=null) {
                for (let [filename, contents] of Object.entries( object.connectorFiles ) ) {
                    that.addFile( appId, filename, contents );
                }
                console.log('do something with connectorFiles');
            }
            return Promise.all(promises).then( function( resp ) {
                    console.log('all promises resolved');
                    return resp;
                }, function( err ){
                    console.log('all.reject');
                    console.log(err);
                    return Promise.reject(err);
                });
            return Promise.resolve( resp );
        }, function( err ) {
            return Promise.reject(err);
        });
    });
}

module.exports = Sources;