var client;

function Sources( client ) {

    this.client=client;

}

Sources.prototype.list = function list () {

    let limit=100;
    let offset=0;

    var url=this.client.apiUrl+'/beta/sources?limit='+limit+'&offset='+offset+'&count=true';

    return this.client.get(url)
    .then( function (resp) {
        return Promise.resolve(resp.data);
    }).catch( function (err) {
        console.log(err);
        return Promise.resolve(err);
    });

}

Sources.prototype.get = async function get ( id ) {
    
    var token=await this.client.token();
    return {
        "a": "thing"
    }
}

module.exports = Sources;