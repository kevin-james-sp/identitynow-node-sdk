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

if userAuthenticate is set to true, when a call is made to IdentityNow a browser window will be opened for the user to authenticate. If the user is already authenticated in the browser, then the window will immediately close and execution will continue.

*NOTE* If you wish to perform API activities that require an ORG_ADMIN connection, you will need to step up in the browser before running your code

Once you have an authenticated client, the following actions are available

**NOTE** Unless otherwise specified, all methods will return a Promise

## Sources ##

### List ###
```
  var sources = client.Sources.List();
```

### Get ###

Get a specific source by ID
```
    var source = client.Sources.get( 'abcdef1234' );
```

Get a 'clean' version of the source. Strips out all IDs as they will only be accurate in the tenant the source is extracted from
```
    var source = client.Sources.get( 'abcdef1234', { clean: true } );
```

Get an 'exported' version of the source. This will collect sub-objects (such as Schemas) and bundle them in the response.
```
    var source = client.Sources.get( 'abcdef1234', { clean: true, export: true } );
```
This will return something like:
```{
    source: { <source data> },
    schemas: [
        { <schema data> },
        { <schema data> }
    ]
}
```

### Create ###
TODO

### Update ###
TODO

### Delete ###
TODO

## Schemas ##

### List ###
```
  var sources = client.Schemas.List();
```

### Get ###

Get a specific source by ID
```
    var source = client.Schemas.get( 'abcd1234' );
```

### Create ###
TODO

### Update ###
TODO

### Delete ###
TODO

## Transforms ##

### List ###
```
  var sources = client.Transforms.List();
```

### Get ###

Get a specific transform by ID
```
    var source = client.Schemas.get( 'ToUpper' );
```

### Create ###
TODO

### Update ###
TODO

### Delete ###
TODO
