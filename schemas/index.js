var client;

function Schemas( client ) {

    this.client=client;


}

Schemas.prototype.list=function( id ) {
        
    let url=this.client.apiUrl+'/beta/sources/'+id+'/schemas';
    let that=this;

    return this.client.get(url)
        .then(
        function (resp) {
            return Promise.resolve(resp.data);
        }
        , function (err) {
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });

}

Schemas.prototype.get = function get ( appId, schemaId , options={} ) {
    
    let url=this.client.apiUrl+'/beta/sources/'+appId+'/schemas/'+schemaId;
    let that=this;

    return this.client.get(url)
        .then( 
        function (resp) {
            if ( options && options.clean ) {
                delete resp.data.id;
                delete resp.data.created;
            }
            if (options.tokenize) {
                obj = that.client.SDKUtils.tokenize(resp.data.name, resp.data, options.tokens);
                return Promise.resolve( obj );
            }
            return Promise.resolve( resp.data );
        }
        , function (err) {
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            })
        }
        );
}

Schemas.prototype.create = function( appId, schema ) {

    // Sanity check
    if ( appId==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No app Id specified for creation'
        });
    }

    if ( schema==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No schema specified for creation'
        });

    }

    let url=this.client.apiUrl+'/beta/sources/'+appId+'/schemas';
    
    return this.client.post(url, schema).then( function (resp ) {
        return Promise.resolve(resp.data.id);
    }
    , function ( err ) {
        return Promise.reject(err);
    }
    );
}

Schemas.prototype.update = function get ( appId, schemaId, newSchema, options ) {
    let url=this.client.apiUrl+'/beta/sources/'+appId+'/schemas/'+schemaId;
    // POST needs id *in* schema as well as in URL
    newSchema.id=schemaId;
    return this.client.put( url, newSchema )
    .then(
        resolve=> { return resolve; },
        err=> {
            console.log('error update schema');
            return Promise.reject( err );
        }
    );
}


Schemas.prototype.delete = function get ( appId, schemaId ) {
    
    let url=this.client.apiUrl+'/beta/sources/'+appId+'/schemas/'+schemaId;
    
    return this.client.delete(url)
        .then( 
        function (resp) {
            return Promise.resolve(resp);
        }, function (err) {
            console.log('schemas.delete: '+schemaId);
            console.log(url);
            console.log(JSON.stringify(err));
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            })
        }
        );
}

module.exports = Schemas;