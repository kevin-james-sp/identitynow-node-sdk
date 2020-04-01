var Sources = require('./sources');
var axios = require('axios');
// This will be in the format 'https://<tenant>.api.identitynow.com'
var apiUrl;
var client_id;
var client_secret;

var accesstoken;

var client;
var Sources;

function IdentityNowClient( config ) {

    this.apiUrl=config.apiUrl;

    if (config.apiUrl!=null) {
        this.client_id=config.client_id;
        this.client_secret=config.client_secret;    
    }
    
    this.Sources = new Sources( this );
    this.client = axios.create({
        baseURL: this.apiUrl
    });

    return this;
}

// Get the token by Client Credentials
IdentityNowClient.prototype.token = function( config ) {
    
    
    var client_id;
    var client_secret;
    // Make it possible to override the configured client id and secret
    if ( config != null ) {
        client_id = config.client_id;
        client_secret = config.client_secret;
    } else {
        // Check if we have a token for the default credentials; otherwise we'll need to go grab one
        if (this.accesstoken!=null) {
            return {
                token: this.accesstoken
            };
        }
        client_id = this.client_id;
        client_secret = this.client_secret;
    }

    return this.client.post('/oauth/token?grant_type=client_credentials&client_id='+
        client_id+'&client_secret='+client_secret).then( function ( resp ) {
            this.accesstoken=resp.data.access_token;
            return {
                status: resp.status,
                statusText: resp.statusText,
                token: this.accesstoken
            }
        }, function (err) {
            return {
                status: err.response.status,
                statusText: err.response.statusText
            }
        }
    );
}

IdentityNowClient.prototype.userToken = async function() {

    // Get the token by Authorization Token

}

function doGet( client, url, token ) {

    console.log('doGet: '+url);
    return client.get( url, {
        headers: {
            Authorization: 'Bearer '+token.token
        }
    })

}

IdentityNowClient.prototype.get = function( url ) {

    return this.token().then( doGet.bind(null, this.client, url) );

}

module.exports=IdentityNowClient;
