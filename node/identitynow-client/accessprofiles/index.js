const sortArray = require('sort-array');

var client;

function AccessProfiles( client ) {

    this.client=client;

}

AccessProfiles.prototype.getPage=function(off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;

    let url=this.client.apiUrl+'/beta/search/accessprofiles?limit='+limit+'&offset='+offset+'&count=true';
    let that=this;
    
    let payload={
        "queryType": "SAILPOINT",
        "query": {
            "query": "*"
        } 
    };

    return this.client.post(url, payload)
        .then( function (resp) {
        count=resp.headers['x-total-count'];
        list=list.concat(resp.data);
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(offset, list);
        }
        return Promise.resolve(list);
    }, function ( err ) {
        console.log('getPage.reject');
        console.log( url );
        console.log( err );
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });

}

AccessProfiles.prototype.listv3 = function list () {

    return this.getPage().then( theList => {
        return sortArray( theList, { by: 'name' } );
    }, err => {
        return Promise.reject( err );
    });

}



AccessProfiles.prototype.listv2=function() {
        
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

AccessProfiles.prototype.list=function( options ) {
    if ( options && options.useV2 ) {
        return this.listv2();
    } else {
        return this.listv3();
    }
}

AccessProfiles.prototype.get = function get ( tagId, options ) {

    if ( options && options.useV2 ) {
        return this.getv2( tagId, options );
    } else {
        return this.getv3( tagId, options );
    }
}
 
AccessProfiles.prototype.getv2 = function get ( tagId, options ) {
    let url=this.client.apiUrl+'/v2/access-profiles/'+tagId;
    
    var that=this;
    
    let ret;
    promise=this.client.get(url);
    
    promise = promise.then( resp => {
        ret=resp.data;
        return this.client.Identities.getv2(ret.ownerId).then(
            identity => {
                ret.ownerName=identity.alias;
                return Promise.resolve(ret);
            }, err => {
                return Promise.reject( err );
            }
        );
    }, err => {
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        })
    });

    promise = promise.then( resp => {
        return this.client.Entitlements.list( {
            sourceName: resp.sourceName,
            entitlements: resp.entitlements })
        .then( ents => {
            newEnts=[];
            ents.forEach( ent => {
                newEnts.push( {
                    attribute: ent.attribute,
                    value: ent.value
                });
            });
            resp.entitlements=newEnts;
            return resp;
        }, err => {
            return Promise.reject( {
                url: 'AccessProfile.get.Entitlements',
                status: -1,
                statsText: 'Get Entitlements Failed'
            })
        }

        )
    })

    promise = promise.then( ret => {
        let promise=Promise.resolve();
        if (options!=null){
            // Clean the source
            if (options.clean) {
                ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                    ( (k === 'id') || (k === 'created') || (k === 'modified')
                    || (k === 'sourceId') || (k === 'ownerId') ) ? undefined : v)
                );
            }
        }
        return Promise.resolve( ret );
    }, err => {
        return Promise.reject({
            url: url,
            status: err.response.status|-1,
            statusText: err.response.statusText|err.message
        })
    });

    return promise;
}

AccessProfiles.prototype.getv3 = function get ( tagId, options ) {
    
    // let url=this.client.apiUrl+'/v2/access-profiles/'+tagId;
    let url=this.client.apiUrl+'/beta/search/accessprofiles';
    let payload={
        queryType: "SAILPOINT",
        query: {
            query: tagId
        } 
    };
    
    var that=this;
    return this.client.post(url, payload)
    .then( function (resp) {
        let ret=resp.data[0];
        let promise=Promise.resolve();
        if (options!=null){
            // Clean the source
            if (options.clean) {
                ret=JSON.parse(JSON.stringify(ret, (k,v) => 
                    ( (k === 'id') || (k === 'created') || (k === 'modified') ) ? undefined : v)
                );
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

AccessProfiles.prototype.create = function( json, options = { useV2: true } ) {

    if ( options && options.useV2 ) {
        return this.createv2( json );
    } else {
        return this.createv3( json );
    }
}

AccessProfiles.prototype.createv2 = function( json ) {
    
    let url=this.client.apiUrl+'/v2/access-profiles';
    //TODO: Cache looked up identities
    // Sanity check
    if ( json==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No Access Profile specified for creation'
        });
    }
    if ( json.name==null ) {
        return Promise.reject({
            url: url,
            status: -1,
            statusText: 'No name specified for creation'
        });
    }
    
    let promise=Promise.resolve();
    
    if (json.ownerId==null) {
        promise=this.client.Identities.get( json.ownerName , { useV2: true} )
        .then( identity => {
            json.ownerId=identity.id;
            return Promise.resolve();
        }, err => {
            return Promise.reject( err );
        });
    }
    if (json.sourceId==null) {
        promise=promise.then( () => {
            return this.client.Sources.getByName( json.sourceName )
            .then( source => {
                json.sourceId=source.connectorAttributes.cloudExternalId;
                return Promise.resolve();
            }, err => {
                return Promise.reject( err );
            })
        }, err => {
            return Promise.reject( err );
        });
    }    
    return promise.then( () => {
        let allowedKeys=['name', 'description', 'ownerId', 'sourceId', 'entitlements', 'approvalSchemes',
        'revokeRequestApprovalSchemes', 'requestCommentsRequired', 'deniedCommentsRequired', 'disabled' ];
        Object.keys(json).forEach( key => {
            // Strip out attributes that aren't in Neil's example
            if (!allowedKeys.includes( key )) {
                delete json[key];
            }
        });
        console.log('-- Posting to access-profiles --');
        console.log(JSON.stringify(json, null, 2));
        return this.client.post(url, json);
    }, err => {
        return Promise.reject( err );
    });
    
}

AccessProfiles.prototype.createv3 = function( json ) {
    return Promise.reject( {
        url: 'AccessProfiles.createv3',
        status: -1,
        statusText: 'AccessProfile.create not in v3 API yet'
    });
}

module.exports = AccessProfiles;