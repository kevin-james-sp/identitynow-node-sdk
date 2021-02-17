var axios = require( 'axios' );
var FormData = require( 'form-data' );
var http = require( 'http' );
var jwtDecode = require( 'jwt-decode' );
var open = require( 'open' );
var qs = require( 'querystring' );

var AccessProfiles = require( './accessprofiles' );
var Brandings = require( './brandings' );
var AccountProfiles = require( './accountprofiles' );
var Clusters = require( './clusters' );
var CorrelationConfigs = require( './correlationconfigs' );
var Entitlements = require( './entitlements' );
var Identities = require( './identities' );
var IdentityProfiles = require( './identityprofiles' );
var Roles = require( './roles' );
var Rules = require( './rules' );
var Schemas = require( './schemas' );
var SDKUtils = require( './sdkUtils' );
var Sources = require( './sources' );
var Tags = require( './tags' );
var Transforms = require( './transforms' );
var VirtualAppliances = require( './virtualappliances' );

var QueryString = require( 'querystring' );

var url = require( 'url' );

var config;

// This will be in the format 'https://<tenant>.api.identitynow.com'
var apiUrl;
var authorizationUrl;
var tokenUrl;

var accesstoken;

var jwtRefreshToken;
var jwtExpires;

var pod;
var tenant;

var client;

var IdentityNowClient = function ( config ) {

    this.config = config;

    this.apiUrl = 'https://' + this.config.tenant + '.api.identitynow.com';
    this.webUrl = 'https://' + this.config.tenant + '.identitynow.com';
    this.authorizationUrl = 'https://' + this.config.tenant + '.identitynow.com/oauth/authorize';
    this.tokenUrl = this.apiUrl + '/oauth/token';

    // If we were initialized from a server NodeJS app which has already done the OAuth2 dance
    if ( config.userToken ) {
        this.parseToken( config.userToken );
    }
    if ( config.userRefreshToken ) {
        this.jwtRefreshToken = config.userRefreshToken;
    }

    this.AccessProfiles = new AccessProfiles( this );
    this.Brandings = new Brandings( this );
    this.AccountProfiles = new AccountProfiles( this );
    this.Clusters = new Clusters( this );
    this.CorrelationConfigs = new CorrelationConfigs( this );
    this.Entitlements = new Entitlements( this );
    this.Identities = new Identities( this );
    this.IdentityProfiles = new IdentityProfiles( this );
    this.Roles = new Roles( this );
    this.Rules = new Rules( this );
    this.Schemas = new Schemas( this );
    this.SDKUtils = new SDKUtils( this );
    this.Sources = new Sources( this );
    this.Tags = new Tags( this );
    this.Transforms = new Transforms( this );
    this.VirtualAppliances = new VirtualAppliances( this );
    this.client = axios.create( {
        baseURL: this.apiUrl
    } );


    // Uncomment this to check outbound REST requests
    // this.client.interceptors.request.use(x => {

    //     const headers = {
    //         ...x.headers.common,
    //         ...x.headers[x.method],
    //         ...x.headers
    //     };
    
    //     ['common','get', 'post', 'head', 'put', 'patch', 'delete'].forEach(header => {
    //         delete headers[header]
    //     })
    
    //     const printable = `${new Date()}
    //     Request: ${x.method.toUpperCase()} ${x.url}
    //     ${ JSON.stringify(headers, null, 2)}
    //     ${ JSON.stringify( x.data) }`
    //     console.log(printable);
    //     console.log('-------------\n\n\n\n\n');
    
    //     return x;
    // })

    //////////////////////////////////////////////////// This broke things for me..
    // this.client.interceptors.response.use( response => {
    //     console.log( 'Response:', JSON.stringify( response, null, 2 ) )
    //     return response
    // } )

    return this;
}

IdentityNowClient.prototype.parseToken = function ( token ) {
    this.accesstoken = token;
    // Parse out some important data. Well, some of it is important (expiry time) but some
    // of it is just a pain to get from elsewhere (pod, tenant)
    let decoded = jwtDecode( token );
    this.tenant = decoded.org;
    this.pod = decoded.pod;
    this.jwtExpires = decoded.exp;

}

