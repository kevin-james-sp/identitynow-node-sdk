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

Schemas.prototype.get = function get ( appId, schemaId ) {
    
    let url=this.client.apiUrl+'/beta/sources/'+appId+'/schemas/'+schemaId;
    
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
        console.log('schemas.create');
        console.log(JSON.stringify(resp.data));
        return Promise.resolve(resp.data.id);
    }
    , function ( err ) {
        return Promise.reject(err);
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