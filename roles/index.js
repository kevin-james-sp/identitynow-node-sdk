var client;
// var attributesToExclude;

function Roles( client ) {
    
    this.client=client;
    this.attributesToExclude=['id', 'created', 'modified', 'synced', 'pod', 'org', 'email', 'accessProfileCount', 'segmentCount'];


}

Roles.prototype.getPage=function( off, lst, options ) {
        
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

    let url=this.client.apiUrl+'/v3/search?limit='+limit+'&offset='+offset+'&count=true';
    let payload={
        queryType: "SAILPOINT",
        indices: [ "roles" ],
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
        throw{
            url: url,
            status: err.status,
            statusText: err.statusText || err.detailcode
        };
    });
    
    
}


Roles.prototype.list = function list ( options ) {
    
    return this.getPage().then( results => {
        if (options && options.clean ) {
            let ret=[];
            results.forEach( result => {
                ret.push(
                    JSON.parse(JSON.stringify(result, (k,v) => 
                        ( this.attributesToExclude.includes(k) ? undefined : v) )
                    )
                )
            });
            return ret;
        }
        return results;
    })
    
}

Roles.prototype.get = function get ( id, options ) {
    
    let url=this. client.apiUrl+'/v3/search';
    let payload={
        "queryType": "SAILPOINT",
        "indices": "roles",
        "query": {
            "query": id
        }
    }
    
    var that=this;
    return this.client.post(url, payload)
    .then( function (resp) {
        if (resp.data.length==0) {
            throw {
                url: url,
                status: -1,
                statusText: "Role '"+id+"' not found"
                };
            }
            if (resp.data.length>1) {
                throw {
                    url: url,
                    status: -1,
                    statusText: "Multiple results for Role '"+id+"'"
                };
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