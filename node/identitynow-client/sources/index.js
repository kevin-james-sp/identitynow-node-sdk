var client;

function Sources( client ) {

    this.client=client;

}


Sources.prototype.get = async function get ( id ) {
    
    var token=await this.client.token();
    console.log('--token');
    console.log(token);
    console.log('--token--');

    console.log("sources.get("+id+")");
    console.log('token='+token);
    return {
        "a": "thing"
    }
}

module.exports = Sources;