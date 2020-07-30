const JSZip=require('jszip');

var client;

function IdentityProfiles( client ) {

    this.client=client;


}

IdentityProfiles.prototype.list = function list () {
        
    let url=this.client.apiUrl+'/cc/api/profile/list';
    let that=this;

    
    return this.client.get(url)
      .then( function (resp) {
        let list=[];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        return Promise.resolve(list);
    }, function (reject) {
        console.log(reject);
        return Promise.reject(reject);
    });


}

IdentityProfiles.prototype.get = function get ( id, options ) {
    
    let url=this.client.apiUrl+'/cc/api/profile/get/'+id;
    
    let that=this;
    return this.client.get(url)
        .then( function (resp) {
            if (options==null || !options.clean) {
                return Promise.resolve(resp.data);
            }
            // Clean the source
            let ret=JSON.parse(JSON.stringify(resp.data, (k,v) => 
                ( (k === 'id') || (k === 'created') || (k === 'modified')
                || (k == 'externalId') || (k == 'lastUpdated') || (k == 'lastAggregated') || (k == 'sinceLastAggregated') 
                || (k == 'applicationId') || (k == 'applicationName') || (k == 'externalName')
                || (k == 'credentialServiceId') 
                ) ? undefined : v )
            );  
            
            if (options.tokenize) {
                return that.client.SDKUtils.tokenize(ret.name, ret, options.tokens);
            }

            return ret;
        }, function(err) {
            console.log('---rejected---');
            console.log(err.response.statusText);
            return Promise.reject({
                url: url,
                status: err.response.status,
                statusText: err.response.statusText
            });
        });
}

IdentityProfiles.prototype.create = function create( profile, options={} ) {

    let url = this.client.apiUrl+'/cc/api/profile/create';
    let that = this;
    // The inital (v2) create call needs a name and source ID (old format)

    if ( !profile.name ) {
        return Promise.reject({
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a name'
        });
    }
    if ( !profile.source.name ) {
        return Promise.reject({
            url: 'IdentityProfile.create',
            status: -1,
            statusText: 'Profile must have a source name'
        });
    }

    // If we need to resolve the name to source ID..
    let sourceIDPromise;
    if (profile.source.id) {
        sourceIDPromise = Promise.resolve( profile.source.id );
    } else {
        sourceIDPromise = this.client.Sources.getByName( profile.source.name )
        .then( source => {
          return source.connectorAttributes.cloudExternalId;
        }, err=> {
          return Promise.reject( err );
        });
    }
    
    sourceIDPromise = sourceIDPromise.then( id => {
        profile.source.id=id; 
        // do the create
        return this.client.post( url, { name: profile.name, sourceId: profile.source.id }, { formEncoded: true } ).then( response => {
            console.log( JSON.stringify(response.data, null, 2) );
            return response.data;
        }, err => {
            console.log('Error creating Identity Profile: '+err.statusText);
            return Promise.reject( err );
        });
    }, err => {
        return Promise.reject( err );
    });

    sourceIDPromise = sourceIDPromise.then( newProfile => {
        console.log("do something here with body and profile");
        // Looks like we need to remove attributeConfig from the new profile, as it won't be merged properly
        // (indeterminate overwrite of attributes on 'attributeTransforms' e.g. ids being left)
        delete newProfile.attributeTransforms;
        // Probably need to do something with configuredStates as well
        let mergedProfile={
            ...newProfile,
            ...profile
        }

        // Define all the promises we need
        let allPromises=[];
        // attribute configs
        mergedProfile.attributeConfig.attributeTransforms.forEach( xform=> {
            if (xform.type=='rule') {
                if (xform.attributes.id==null) {
                    allPromises.push( that.client.Rules.getByName( xform.attributes.name ).then( rule => {
                            xform.attributes.id=rule.id;
                        }, err => {
                            return Promise.reject( err );
                        })
                    );
                }
            }
            else if (xform.type=='accountAttribute') {
                allPromises.push( that.client.Sources.getByName( xform.attributes.sourceName ).then( source => {
                        xform.attributes.applicationId = source.id;
                    }, err => {
                        return Promise.reject( err );
                    })
                );
            }
            else if (xform.type=='reference') {
                // TODO: Validate transform exists
                allPromises.push( that.client.Sources.getByName( xform.attributes.input.attributes.sourceName ).then( source => {
                        xform.attributes.input.attributes.applicationId = source.id;
                    }, err => {
                        return Promise.reject( err );
                    })
                );
            }
        });
        if (mergedProfile.credentialService!=null) {
            if (mergedProfile.credentialService.id==null) {
                allPromises.push( that.client.Sources.getByName( mergedProfile.credentialService.name ).then( source => {
                    mergedProfile.credentialService.id = source.externalId;
                    }, err => {
                        return Promise.reject( err );
                    })
                );
            }
        }
        return Promise.all( allPromises ).then( () => {
            console.log( JSON.stringify( mergedProfile, null, 2) );
            // Do the update here
            return mergedProfile;
        });
    }, err => {
        return Promise.reject( err );            
    })

    return sourceIDPromise;
}

module.exports = IdentityProfiles;