const JSZip = require( 'jszip' );

var client;

function IdentityAttributes( client ) {

    this.client = client;


}

IdentityAttributes.prototype.list = function list() {

    let url = this.client.apiUrl + '/cc/api/identityAttribute/list';
    let that = this;


    return this.client.get( url )
        .then( function ( resp ) {
            let list = [];
            resp.data.forEach( function ( itm ) {
                list.push( itm );
            } );
            return Promise.resolve( list );
        }, function ( reject ) {
            console.log( reject );
            return Promise.reject( reject );
        } );


}

// IdentityAttributes.prototype.get = function get( id, options ) {

//     let url = this.client.apiUrl + '/cc/api/identityAttroinbite/get/' + id;

//     let that = this;
//     return this.client.get( url )
//         .then( function ( resp ) {
//             if ( options == null || !options.clean ) {
//                 return Promise.resolve( resp.data );
//             }
//             // Clean the source
//             let ret = JSON.parse( JSON.stringify( resp.data, ( k, v ) =>
//                 ( ( k === 'id' ) || ( k === 'created' ) || ( k === 'modified' )
//                     || ( k == 'externalId' ) || ( k == 'lastUpdated' ) || ( k == 'lastAggregated' ) || ( k == 'sinceLastAggregated' )
//                     || ( k == 'applicationId' ) || ( k == 'applicationName' ) || ( k == 'externalName' )
//                     || ( k == 'credentialServiceId' )
//                 ) ? undefined : v )
//             );

//             if ( options.tokenize ) {
//                 return that.client.SDKUtils.tokenize( ret.name, ret, options.tokens );
//             }

//             return ret;
//         }, function ( err ) {
//             console.log( '---rejected---' );
//             console.log( err.response.statusText );
//             return Promise.reject( {
//                 url: url,
//                 status: err.response.status,
//                 statusText: err.response.statusText
//             } );
//         } );
// }

/**
 * Creates a new Identity Attribute
 * 
 */
IdentityAttributes.prototype.create = function ( attr ) {

    let url = this.client.apiUrl + '/cc/api/identityAttribute/create';
    let that = this;
    
    console.log( 'IdentityAttributes.create' );
    // console.log( profile );
    if ( !profile.name ) {
        throw {
            url: 'IdentityAttribute.create',
            status: -1,
            statusText: 'Attribute must have a name'
        };
    }
    return this.client.post( url, attr ).then( response => {
        console.log(`Identity Attribute created: ${attr.name}`);
        return response;
    });
       
}

module.exports = IdentityAttributes;