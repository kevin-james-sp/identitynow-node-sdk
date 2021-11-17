var client;

function Connectors( client ) {

    this.client=client;

}

Connectors.prototype.list=function( ) {

    let url=this.client.apiUrl+'/cc/api/connector/list';
    let that=this;

    return this.client.get(url)
    .then( resp => {

        return resp.data.items;
        
    });

}

Connectors.prototype.get=function( id ) {

    if (!id) {
        throw {
            url: 'Connectors',
            status: -1,
            statusText: 'get: ID is required'
        };
    }
    
    return this.list()
    .then( connectors => {
        for ( conn  of connectors ) {
            if ( conn.id == id || conn.name == id ) {
                return conn;
            }
        }
        throw {
            url: 'Connectors',
            status: -1,
            statusText: `get: ${id} not found`
        };
    });

}

Connectors.prototype.export=function( id ) {

    let url=this.client.apiUrl+'/cc/api/connector/export';
    let that=this;

    if (!id) {
        throw {
            url: 'Connectors',
            status: -1,
            statusText: 'export: ID is required'
        };
    }
    
    return this.get( id )
    .then( connector => {
        return that.client.get( `${url}/${connector.id}`).then( resp => {            
            return resp.data;
        });
    }).catch( err => {
        throw err;
    });

}

Connectors.prototype.import=function( filename, contents ) {

    let url=this.client.apiUrl+'/cc/api/connector/import';
    let that=this;

    if (!filename) {
        throw {
            url: 'Connectors',
            status: -1,
            statusText: 'import: filename is required'
        };
    }
    
    if (!contents) {
        throw {
            url: 'Connectors',
            status: -1,
            statusText: 'import: file contents required'
        };
    }

    let payload = [{
        type: 'file',
        name: 'file',
        value: contents,
        filename: filename
    }];
    
    return this.client.post( url, payload, { multipart: true } ).then(
        success => {
            console.log( 'Import connector Success:' );
        }, err => {
            console.log( `Import connector failed: ${JSON.stringify( err )}` );
            return Promise.reject( {
                url: url,
                status: -1,
                statusText: `Import connector failed: ${err}`
            } )
        }

    );

}


module.exports = Connectors;