const JSZip=require('jszip');

var client;

function Entitlements( client ) {

    this.client=client;


}

Entitlements.prototype.getPage=function( query, off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;

    let url=this.client.apiUrl+'/v3/search?limit='+limit+'&offset='+offset+'&count=true';
    let that=this;

    return this.client.post(url, query)
        .then( function (resp) {
        count=resp.headers['x-total-count'];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(query, offset, list);
        }
        return Promise.resolve(list);
    }, function ( err ) {
        console.log('getPage.reject');
        console.log( err );
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });


}


Entitlements.prototype.get = function get ( id ) {

    let query={
        query: {
            query: id
        },
        "indices": ["entitlements"],
        "sort": ["id"]
    }

    let url=this.client.apiUrl+'/v3/search?limit='+limit+'&offset='+offset+'&count=true';
    let that=this;

    return this.client.post(url, query)
    .then( function (resp) {
        count=resp.headers['x-total-count'];
        if ( count>1 ) {
            throw "Entitlements.get returns multiple results"
        }
        return resp.data[0];
        
    });

}

Entitlements.prototype.list = function list ( parms ) {

    let query={
        query: {
            query: '*'
        },
        "indices": ["entitlements"],
        "sort": ["id"]
    }

    let sourceId=Promise.resolve();
    if ( parms ){
        // mess around with parms
        // convert sourceName to source ID
        if (parms.sourceId) {
            sourceId=sourceId.then( ()=> {
                console.log('resolving to parms.sourceId');
                return parms.sourceId;
            });
        } else if  (parms.sourceName) {
            let that=this;
            sourceId=sourceId.then( ()=> {
                console.log( 'resolving Source name '+parms.sourceName);
                return this.client.Sources.getByName(parms.sourceName).then( source => {
                    console.log('resolving to source.id from name');
                    if ( source ) {
                        return Promise.resolve(source.id);
                    } else {
                        return Promise.reject( {
                            url: 'Entitlements.list',
                            status: 1,
                            statusText: 'Entitlement Source \''+parms.sourceName+'\' not found'
                        });
                    }
                }, err=> {
                    console.log('entprotlist');
                    console.log(err);
                    return Promise.reject( err );
                })
            }, function( err ){
                return Promise.reject( err );
            });
        };
    }
    return sourceId.then( sourceId=> {
        // Pass sourceId to query generator promise (if defined)
        console.log('sourceId.then: sourceId='+sourceId);
        return Promise.resolve('source.id:\"'+sourceId+'\"');
    }, err=> {
        console.log('no sounrce found');
        return Promise.reject( {
            url: 'Entitlements.list',
            status: 1,
            statusText: 'Source '+parms.sourceName+' not found '
        });
    }).then( queryString => {
        if (!parms|| !parms.entitlements) return Promise.resolve( queryString );
        // now add all the attribute values to a string
        let entStr='';
        let first=true;
        parms.entitlements.forEach( ent => {
          if (first) {
            first=false;
            entStr='(';
          } else {
            entStr+=' OR ';
          }
          entStr+='\"'+ent+'\"';
        });
        if (!first) {
          entStr+=')';
        }
        if (queryString) {
            return Promise.resolve(queryString+' AND '+entStr);
        } else {
            return Promise.resolve(entStr);
        }
    }, err => {
        return Promise.reject( err );
    }).then( queryString => {
        console.log('Doing query: queryString='+queryString);
        if (queryString) {
            query.query.query=queryString;
        }
        return this.getPage( query );
    }, err => {
        console.log('err');
        return Promise.reject( err );
    });


}

module.exports = Entitlements;