var client;

function SPConfig( client ) {

    this.client = client;


}

SPConfig.prototype.import = function ( objects ) {

    if ( !objects ) {
        console.log( 'SPConfig.import : nothing to import' );
        return;
    }

    if ( !Array.isArray( objects ) ) {
        throw ( `SPConfig.import: must provide an array of objects` );
    }

    let url = this.client.apiUrl + '/beta/sp-config/import';
    let that = this;

    let fileContents = JSON.stringify( {
        objects: objects
    } );

    let payload = [{
        type: 'file',
        name: 'data',
        filename: 'importobjects.json',
        value: fileContents
    }]

    return this.client.post( url, payload, { multipart: true } )
        .then( resp => {
            console.log( 'SPConfig.list' );
            console.log( JSON.stringify( resp.data, null, 2 ) );
            return Promise.resolve( resp.data.items );
        } ).catch( err => {
            console.log( 'SPConfig.list: error' );
            console.log( JSON.stringify( err, null, 2 ) );
            throw {
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            };
        } );

}



module.exports = SPConfig;