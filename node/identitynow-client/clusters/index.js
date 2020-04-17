var client;

function Clusters( client ) {

    this.client=client;


}

Clusters.prototype.list=function( id ) {
        
    let url=this.client.apiUrl+'/cc/api/cluster/list';
    let that=this;

    return this.client.get(url)
        .then(
        function (resp) {
            return Promise.resolve(resp.data);
        }
        , function (err) {
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });

}

Clusters.prototype.getByName = function get (clusterName ) {
    
    return this.list()
        .then( 
        function (resp) {
            let item=resp.find( itm => itm.name==clusterName);
            if (item!=null) {
                return Promise.resolve(item);
            }
            return Promise.reject({
                status: -1,
                statusText: "Cluster '"+clusterName+"' not found"
            })
        }
        , function (err) {
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            })
        }
        );
}

// Clusters.prototype.create = function( xform ) {

//     // Sanity check
//     if ( xform==null ) {
//         return Promise.reject('No transform specified for creation');
//     }
//     if ( xform.id==null ) {
//         return Promise.reject('No id specified for creation');
//     }
//     if ( xform.type==null ) {
//         return Promise.reject('No type specified for creation');
//     }

//     return this.client.post(url, xform).then( function (resp ) {
//         return resp.id;
//     })
//     , function (err) {
//         return Promise.reject({
//             url: url,
//             status: err.slpt_error_code,
//             statusText: formatted_msg
//         })
//     }

// }



module.exports = Clusters;