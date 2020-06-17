const JSZip=require('jszip');

var client;

function IdentityProfiles( client ) {

    this.client=client;


}

IdentityProfiles.prototype.list = function list () {
        
    let url=this.client.apiUrl+'/cc/api/profile/list';
    let that=this;

    
    return this.client.get(url)
      .then( function (resp) {
        let list=[];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        return Promise.resolve(list);
    }, function (reject) {
        console.log(reject);
        return Promise.reject(reject);
    });


}

IdentityProfiles.prototype.get = function get ( id, options ) {
    
    let url=this.client.apiUrl+'/cc/api/profile/get/'+id;
    
    let that=this;
    return this.client.get(url)
        .then( function (resp) {
            if (options==null || !options.clean) {
                return Promise.resolve(resp.data);
            }
            // Clean the source
            let ret=JSON.parse(JSON.stringify(resp.data, (k,v) => 
                ( (k === 'id') || (k === 'created') || (k === 'modified') ) ? undefined : v)
            );
            // If we aren't exporting, just return the source
            if (!options.export) {
                return Promise.resolve(ret.source);
            }
            
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
}

module.exports = IdentityProfiles;