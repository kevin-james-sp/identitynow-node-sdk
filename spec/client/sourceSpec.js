const idnClient=require('../../index.js');
const config=require('./config.js');

console.log('start');
var myconfig = { userAuthenticate: true };
Object.assign( myconfig, config.config_api );
var client= idnClient.Create( myconfig );
console.log('end');

describe("Sources", function(){


    it("Should return a list", async function() {
        
        let sources=await client.Sources.list();        

        sources.forEach( function (itm) {
            console.log(itm.id+' : '+itm.name+' ( '+itm.description+' )');
        });
    }, 30000);
});