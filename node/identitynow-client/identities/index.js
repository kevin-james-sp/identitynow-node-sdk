var client;

function Identities( client ) {

    this.client=client;


}

Identities.prototype.getPage=function(off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;
    console.log()

    let url=this.client.apiUrl+'/v3/search/identities?limit='+limit+'&offset='+offset+'&count=true';
    let payload={
        indicies: [ "identities" ],
        query: {
          query: "*"
        }
    }
    let that=this;

    return this.client.post(url, payload)
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
    }, function (err) {
        console.log('getPage.reject');
        console.log(reject);
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });


}


Identities.prototype.list = function list () {

    return this.getPage();

}

Identities.prototype.get = function get ( id, options ) {
    
    let url=this.client.apiUrl+'/v3/search/identities';
    let payload={
        "queryType": "SAILPOINT",
        "query": {
          "query": id
        }
    }
    
    return this.client.post(url, payload)
        .then( function (resp) {
            if (resp.data.length==0) {
                return Promise.reject({
                    url: url,
                    status: -1,
                    statusText: "Identity '"+id+"' not found"
                });
            }
            if (resp.data.length>1) {
                return Promise.reject({
                    url: url,
                    status: -1,
                    statusText: "Multiple results for Identity '"+id+"'"
                });
            }
            return resp.data[0];
        },
        function (err) {
            return Promise.reject(err);
        }
        );
}

module.exports = Identities;