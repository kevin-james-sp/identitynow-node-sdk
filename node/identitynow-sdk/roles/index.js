var client;
// var attributesToExclude;

function Roles( client ) {
    
    this.client=client;
    this.attributesToExclude=['id', 'created', 'modified', 'synced', 'pod', 'org'];


}

Roles.prototype.getPage=function(off, lst) {
        
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

    let url=this.client.apiUrl+'/v3/search/roles?limit='+limit+'&offset='+offset+'&count=true';
    let payload={
        queryType: "SAILPOINT",
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
        console.log(err);
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });


}


Roles.prototype.list = function list () {

    return this.getPage();

}

Roles.prototype.get = function get ( id, options ) {
    
    let url=this. client.apiUrl+'/v3/search/roles';
    let payload={
        "queryType": "SAILPOINT",
        "query": {
          "query": id
        }
    }
    
    var that=this;
    return this.client.post(url, payload)
        .then( function (resp) {
            if (resp.data.length==0) {
                return Promise.reject({
                    url: url,
                    status: -1,
                    statusText: "Role '"+id+"' not found"
                });
            }
            if (resp.data.length>1) {
                return Promise.reject({
                    url: url,
                    status: -1,
                    statusText: "Multiple results for Role '"+id+"'"
                });
            }
            let ret=resp.data[0];
            if (options!=null){
                // Clean the source
                if (options.clean) {
                    ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                        ( that.attributesToExclude.includes(k) ? undefined : v) )
                    );
                }
                if (options.export) {
                }
            }
            return Promise.resolve(ret);
        },
        function (err) {
            console.log('roles.get');
            console.log(err);
            return Promise.reject(err);
        }
    );
}

module.exports = Roles;