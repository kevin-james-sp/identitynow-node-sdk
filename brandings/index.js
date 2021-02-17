const { stringify } = require( 'querystring' );
const { log } = require( 'grunt' );
const sortArray = require( 'sort-array' );
const querystring = require('querystring');
var client;

function Brandings( client ) {

    this.client = client;

}

Brandings.prototype.list = function () {

    let url = this.client.apiUrl + '/cc/api/branding/list';
    let that = this;

    return this.client.get( url )
        .then(
            function ( resp ) {
                console.log( 'Brandings.list' );
                return Promise.resolve( resp.data );
            }
            , function ( err ) {
                console.log( 'Branding' );
                console.log( JSON.stringify( err, null, 2 ) );
                return Promise.reject( {
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                } );
            } );

}

Brandings.prototype.get = function ( name ) {

    let url = `${this.client.apiUrl}/cc/api/branding/get?name=${encodeURI( name )}`;
    let that = this;

    return this.client.get( url )
        .then(
            function ( resp ) {
                console.log( 'Brandings.get' );
                return Promise.resolve( resp.data );
            }
            , function ( err ) {
                console.log( 'Branding' );
                console.log( err );
                console.log( JSON.stringify( err, null, 2 ) );
                return Promise.reject( {
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                } );
            } );

}

/**
 * Note: colors must be specified without the '#'
 * @param {} branding 
 */
Brandings.prototype.update = function ( branding ) {

    if ( !branding.name ) {
        throw ( 'branding name must be specified' );
    }

    let that = this;

    return this.get( branding.name ).then( oldBranding => {
        console.log( 'Old Branding:' )
        console.log( oldBranding );

        let newBranding = Object.assign( oldBranding, branding );
        console.log('--New Branding--');
        console.log(newBranding);
        console.log('----------------');
        let url = `${this.client.apiUrl}/cc/api/branding/update?${querystring.stringify(newBranding)}`;
        console.log(url);
        return that.client.post( url );
    } )

}



module.exports = Brandings;