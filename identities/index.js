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
    if ( options && options.useV2 ) {
        return this.getv2( id, options );
    } else {
        return this.getv3( id, options );
    }
}

Identities.prototype.getv2 = function getv2 ( id, options ) {
    
    let url=this.client.apiUrl+'/v2/identities';
    if (id) {
        url+='/'+id;
    }

    return this.client.get(url)
        .then( function (resp) {
            return resp.data;
        },
        function ( err ) {
            return Promise.reject({
                url: url,
                status: -1,
                statusText: err.response.data.message
            });
        }
    );
}

Identities.prototype.getv3 = function getv3 ( id, options ) {
    
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

/**
 * send invites
 * @param {} users An array of email addresses
 */
Identities.prototype.invite = function invite ( users ) {
    
    let promise = this.getv2( null, null ); // Because invite is a CC api and needs the old id

    promise = promise.then( identities => {
        let ids=[];
        identities.forEach( identity => {
            if (users.includes(identity.email)) {
                ids.push(identity.id);
            }
        });
        if (ids.includes(null)) {
            console.log(`Identities.invite: one or more IDs is null: ${ids}` );
            throw {
                url: 'Identities.invite',
                status: -1,
                statusText: `One or more IDs is null`
            }
        }
        if (ids.length==0) {
            console.log(`Couldn't find any users to invite on list` );
            console.log(users);
            throw {
                url: 'Identities.invite',
                status: -1,
                statusText: `Couldn't find any users to invite on list`
            }
        }
        let url = this.client.apiUrl+'/cc/api/user/invite?'
        let first=true;
        ids.forEach( id => {
            if (first) {
                first=false;
            } else {
                url+='&'
            }
            url+=`ids=${id}`;
        });
        console.log(url);
        return this.client.post( url ).then( result => {
            console.log(result.data);
            return ids;
        });
    });
    return promise;

}

/**
 * grant Admin privileges to users
 * @param {} users An array of email addresses
 */
Identities.prototype.grantAdmin = function grantAdmin ( users ) {
    
    let promise = this.getv2( null, null ); // Because currentState.config.threadID is a CC api and needs the old id

    promise = promise.then( identities => {
        let ids=[];
        identities.forEach( identity => {
            if (users.includes(identity.email)) {
                ids.push(identity.id);
            }
        });
        if (ids.includes(null)) {
            console.log(`Identities.grantAdmin: one or more IDs is null: ${ids}` );
            throw {
                url: 'Identities.grantAdmin',
                status: -1,
                statusText: `One or more IDs is null`
            }
        }
        if (ids.length==0) {
            console.log('No identities found to grant admin to');
            console.log(`users: ${users}`);
            identities.forEach( identity => {
                console.log(identity.email);
            });
            throw {
                url: 'Identities.grantAdmin',
                status: -1,
                statusText: `No identities found to grant admin to`
            }
        }
        let url = this.client.apiUrl+'/cc/api/user/updatePermissions?'
        let first=true;
        ids.forEach( id => {
            if (id!=null) {
                if (first) {
                    first=false;
                } else {
                    url+='&'
                }
                url+=`ids=${id}`;
            }
        });
        url+='&isAdmin=1&adminType=ADMIN';
        console.log(url);
        return this.client.post( url ).then( result => {
            return ids;
        });
    });
    return promise;

}

module.exports = Identities;