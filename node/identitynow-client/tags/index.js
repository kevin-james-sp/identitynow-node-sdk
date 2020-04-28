var client;

function Tags( client ) {

    this.client=client;


}

Tags.prototype.list=function( id ) {
        
    let url=this.client.apiUrl+'/beta/tags';
    let that=this;

    return this.client.get(url)
        .then(
        function (resp) {
            console.log('Tags.list');
            console.log(JSON.stringify(resp.data, null, 2));
            return Promise.resolve(resp.data);
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

Tags.prototype.get = function get ( tagId ) {
    
    let url=this.client.apiUrl+'/beta/tags/'+tagId;
    
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

Tags.prototype.create = function( tag ) {

    let url=this.client.apiUrl+'/beta/tags/'+tagId;

    // Sanity check
    if ( tag==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No tag specified for creation'
        });
    }
    if ( tag.name==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No name specified for creation'
        });
    }

    return this.client.post(url, tag).then( function (resp ) {
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



module.exports = Tags;