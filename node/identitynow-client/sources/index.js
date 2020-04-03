var client;

function Sources( client ) {

    this.client=client;


}

Sources.prototype.getPage=function(off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=10;
    console.log()

    let url=this.client.apiUrl+'/beta/sources?limit='+limit+'&offset='+offset+'&count=true';
    let that=this;

    return this.client.get(url)
        .then( function (resp) {
        count=resp.headers['x-total-count'];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        offset+=resp.data.length;
        if (list.length<count) {
            console.log('next page');
            return that.getPage(offset, list);
        }
        return Promise.resolve(list);
    }, function (reject) {
        console.log('getPage.reject');
        console.log(reject);
    });


}


Sources.prototype.list = function list () {

    return this.getPage();

}

Sources.prototype.get = function get ( id ) {
    
    let url=this.client.apiUrl+'/beta/sources/'+id;
    
    return this.client.get(url)
        .then( function (resp) {
            return Promise.resolve(resp.data);
        } );
}

module.exports = Sources;