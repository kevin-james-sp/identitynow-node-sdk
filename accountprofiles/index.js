var client;

function AccountProfiles( client ) {

    this.client=client;


}

AccountProfiles.prototype.list=function( id, options = {} ) {
        
    let url=this.client.apiUrl+'/cc/api/accountProfile/list/';
    let that=this;
    
    return this.client.Sources.get( id ).then( function( resp ) {
        
        return that.client.get(url+resp.connectorAttributes.cloudExternalId).then(
            function (resp) {
                if (options.tokenize) {
                    // Need to tokenize each one individually
                    obj={};
                    obj.object=[];
                    obj.tokens=[];
                    resp.data.forEach( ap=> {
                        let apname=ap.name;
                        if (apname.replace(/\s/g, '')!=ap.usage){
                            apname=apname+'.'+ap.usage;
                        }
                        tokenAP = that.client.SDKUtils.tokenize(apname, ap, options.tokens);
                        obj.object.push(tokenAP.object);
                        obj.tokens = obj.tokens.concat(tokenAP.tokens);
                    });
                    return Promise.resolve( obj );
                }
                return Promise.resolve(resp.data);
            }
            , function (err) {
                return Promise.reject({
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                });
            });
        }, function (err) {
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });
        
        
    }
    
AccountProfiles.prototype.get = function get ( id, usage, options={} ) {
        
    let that=this;
    return this.client.Sources.get( id ).then( function( resp ) {
        
        let url=that.client.apiUrl+'/cc/api/accountProfile/get/'+resp.connectorAttributes.cloudExternalId+'?usage='+usage;
        return that.client.get(url).then(
            function (resp) {
                if (options.tokenize) {
                    obj = that.client.SDKUtils.tokenize(name, resp.data, options.tokens);
                    return Promise.resolve( obj );
                }
                return Promise.resolve(resp.data);

        }
        , function (err) {
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });
    }, function (err) {
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });
}


AccountProfiles.prototype.update = function( id, profile ) {

    let that = this;
    return this.client.Sources.get( id ).then( function( resp ) {
        
        let url=that.client.apiUrl+'/cc/api/accountProfile/bulkUpdate/'+resp.connectorAttributes.cloudExternalId;
        let name = resp.name;
        return that.client.post(url, [profile]).then( function (resp) {
            console.log(`Updated account profile for ${name}`);
            return Promise.resolve(resp.data);            
        }
        , function (err) {
            console.log('--AccountProfiles.update: failure--');
            console.log(JSON.stringify(err));
            throw err;
            // return Promise.reject({
            //     url: url,
            //     status: err.response.status,
            //     statusText: err.response.statusText
            // });
        });
    }, function (err) {
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });
}

module.exports = AccountProfiles;