var IdentityNowClient = require('./IdentityNowClient');

exports.Create = function ( config ) {

    return new IdentityNowClient( config );

};
