var axios = require('axios');

var client;

function Rules( client ) {

    this.client=client;
    
}


Rules.prototype.getPage=function(off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;

    let url=this.client.apiUrl+'/cc/api/rule/list?start='+offset+'&limit='+limit+'&count=true';
    let that=this;

    return this.client.get(url)
        .then( function (resp) {
        count=resp.data.count;
        list=list.concat(resp.data.items);
        offset+=resp.data.count;
        // No total count in header; rely on data.count being less than limit to indicate we got everything
        if (resp.data.count==limit) {
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


Rules.prototype.list = function list () {

    return this.getPage();

}

Rules.prototype.getByName = function ( name, options ){

    // TODO: This is a horrible way to do it. It does a list and then
    // potentially another GET. 

    let that=this;
    return this.list().then( function( list ){
        let source;
        if (list!=null) {
            let foundRule;
            for( let rule of list) {
                if (rule.name==(name)) {
                    source=rule;
                }
            };
        }
        if (source) {
            return Promise.resolve( source );
        }
        console.log('rule not found');
        return Promise.reject({
            url: 'Rules',
            status: -1,
            statusText: 'Rule not found'
        });
    }, function( err ){
        return Promise.reject( err );
    });
}

Rules.prototype.get = function get ( id, options = [] ) {
    
    let url=this.client.apiUrl+'/cc/api/rule/get/'+id;
    
    let that=this;
    
    return this.client.get(url)
    .then( resp => {
        if (options==null || !options.clean) {
            return Promise.resolve(resp.data);
        }
        // Clean the source
        let ret={
            source: JSON.parse(JSON.stringify(resp.data, (k,v) => 
            ( (k === 'id') || (k === 'created') || (k === 'modified') ) ? undefined : v)
            )
        };
        
        return Promise.resolve(ret);
    } );
}

module.exports = Rules;