IdentityNowClient.prototype.token = function ( overrideconfig = [] ) {

    let expired = ( this.jwtExpires != null && this.jwtExpires < ( Date.now() / 1000 ) );
    // Do we have a current token, and not wanting to regenerate?
    if ( this.accesstoken != null && !expired && !overrideconfig.regenerate ) {

        return Promise.resolve( this.accesstoken );

    }

    if ( expired ) {
        return this.getClientToken( { refresh: true } );
    }

    if ( this.config.userAuthenticate ) {

        var that = this;
        this.accesstoken = this.getUserToken().then( function ( ok ) {
            if ( ok ) {
                return Promise.resolve( that.accesstoken )
            }
        }, err => {
            console.error( 'User authentication failed: ' + err );
            return Promise.reject( err );
        } );
        return Promise.resolve( this.accesstoken );
    } else {
        this.accesstoken = this.getClientToken( overrideconfig );
        return Promise.resolve( this.accesstoken );
    }

}

// Get the token by Client Credentials
IdentityNowClient.prototype.getClientToken = function ( overrideconfig = [] ) {

    var client_id;
    var client_secret;

    // Check if we have a token for the default credentials; otherwise we'll need to go grab one
    if ( this.accesstoken != null && !overrideconfig.refresh ) {
        return Promise.resolve( this.accesstoken );
    }

    // Make it possible to override the configured client id and secret
    client_id = overrideconfig.client_id || this.config.client_id;
    client_secret = overrideconfig.client_secret || this.config.client_secret;

    // build the URL. this could be client_credentials, or a refresh token
    let url = '/oauth/token?grant_type=';
    url += ( overrideconfig.refresh ? 'refresh_token' : 'client_credentials' );
    url += '&client_id=' + client_id + '&client_secret=' + client_secret;
    if ( overrideconfig.refresh ) {
        url += '&refresh_token=' + this.jwtRefreshToken;
    }
    return this.client.post( url ).then( resp => {
        this.parseToken( resp.data.access_token );
        // console.log(this.accesstoken);
        return Promise.resolve( this.accesstoken );
    }, err => {
        console.log(err);
        console.log( `idnclient.error: ${JSON.stringify( err )}` );
        return Promise.reject( {
            status: err.response.status,
            statusText: err.message
        } );
    }
    );
}


IdentityNowClient.prototype.getUserToken = function () {

    let that = this;

    return this.getJWTToken().then( resp => {
        that.accesstoken = resp.access_token;
        that.jwtRefreshToken = resp.refresh_token;
        that.jwtExpires = new Date();
        that.jwtExpires.setSeconds( that.jwtExpires.getSeconds() + resp.expires_in );
        console.debug( 'JWT Token expires at: ' + that.jwtExpires );
        return Promise.resolve( true );
    } );

}


IdentityNowClient.prototype.getJWTToken = function ( callback ) {
    return new Promise( ( resolve, reject ) => {

        // spin up local http server to receive auth code callback

        const server = http.createServer( ( req, res ) => {
            const parsedUrl = url.parse( req.url, true );
            const queryAsObject = parsedUrl.query;
            const authCode = queryAsObject.code;

            // Configure parameters for our token call
            let config = {
                grant_type: 'authorization_code',
                client_id: this.config.client_id,
                client_secret: this.config.client_secret,
                code: authCode,
                redirect_uri: this.config.callbackURL
            }

            // go get our token
            this.client.post( '/oauth/token', null, {
                params: config
            } ).then( resp => {
                resolve( resp.data );
            }, function ( err ) {
                reject( {
                    status: err.response.status,
                    statusText: this.cerr.response.statusText || err.response.data.message
                } );
            } );

            console.log( 'responding..' );
            // Close the browser
            res.write( "<script>close()</script>" );
            res.end();

            req.connection.end(); // close the socket
            req.connection.destroy(); // close it really
            server.close(); // close the server
            console.log( 'server closed' );
        } ).listen( 5000 );

        // opens browser to the authorizationURL
        let parms = {
            response_type: 'code',
            client_id: this.config.client_id,
            scope: 'sp:scopes:all',
            redirect_uri: this.config.callbackURL
        }

        let target = this.authorizationUrl + '?' + qs.stringify( parms );
        console.log( `target=${target}` );
        try {
            open( target );
        } catch ( err ) {
            console.log( 'err' );
            console.log( err );
            reject( err );
        }

        console.log( 'Please check your web browser and (if necessary) authenticate there' );
    } );
}

