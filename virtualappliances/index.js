const ssh = require( 'ssh2' );
const yaml = require( 'js-yaml' )
var axios = require( 'axios' );

const JSZip = require( 'jszip' );

var client;

function VirtualAppliances( client ) {

    this.client = client;

}
/**
 * Creates a cluster
 * config is an object containing the configuration
 * - suffix: Custom suffix for Cluster name (optional)
 * @param {} config 
 */
VirtualAppliances.prototype.createCluster = function ( config ) {
    // POST: https://se-labs.api.identitynow.com/cc/api/cluster/create?name=AWS%20Cluster&gmtOffset=-6

    if (!config.clusterName) {
        return Promise.reject('VA:CreateCluster: clusterName must be specified');
    }

    let url = `${this.client.apiUrl}/cc/api/cluster/create?name=${encodeURI( config.clusterName )}&gmtOffset=-6`;

    return this.client.post( url ).then( response => {
        return response.data;
    } );

    //cluster = JSON.parse(SeUtils.api_post(org,api,user,pass,client_id,client_secret,nil,token))
    // response is :
    // {"id":"1360","description":null,"clients":[],"name":"AWS Cluster","clusterId":"1360","pollingPeriod":-1,
    //   "pollingPeriodMinutes":-1,"pollingPeriodTimestamp":1611346474011,
    //   "apiGatewayBaseUrl":"https://se-labs.api.identitynow.com","jwtRequestPath":"/oauth/token",
    //   "configuration":{"gmtOffset":"-6","va_version":"va-stg02-useast1","failureThreshold":"0","debug":"false",
    //     "scheduleUpgrade":"false","clusterType":"sqsCluster","clusterExternalId":"2c91808c771b686101772bbbf3c00cda",
    //     "cookbook":"va-stg02-useast1"},"va_version":"va-stg02-useast1",
    //     "maintenance":{"window":"false","windowStartTime":"2021-01-22T00:00:00Z",
    //       "windowClusterTime":"2021-01-22T14:14:34Z","windowFinishTime":"2021-01-22T04:00:00Z"},
    //       "queue":{"name":"stg02-useast1-se-labs-cluster-1360","region":"us-east-1"},
    //       "connectorServiceCount":0,"serviceCount":0,"status":"FAILED","alertKey":"NO_ACTIVE_CLIENTS",
    //       "activeClients":0,"jobs":[]}

}

/**
 * Create a new VA in the cluster
 * in the config object parameter:
 * id: ID of the cluster to create the VA in
 * @param {*} config 
 * 
 * returns: an Object containing
 *   id: the ID of the newly created VA
 *   yamlSource: the String that is the YAML configuration file
 *   yamlObject: a JavaScript object representing the parsed YAML file
 */
VirtualAppliances.prototype.createVA = function ( config ) {

    if ( !config.clusterid ) {
        throw ( 'VA.CreateVA: clusterid must be specified' );
    }

    let url = `${this.client.apiUrl}/cc/api/client/create?clusterId=${config.clusterid}&type=VA`;


console.log(url);

    // Create the VA
    let promise = this.client.post( url );

    // get the YAML file from the response
    promise = promise.then( response => {
        console.log('Create VA Complete');
        console.log( JSON.stringify( response.data ));
        let yamlconfig = yaml.load( response.data.yamlConfig );
        // We only need to return some of the stuff we got. Add here if necessary
        return {
            id: response.data.id,
            yamlSource: response.data.yamlConfig,
            yamlObject: yamlconfig
        }
    }, reject => {
        console.log(`createVA failed: ${JSON.stringify(reject, null, 2)}`);
        return Promise.reject( reject );
    } );

    return promise;
}

module.exports = VirtualAppliances;