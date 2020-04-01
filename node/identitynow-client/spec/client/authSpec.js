const idnClient=require('../../index.js');
const config=require('./config.js');
var jwtDecode = require('jwt-decode');

describe("Client Credential Auth: config", function(){
    it("The token should be granted for the client id/secret in the config", async function() {
        
        var client= idnClient.Create( config );
        
        var resp = await client.token( );
        expect(resp.status).toBe(200);

        var token=resp.token;
        var decoded=jwtDecode(token);
        
        expect(decoded.client_id).toBe(config.client_id);
        expect(decoded.authorities[0]).toBe('API');
    });
});

describe("Client Credential Auth: overide", function(){
    it("The token should be granted for the client id/secret overriding the config settings", async function() {
        
        var client= idnClient.Create( config );
        
        var config_override = {
            client_id: 'eb6412ae-c214-48bf-a433-93dd87876323',
            client_secret: '5565ea630474039284c207848a33d7c68b9308efaf5e6243e7223ebb7f503810'
        };
        
        var resp = await client.token( config_override );
        expect(resp.status).toBe(200);
        var token=resp.token;
        var decoded=jwtDecode(resp.token);
        
        expect(decoded.client_id).toBe(config_override.client_id);
        expect(decoded.authorities[0]).toBe('API');
    });
});

describe("Client Credential Auth: bad", function(){
    it("The response should return a 401 status", async function() {
        
        var client= idnClient.Create( config );
        
        var config_override = {
            client_id: 'badcafe',
            client_secret: 'b0b15dead'
        };
        
        var resp = await client.token( config_override );
        expect(resp.status).toBe(401);
    });
});