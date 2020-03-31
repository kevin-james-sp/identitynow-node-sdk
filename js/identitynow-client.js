const axios = require('axios');
const qs = require('qs');

module.exports = class IdentityNowClient {

  constructor( logger, config ) {
    this.client_id = process.env.IDN_CLIENT_ID;
    this.client_secret = process.env.IDN_CLIENT_SECRET;
    this.api_url = process.env.IDN_API_URL;
    this.client = axios.create({
      baseURL: process.env.IDN_API_URL
    });
    this.client.interceptors.request.use(function (config) {
      config.metadata = { startTime: new Date() };
      return config;
    }, function (error) {
      return Promise.reject(error);
    });
    this.client.interceptors.response.use(function (response) {
      response.config.metadata.endTime = new Date();
      response.duration = response.config.metadata.endTime - response.config.metadata.startTime;
      logger.debug( `${response.config.method.toUpperCase()} ${response.request.path} ${response.status} ${response.duration} ms` );
      return response;
    }, function (error) {
      error.config.metadata.endTime = new Date();
      error.duration = error.config.metadata.endTime - error.config.metadata.startTime;
      return Promise.reject(error);
    });
  }

  get_token() {
    return this.client.post(`/oauth/token?grant_type=client_credentials&client_id=${this.client_id}&client_secret=${this.client_secret}`,
      null,
      {
        baseURL: this.api_url,
        headers: {
          'Authorization': `Bearer ${this.token}`,
          'Content-Type': 'application/json'
        }
      });
  }

  search_identities( token, query, count, limit ) {
    return this.client.post(`/beta/search/identities?count=${count}&limit=${limit}`,
      {
        "queryType": "SAILPOINT",
        "query": {
          "query": query
        }
      },
      {
        baseURL: this.api_url,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
  }

  search_entitlements( token, query, count, limit ) {
    return this.client.post(`/beta/search/entitlements?count=${count}&limit=${limit}`,
      {
        "queryType": "SAILPOINT",
        "query": {
          "query": query
        }
      },
      {
        baseURL: this.api_url,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
  }

  search_access_profiles( token, query, count, limit ) {
    return this.client.post(`/beta/search/accessprofiles?count=${count}&limit=${limit}`,
      {
        "queryType": "SAILPOINT",
        "query": {
          "query": query
        }
      },
      {
        baseURL: this.api_url,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
  }

  search_role( token, query, count, limit ) {
    return this.client.post(`/beta/search/roles?count=${count}&limit=${limit}`,
      {
        "queryType": "SAILPOINT",
        "query": {
          "query": query
        }
      },
      {
        baseURL: this.api_url,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
  }

};
