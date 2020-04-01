var client;

exports.configure = function( client ) {

    console.log("sources.constructor");
    this.client=client;

}

exports.get = function ( id ) {

    console.log("sources.get("+id+")");
    console.log('token='+this.client.token);
    return {
        "a": "thing"
    }
}