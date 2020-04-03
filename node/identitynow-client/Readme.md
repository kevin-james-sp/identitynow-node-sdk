# Node SDK for IdentityNow #

This is a node module for interacting with IdentityNow.

## Import ##
`const idnClient=require('identitynow-client');`

## Authorization ##

Authorization needs to be configured during instantiation. The following fields need to be passed in:
| | |
|---|---| 
| Tenant | Name of the IDN Tenant
| clientID | Client ID for your API Client or Personal Access Token (PAT)
| clientSecret | Secret for your API Client or PAT
| callbackURL | Address your local listener or server will be running on. Only required for interactive user auth
| userAuthenticate | boolean flag to indicate OAuth user authentication should be performed. (Optional)

This can be performed in a block like this:

```
const config={
    tenant: 'readme',
    clientID: 'b0b15abaddad',
    clientSecret: '900dc0ffee',
    callbackURL: 'http://localhost:5000/auth/identitynow/callback',
    userAuthenticate: true
}

const client=idnClient.Create( config );

```

ClientID and Secret are generated on the Admin page Global->Security Settings->API Management. a PAT can only (currently) be generated through REST API calls. Both are outside the scope of this document.