IdentityNowClient.prototype.get = function ( url, retry ) {

    let that = this;

    return this.token().then(
        resp => {
            return that.client.get( url, {
                headers: {
                    Authorization: 'Bearer ' + resp
                }
            } ).then(
                success => {
                    return success
                },
                err => {
                    if ( err.response.status == 404 ) {
                        return Promise.reject( {
                            url: url,
                            status: -1,
                            statusText: 'URL Not found'
                        } )
                    }
                    return Promise.reject( {
                        url: url,
                        status: -1,
                        statusText: err.message
                    } )
                }
            );
        },
        err => {
            if ( err.response.status == 401 && !retry ) {
                // 401. Not a retry. Invalidate the token and try once more
                that.accesstoken = null;
                return get( url, true );
            }
            console.log( err );
            console.log( '------Get ERRROR-------' );
            return Promise.reject( err );
        } );

}

IdentityNowClient.prototype.delete = function ( url ) {

    let that = this;

    return this.token().then( function ( resp ) {
        return that.client.delete( url, {
            headers: {
                Authorization: 'Bearer ' + resp
            }
        } );
    } );

}

IdentityNowClient.prototype.post = function ( url, payload, options = {}, retry ) {

    let that = this;

    return this.token().then( function ( resp ) { // token success
        config = {
            headers: {
                Authorization: 'Bearer ' + resp
            }
        };
        if ( options.formEncoded ) {
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded';
            payload = QueryString['stringify']( payload );
        }
        if ( options.multipart ) {
            /*
             Multipart data must be sent to us differently
             Instead of sending us an object (name/value pairs), we get an array of 
             { type, name, value, .... } objects
             that way we can handle special cases like type=file, or any others we come across
            */
            var formData = new FormData();
            payload.forEach( entry => {
                switch ( entry.type ) {
                    case 'file': {
                        formData.append( entry.name, entry.value, {
                            filename: entry.filename,
                            contentType: 'application/octet-stream'
                        } );
                        break;
                    }
                    default: {
                        formData.append( entry.name, entry.value );
                    }
                }
            } );
            payload = formData.getBuffer();
            config.headers = { ...config.headers };
            config.headers['Content-Type']=formData.getHeaders()['content-type'];

        }
        return that.client.post( url, payload, config )
            .then( resp => { // post success
                return Promise.resolve( resp );
            }, function ( err ) { //post failure
                if ( err.response ) {

                    if ( err.response.status == 400 ) {
                        throw {
                            url: url,
                            detailcode: err.response.data.detailCode || err.response.data.error_code,
                            messages: err.response.data.messages,
                            trackingId: err.response.data.trackingId,
                            status: 400,
                            statusText: err.response.data.formatted_msg || err.response.data.message
                        };
                    } else if ( err.response.status == 401 && !retry ) {
                        // 401. Not a retry. Invalidate the token and try once more
                        that.accesstoken = null;
                        return post( url, payload, options, true );
                    } else {
                        throw {
                            url: url,
                            status: err.response.status,
                            statusText: err.response.statusText || err.response.data.message,
                            data: err.response.data
                        };
                    }
                }
                // Not an HTTP error response. :shrug:
                throw err;
            } )
    }
        , function ( err ) { // token failure
            return Promise.reject( err );
        } );

}

IdentityNowClient.prototype.put = function ( url, payload, options, retry ) {

    let that = this;

    return this.token().then( function ( resp ) { // token success
        headers = {
            headers: {
                Authorization: 'Bearer ' + resp
            }
        };
        if ( options && options.formEncoded ) {
            headers['Content-Type'] = 'application/x-www-form-urlencoded';
            payload = QueryString['stringify']( payload );
        }
        return that.client.put( url, payload, headers )
            .then( resp => { // post success
                return Promise.resolve( resp );
            }, function ( err ) { //post failure
                if ( err.response.status == 400 ) {
                    return Promise.reject( {
                        url: url,
                        detailcode: err.response.data.detailCode,
                        messages: err.response.data.messages,
                        trackingId: err.response.data.trackingId,
                        status: 400,
                        statusText: err.response.data.messages[0].text
                    } );
                } else if ( err.response.status == 401 && !retry ) {
                    // 401. Not a retry. Invalidate the token and try once more
                    that.accesstoken = null;
                    return put( url, payload, options, true );
                } else {
                    return Promise.reject( {
                        url: url,
                        status: err.response.status,
                        statusText: this.cerr.response.statusText || err.response.data.message
                    } );
                }
            }
            );
    }
        , function ( err ) { // token failure
            return Promise.reject( err );
        } );

}

module.exports = IdentityNowClient;
