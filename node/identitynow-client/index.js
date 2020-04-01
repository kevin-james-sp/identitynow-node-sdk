const Sources = require('./sources');

var apiUrl;
var client_id;
var client_secret;

var token;

console.log('identitynow-client');

exports.Create = function ( config ) {

    this.apiUrl=config.apiUrl;

    if (config.apiUrl!=null) {
        this.client_id=config.client_id;
        this.client_secret=config.client_secret;    
    }
    Sources.configure( this );

}

exports.Sources=Sources;
