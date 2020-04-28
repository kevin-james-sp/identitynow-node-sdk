var client;

function AccessProfiles( client ) {

    this.client=client;


}

AccessProfiles.prototype.list=function( id ) {
        
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

AccessProfiles.prototype.get = function get ( tagId, options ) {
    
    let url=this.client.apiUrl+'/v2/access-profiles/'+tagId;
    
    var that=this;
    return this.client.get(url)
    .then( function (resp) {
        let ret=resp.data;
        let promise=Promise.resolve();
        if (options!=null){
            // Clean the source
            if (options.clean) {
                ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                    ( (k === 'id') || (k === 'created') || (k === 'modified') ) ? undefined : v)
                );
            }
            if (options.export) {
                return that.client.get(url+'/entitlements').then( function( ents ){
                    let list=[];
                    ents.data.forEach( function( ent ){
                        list.push( {
                            applicationName: ret.sourceName,
                            attribute: ent.attribute,
                            value: ent.value
                        });
                    });
                    ret.entitlements=list;
                    return Promise.resolve(ret);
                }, function( err ){
                    return Promise.reject({
                        url: url,
                        status: err.response.status,
                        statusText: err.response.statusText
                    });
                });
            }
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

AccessProfiles.prototype.create = function( tag ) {

    let url=this.client.apiUrl+'/beta/AccessProfiles/'+tagId;

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



module.exports = AccessProfiles;