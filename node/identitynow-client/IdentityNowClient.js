var axios = require('axios');
var http = require('http');
var open = require('open');
var qs = require('querystring');
var AccessProfiles = require('./accessprofiles');
var AccountProfiles = require('./accountprofiles');
var Clusters = require('./clusters');
var CorrelationConfigs = require('./correlationconfigs');
var Entitlements = require('./entitlements');
var Identities = require('./identities');
var IdentityProfiles = require('./identityprofiles');
var Roles = require('./roles');
var Sources = require('./sources');
var Schemas = require('./schemas');
var Tags = require('./tags');
var Transforms = require('./transforms');
var url = require('url');

var config;

// This will be in the format 'https://<tenant>.api.identitynow.com'
var apiUrl;
var authorizationUrl;
var tokenUrl;

var accesstoken;

var jwtRefreshToken;
var jwtExpires;

var client;

var IdentityNowClient=function( config ) {
    
    this.config=config;
    
    this.apiUrl='https://'+this.config.tenant+'.api.identitynow.com';
    this.authorizationUrl='https://'+this.config.tenant+'.identitynow.com/oauth/authorize';
    this.tokenUrl=this.apiUrl+'/oauth/token';
    
    // If we were initialized from a server NodeJS app which has already done the OAuth2 dance
    if (config.userToken) {
        this.accesstoken=config.userToken;
    }
    if (config.userRefreshToken) {
        this.jwtRefreshToken=config.userRefreshToken;
    }

    this.AccessProfiles = new AccessProfiles( this );
    this.AccountProfiles = new AccountProfiles( this );
    this.Clusters = new Clusters( this );
    this.CorrelationConfigs = new CorrelationConfigs( this );
    this.Entitlements = new Entitlements( this );
    this.Identities = new Identities( this );    
    this.IdentityProfiles = new IdentityProfiles( this );    
    this.Roles=new Roles( this );
    this.Sources = new Sources( this );
    this.Schemas = new Schemas( this );
    this.Tags = new Tags( this );
    this.Transforms = new Transforms( this );
    this.client = axios.create({
        baseURL: this.apiUrl
    });
    
    return this;
}

IdentityNowClient.prototype.token = function( overrideconfig ) {
    
    // Do we have a current token, and not wanting to regenerate?
    if ( this.accesstoken!=null && (overrideconfig==null || !(true==overrideconfig.regenerate)) ) {
                
        return Promise.resolve(this.accesstoken);
        
    }
    
    if ( this.config.userAuthenticate ) {
        
        var that=this;
        this.accesstoken=this.getUserToken().then( function ( ok )  {
            if (ok) {
                return Promise.resolve( that.accesstoken )
            }
        }, function ( err ) {            
            console.error('User authentication failed: '+err);
            return Promise.reject( err );
        });
        return Promise.resolve(this.accesstoken);
    } else {
        this.accesstoken=this.getClientToken( overrideconfig );
        return Promise.resolve(this.accesstoken);
    }
    
}

// Get the token by Client Credentials
IdentityNowClient.prototype.getClientToken = function( overrideconfig ) {
    
    var client_id;
    var client_secret;
    // Make it possible to override the configured client id and secret
    if ( overrideconfig != null ) {
        client_id = overrideconfig.client_id;
        client_secret = overrideconfig.client_secret;
    } else {
        // Check if we have a token for the default credentials; otherwise we'll need to go grab one
        if (this.accesstoken!=null) {
            return Promise.resolve(this.accesstoken);
        }
        client_id = this.config.client_id;
        client_secret = this.config.client_secret;
    }
    let url='/oauth/token?grant_type=client_credentials&client_id='+client_id+'&client_secret='+client_secret;
    return this.client.post(url).then( function ( resp ) {
            this.accesstoken=resp.data.access_token;
            return Promise.resolve(this.accesstoken);
        }, function (err) {
            return Promise.reject({
                status: err.response.status,
                statusText: err.response.statusText
            });
        }
    );
}


IdentityNowClient.prototype.getUserToken = function() {

    let that=this;

    return this.getJWTToken().then( function ( resp ) {
        that.accesstoken=resp.access_token;
        that.jwtRefreshToken=resp.refresh_token;
        that.jwtExpires=new Date();
        that.jwtExpires.setSeconds(that.jwtExpires.getSeconds()+resp.expires_in);
        console.debug('JWT Token expires at: '+that.jwtExpires);
        return Promise.resolve(true);
    });

}


IdentityNowClient.prototype.getJWTToken=function( callback ) {
    return new Promise((resolve, reject) => {
        
        // spin up local http server to receive auth code callback

        const server = http.createServer((req, res) => {
            const parsedUrl = url.parse(req.url, true);
            const queryAsObject = parsedUrl.query;
            const authCode = queryAsObject.code;

            // Configure parameters for our token call
            let config={
                grant_type: 'authorization_code',
                client_id: this.config.client_id,
                client_secret: this.config.client_secret,
                code: authCode,
                redirect_uri: this.config.callbackURL
            }

            // go get our token
            this.client.post('/oauth/token', null, {
                params: config
            }).then( function ( resp ) {
                resolve( resp.data );
            }, function (err) {
                reject({
                    status: err.response.status,
                    statusText: err.response.statusText
                });
            });

            console.log('responding..');
            // Close the browser
            res.write("<script>close()</script>");
            res.end();

            req.connection.end(); // close the socket
            req.connection.destroy(); // close it really
            server.close(); // close the server
            console.log('server closed');
        }).listen(5000);

        // opens browser to the authorizationURL
        let parms={
            response_type: 'code',
            client_id: this.config.client_id,
            scope: 'read write',
            redirect_uri: this.config.callbackURL
        }

        let target=this.authorizationUrl+'?'+qs.stringify(parms);
        try {
            open(target);
        } catch (err) {
            console.log('err');
            console.log(err);
            reject(err);
        }

        console.log('Please check your web browser and (if necessary) authenticate there');
    });
}

IdentityNowClient.prototype.get = function( url ) {
    
    let that=this;
    
    return this.token().then( function( resp ) {
        return that.client.get( url, {
            headers: {
                Authorization: 'Bearer '+resp
            }
        });
    }, function( err ){
        console.log('------Get ERRROR-------');
        console.log(err);
        return Promise.reject(err);
    });
    
}

IdentityNowClient.prototype.delete = function( url ) {
    
    let that=this;
    
    return this.token().then( function( resp ) {
        return that.client.delete( url, {
            headers: {
                Authorization: 'Bearer '+resp
            }
        });
    });
    
}

IdentityNowClient.prototype.post = function( url, payload ) {
    
    let that=this;
    
    return this.token().then( function( resp ) { // token success
        return that.client.post( url, payload, {
                headers: {
                    Authorization: 'Bearer '+resp
                }
            }).then( function ( resp ) { // post success
                return Promise.resolve( resp );
            }, function(err) { //post failure
                if (err.response.status==400) {                    
                    return Promise.reject({
                        url: url,
                        detailcode: err.response.data.detailCode,
                        messages: err.response.data.messages,
                        trackingId: err.response.data.trackingId,
                        status: 400,
                        statusText: err.response.data.messages[0].text
                    });
                } else {
                    return Promise.reject({
                        url: url,
                        status: err.response.status,
                        statusText: err.response.statusText
                    });
                }
            })
    }
    , function (err ) { // token failure
        return Promise.reject(err);
    });
    
}

module.exports=IdentityNowClient;
