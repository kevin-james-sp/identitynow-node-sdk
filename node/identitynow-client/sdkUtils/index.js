const {JSONPath} = require('jsonpath-plus');

 
var client;
// var attributesToExclude;

function SDKUtils( client ) {
    
    this.client=client;
    this.surrounder='%%'; // Default symbols to denote a token
   
}

/* Substitute certain values for tokens
 * This uses JSONPath. See https://goessner.net/articles/JsonPath/ for details
 * We expect tokenDefinitions to be:
 * [
 *   {
 *     path: <jsonpath> e.g. $..username,
 *     token: <token _without_ surrounders> e.g. username. This will get translated to e.g. %%username%%
 *   }
 * ]
 * We will return an object like:
 * {
 *   object: <processed object>,
 *   tokens: [
 *     { token: <token>,
 *       value: <value from original object>
 *     }
 *   ]
 * }
 */
SDKUtils.prototype.tokenize = function tokenize (objectName, object = {}, tokenDefinitions = [] ) {

    let tokens=[];
    
    // Ok, now the magic..
    let ourObject=JSON.parse(JSON.stringify(object));

    let that=this;
    tokenDefinitions.forEach( tokenDef => {
        let found=JSONPath({path: tokenDef.path, json: ourObject, resultType: 'all'});
        if (found) {
            found.forEach( itm => {
                tok={};
                // tok.token=surrounder+tokenDef.token+surrounder;
                tok.token=this.surrounder+objectName+'_'+tokenDef.token+this.surrounder;
                tok.value=itm.value;
                tokens.push( tok );
                itm.parent[itm.parentProperty]=this.surrounder+objectName+'_'+tokenDef.token+this.surrounder;
            });
        }
    });
    return {
        object: ourObject,
        tokens: tokens
    }
}

SDKUtils.prototype.deTokenize = function deTokenize ( object = {}, tokens = [] ) {

    let json=JSON.stringify(object);
    tokens.forEach( token => {

        json=json.replace( this.surrounder+token.token+this.surrounder, token.value );

    });

    return JSON.parse(json);
}

module.exports=SDKUtils;
