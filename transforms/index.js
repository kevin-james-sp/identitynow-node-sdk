var client;

function Transforms( client ) {

    this.client=client;


}

Transforms.prototype.list=function( id ) {
        
    let url=this.client.apiUrl+'/cc/api/transform/list';
    let that=this;

    return this.client.get(url)
        .then(
        function (resp) {
            console.log('Transforms.list');
            console.log(JSON.stringify(resp.data, null, 2));
            return Promise.resolve(resp.data.items);
        }
        , function (err) {
            console.log('Transform');
            console.log(JSON.stringify(err, null, 2));
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });

}

Transforms.prototype.get = function get (transformName ) {
    
    let url=this.client.apiUrl+'/cc/api/transform/get/'+transformName;
    
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

Transforms.prototype.create = function( xform ) {

    // Sanity check
    if ( xform==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No transform specified for creation'
        });
    }
    if ( xform.id==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No id specified for creation'
        });
    }
    if ( xform.type==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No type specified for creation'
        });
    }

    return this.client.post(url, xform).then( function (resp ) {
        return resp.id;
    })
    , function (err) {
        return Promise.reject({
            url: url,
            status: err.slpt_error_code,
            statusText: formatted_msg
        })
    }

}



module.exports = Transforms;