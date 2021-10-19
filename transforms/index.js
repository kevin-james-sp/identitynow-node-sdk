var client;

function Transforms( client ) {

    this.client = client;


}

function clean( xform ) {
    return JSON.parse( JSON.stringify( xform, ( k, v ) => {
        return ( ( k === 'id' ) || ( k === 'created' ) || ( k === 'modified' ) ) ? undefined : v;
    }));
}

Transforms.prototype.list = function ( options = {} ) {

    let url = this.client.apiUrl + '/v3/transforms';
    let that = this;

    return this.client.get( url ).then( resp => {
        let ret = [];
        resp.data.forEach( xform => {
            if ( !xform.internal || !options.excludeInternal ) {
                if ( options.clean ) {
                    ret.push( clean( xform ) );
                } else {
                    ret.push( xform );
                }
            }
        } )
        return ret;
    }
    , function ( err ) {
        console.log( 'Transform' );
        console.log( JSON.stringify( err, null, 2 ) );
        throw {
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        }
    } );

}

Transforms.prototype.get = function get( transformName, options = {} ) {

    if ( !transformName ) {
        throw ( 'Transform.get: name is required' );
    }

    let url = `${this.client.apiUrl}/v3/transforms?name=${transformName}`;

    return this.client.get( url )
        .then(
            function ( resp ) {
                if ( options.clean ) {
                    return clean( resp.data );
                }
                return resp.data;
            }
            , function ( err ) {
                return Promise.reject( {
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                } )
            }
        );
}

Transforms.prototype.create = function ( xform ) {

    let url = this.client.apiUrl + '/v3/transforms';

    // Sanity check
    if ( xform == null ) {
        throw {
            url: url,
            module: 'Transforms.create',
            status: -1,
            statusText: 'No transform specified for creation'
        };
    }
    if ( xform.name == null ) {
        throw {
            url: url,
            module: 'Transforms.create',
            status: -1,
            statusText: 'No name specified for creation'
        };
    }
    if ( xform.type == null ) {
        throw {
            url: url,
            module: 'Transforms.create',
            status: -1,
            statusText: 'No type specified for creation'
        };
    }

    return this.client.post( url, xform ).then( resp => {
        return {
            "result": "ok",
            "id": resp.data.id,
            "name": resp.data.name
        };
    }, err => {
        console.log( JSON.stringify( err ) );
        if ( err.detailcode && err.detailcode.startsWith( '400.1.409' ) ) {
            return {
                result: "warn",
                message: `Transform ${xform.name} already exists`,
                name: xform.name
            };
        }
        console.log( `Transform create failed: ${xform.name}` );
        console.log( JSON.stringify( err, null, 2 ) );
        throw {
            url: url,
            module: 'Transforms.create',
            status: err.detailCode
            // statusText: formatted_msg
        }
    } );

}



module.exports = Transforms;