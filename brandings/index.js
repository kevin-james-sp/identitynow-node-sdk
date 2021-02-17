const { stringify } = require( 'querystring' );
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
    console.log(url);
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
Brandings.prototype.update = function ( newBranding ) {

    console.log(newBranding);

    if ( !newBranding.name ) {
        console.log('updating default branding');
        newBranding.name='default';
    }

    let that = this;

    return this.get( newBranding.name ).then( branding => {

        Object.keys(branding).forEach( key => {
            if (newBranding[key]) {
                branding[key]=newBranding[key];
            }
        });
        console.log('--New Branding--');
        console.log(branding);
        console.log('----------------');
        let url = `${this.client.apiUrl}/cc/api/branding/update?${querystring.stringify(branding)}`;
        console.log(url);
        // AXIOS has a bug (or maybe IDN does) that something says the response is gzipped but it's actually text
        // so for this we turn off decompression

        return that.client.post( url, null, { noDecompress: true } ).then( ok => {
            return 'Branding updated sucessfully';
        });
    } )

}



module.exports = Brandings;