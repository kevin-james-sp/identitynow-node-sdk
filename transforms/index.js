var client;

function Transforms( client ) {

    this.client = client;


}

Transforms.prototype.list = function ( id ) {

    let url = this.client.apiUrl + '/beta/transforms/list';
    let that = this;

    return this.client.get( url )
        .then(
            function ( resp ) {
                console.log( 'Transforms.list' );
                console.log( JSON.stringify( resp.data, null, 2 ) );
                return Promise.resolve( resp.data.items );
            }
            , function ( err ) {
                console.log( 'Transform' );
                console.log( JSON.stringify( err, null, 2 ) );
                return Promise.reject( {
                    url: url,
                    status: err.response.status,
                    statusText: err.response.statusText
                } );
            } );

}

Transforms.prototype.get = function get( transformName ) {

    if (!transformName) {
        throw('Transform.get: name is required');
    }

    let url = `${this.client.apiUrl}/beta/transforms?name=${transformName}`;

    return this.client.get( url )
        .then(
            function ( resp ) {
                return Promise.resolve( resp.data );
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

    let url = this.client.apiUrl + '/beta/transforms/create';

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
        if ( err.data.error_code == 1009 || err.data.error_code == 1005 ) {
            return {
                result: "warn",
                message: `Transform ${xform.id} already exists`,
                name: xform.id
            };
        }
        console.log( `Transform create failed: ${xform.id}` );
        console.log( JSON.stringify( err, null, 2 ) );
        throw {
            url: url,
            module: 'Transforms.create',
            status: err.slpt_error_code
            // statusText: formatted_msg
        }
    } );

}



module.exports = Transforms;