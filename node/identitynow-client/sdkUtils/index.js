var client;
// var attributesToExclude;

function SDKUtils( client ) {
    
    this.client=client;
    this.isV2=true;
    
}

/* Look for the type and name of a warning
* If we find it, get the decision
*/

SDKUtils.prototype.isv2 = function( ) {
    return this.isv2;
}

module.exports=SDKUtils;
