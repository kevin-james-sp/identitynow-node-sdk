var client;

function Certification( client ) {
    this.client=client;
}
// This is a 'Perform Search' API v3. 
Certification.prototype.getPage=function( payload, off, lst) {
        
    let offset=0;
    if (off!=null) {
        offset=off;
    }
    
    let list=[];
    if (lst!=null) {
        list=lst;
    }
    
    let limit=100;

    let url=this.client.apiUrl+'/v3/search?limit='+limit+'&offset='+offset+'&count=true';
    if (!payload) {
        return Promise.reject({
            url: 'Perform search v3',
            status: -6,
            statusText: 'Payload body is null. Cant be null'
        })
    }
    //let that=this;

    return this.client.post(url, payload)
        .then( function (resp) {
        count=resp.headers['x-total-count'];
        resp.data.forEach( function( itm ) {
            list.push(itm);
        } );
        offset+=resp.data.length;
        if (list.length<count) {
            return that.getPage(payload, offset, list);
        }
        return Promise.resolve(list);
    }, function ( err ) {
        console.log('getPage.reject');
        console.log( err );
        return Promise.reject({
            url: url,
            status: err.response.status,
            statusText: err.response.statusText
        });
    });

}

/* Check json object whether it has required value
 * @param {object} certification json body. ex:
 * {
 *      "name" : "Manager Campaign",
 *      "description" : "Everyone needs to review this campaign",
 *      "type" : "SOURCE_OWNER"
 *      "deadline": "2020-03-15T10:00:01.456Z"
 * }
 */

Certification.prototype.checkCampaign = function(object) {
    // check if object has required items (name, description, type)
    if (!object.name) {
        return Promise.reject({
            url: 'Certification.checkCampaign',
            status: -6,
            statusText: 'No name specified for creation'
        });
    } else if (!object.description) {
        return Promise.reject({
            url: 'Certification.checkCampaign',
            status: -6,
            statusText: 'No description specified for creation'
        });
    } else if (!object.type) {
        return Promise.reject({
            url: 'Certification.checkCampaign',
            status: -6,
            statusText: 'No type specified for creation'
        });
    } else {
        console.log('all checks pass');
        return true;
    }
}


/* Create a new certification campaign with information in object parameter
 * @param {object} certification json body. ex:
 * {
 *      "name" : "Manager Campaign",
 *      "description" : "Every manager needs to review this campaign",
 *      "type" : "SOURCE_OWNER"
 *      "deadline": "2020-03-15T10:00:01.456Z"
 * }
 */

Certification.prototype.createCampaign = function (object) {

    // check if object has required items (name, description, type)
    let check = this.checkCampaign(object);

    if (check == true) {
        const url = `${this. client.apiUrl}/beta/campaigns`;
        const options = {
            contentType: 'application/json',
            formEncoded: false,
        }
    
    // AXIOS POST return promises, store promise in 'result' variable
    let result = this.client.post(url, object, options)
        .then(resp => {
            return resp.data;
        }).catch( err => {
            if (!err.statusText) {
                console.log('err with no statusText calling certification.create /beta/campaigns');
                console.log(err);
            }
            return Promise.reject({
                url: 'Certification.createCampaign',
                status: -9,
                statusText: err.statusText || err.exception_message
            });
        })
        return result;

    } else {
        return check;
    }

}

/* Create a new certification campaign template with information in object parameter
 *  @param {object} certification json body. ex:
 * {
 *      "name" : "Manager Campaign",
 *      "description" : "Everyone needs to review this campaign",
 *      "campaign" : {
 *         "name" : "Manager campaign", 
 *         "description" : "this is a manager template"
 *         "type" : "MANAGER",
 *         "emailNotificationEnabled" : true,
 *       }
 *      "deadlineDuration" : "P2W"
 * }
 */

Certification.prototype.createTemplate = function (object) {

    // check if object has required items (name, description, campaign object)
    if (!object.name) {
        return Promise.reject({
            url: 'Certification.createTemplate',
            status: -6,
            statusText: 'No name specified for creation'
        });
    } else if (!object.description) {
        return Promise.reject({
            url: 'Certification.createTemplate',
            status: -6,
            statusText: 'No description specified for creation'
        });
    } else if (!object.campaign) {
        return Promise.reject({
            url: 'Certification.createTemplate',
            status: -6,
            statusText: 'No campaign object specified for creation'
        });
    }

    // check if campaign key object has name or description or type
    let check = this.checkCampaign(object.campaign);

    if (check == true){
        const url = `${this. client.apiUrl}/beta/campaign-templates`;
        const options = {
            contentType: 'application/json',
            formEncoded: false,
        }
    
    // AXIOS POST return promises
    let result = this.client.post(url, object, options)
        .then(resp => {
            return resp.data;
        }).catch( err => {
            if (!err.statusText) {
                console.log('err with no statusText calling certification.createTemplate /beta/campaign-template');
                console.log(err);
            }
            return Promise.reject({
                url: 'Certification.createTemplate',
                status: -9,
                statusText: err.statusText || err.exception_message
            });
        })
        return result;
    } else {
        return check;
    }

}



/* List Campaign Template
 * No object parameter is needed
 */

Certification.prototype.listTemplate = function() {

    let url = this.client.apiUrl + '/beta/campaign-templates';
    //let that = this;

    //AXIOS GET return promises, store promise in 'result' variable
    let result = this.client.get(url)
        .then(function(resp) {
            if (resp.data){
                return resp.data;
            } else {
                return Promise.reject({
                    url: 'Certification.list',
                    status: -9,
                    statusText: 'No data is returned'
                });
            }
        }).catch( err => {
            if (!err.statusText) {
                console.log('err with no statusText calling certification.list /beta/campaign-templates');
                console.log(err);
            }
            return Promise.reject({
                url: 'Certification.list',
                status: -9,
                statusText: err.statusText || err.exception_message
            });
        })
    return result;
}

/* Create a new certification campaign template with information in object parameter
 *  @param {string} campaign id - ex: 27cff0281ae647c4b4917e5bdd48c3dc
 */


Certification.prototype.generate = function(id) {

    let url = `${this.client.apiUrl}/beta/campaign-templates/${id}/generate`;
    console.log(`Generating Campaign from Templates ${id}`);

    const options = {
        contentType: 'application/json',
        formEncoded: false,
    }

    // AXIOS POST return promises
    let result = this.client.post(url, options)
    .then(resp => {
        return resp.data;
    }).catch( err => {
        if (!err.statusText) {
            console.log('err with no statusText calling certification.generate');
            console.log(err);
        }
        return Promise.reject({
            url: 'Certification.generate',
            status: -9,
            statusText: err.statusText || err.exception_message
        });
    })
    return result;

}
module.exports = Certification;