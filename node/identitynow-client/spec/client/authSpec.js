const idnClient=require('../../index.js');
const config=require('./config.js');
var jwtDecode = require('jwt-decode');

xdescribe("Authentication", function(){

    it("The token should be granted for the client id/secret in the config", async function() {
        
        var client= idnClient.Create( config.config_api );
        
        var token = await client.token();

        var decoded=jwtDecode(token);
        
        expect(decoded.client_id).toBe(config.config_api.client_id);
        expect(decoded.authorities[0]).toBe('API');
    });

    it("The token should be granted for the client id/secret overriding the config settings", async function() {
        
        var client= idnClient.Create( config.config_api );
        
        var config_override = {
            client_id: 'eb6412ae-c214-48bf-a433-93dd87876323',
            client_secret: '5565ea630474039284c207848a33d7c68b9308efaf5e6243e7223ebb7f503810'
        };
        
        var token = await client.token( config_override );
        var decoded=jwtDecode(token);
        
        expect(decoded.client_id).toBe(config_override.client_id);
        expect(decoded.authorities[0]).toBe('API');
    });

    it("The module should return a 401 status", async function() {
        
        var client= idnClient.Create( config.config_api );
        
        var config_override = {
            client_id: 'badcafe',
            client_secret: 'b0b15dead'
        };
        
        var resp = client.token( config_override ).then( function(resp) {
            fail('Bad creds: This test should not resolve');
        }).catch( function(err) {
            expect(err.status).toBe(401);
        });
    });

    it("The token should be granted by user authentication (browser)", async function() {
        
        var myconfig = { userAuthenticate: true };
        Object.assign( myconfig, config.config_api );
        var client= idnClient.Create( myconfig );
        
        var token = await client.token();        
        var decoded=jwtDecode(token);

        expect(decoded.identity_id).not.toBe(null);
    }, 30000);
});