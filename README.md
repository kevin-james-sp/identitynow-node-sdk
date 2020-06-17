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
const idnClient=require('identitynow-client');

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

*ALTERNATIVELY..* If you are using the client from within a server-side Node app, where the app handles the user authentication via browser redirection, you can instantiate the client like so:

```
const idnClient=require('identitynow-sdk');

const config={
    tenant: 'readme',
    userToken: <Token passed back to the app via oauth redirect>
    userRefreshToken: <Refresh token passed back to the app via oauth redirect>
}

const client=idnClient.Create( config );

```

### Common Options ###
All object types support the following options:
- clean: remove attributes that would make no sense in another target system, e.g.
    id, created, modified, synced, pod, org
- tokenize: flag to say whether tokenization should be applied during 'get' operations
- tokens: JSONPath tokens to apply when `tokenize` is true

### Tokenization ###
Tokenization is the act of replacing specified values in an object with tokens. This process uses [JSONPath](https://goessner.net/articles/JsonPath/) to identify values that should be replaced.
For example, we can replace usernames, passwords and server addresses in a source, that can be replaced with new values when importing into another tenant.

To use, specify `tokenize` as `true` in the options of a `get` call, and then pass an array of tokens as `tokens` in the options.

For example:

A section of an exported source looks like this:

```{
  "description": "AD Access Requests",
  "owner": {
    "type": "IDENTITY",
    "name": "Bob.Bobsson"
  },
  "cluster": {
    "type": "CLUSTER",
    "name": "DC1 VA Cluster"
  },
  .
  .
  "connectorAttributes": {
    .
    .
    "forestSettings": [
      "user": "DC1\Administrator"
      .
      .      
    ]
  }
  
```

Passing in a token list like this:
```
[
  {
    path: '$.owner.name',
      token: 'OWNER'
  },
  {
      path: '$.cluster.name',
      token: 'CLUSTER'
  },
  {
      path: '$.connectorAttributes.forestSettings[0].user',
      token: 'FOREST_USERNAME'
  }
]
```

Will result in the source looking like this:
```{
  "description": "AD Access Requests",
  "owner": {
    "type": "IDENTITY",
    "name": "%%OWNER%%"
  },
  "cluster": {
    "type": "CLUSTER",
    "name": "%%CLUSTER%%"
  },
  .
  .
  "connectorAttributes": {
    .
    .
    "forestSettings": [
      "user": "%%FOREST_USERNAME%%"
      .
      .      
    ]
  }
  
```
And also a tokens file that looks like this:
```
[
  {
    "token": "%%OWNER%%",
    "value": "Bob.Bobsson"
  },
  {
    "token": "%%CLUSTER%%",
    "value": "DC1 VA Cluster"
  },
  {
    "token": "%%FOREST_USERNAME%%",
    "value": "DC1\Administrator"
  }
]
```

Once you have an authenticated client, the following actions are available

**NOTE** Unless otherwise specified, all methods will return a Promise

## Access Profiles ##

### List ###
`client.AccessProfiles.list( [options] )`

```
  client.AccessProfiles.list().then( function ( profiles ) { 
      ....
  });
```
List all access profiles.

Options:
- useV2: Use the V2 API to list Access Profiles. The data returned by V2 is slightly different to V3

Methods to directly use the V2 or V3 API are available as `listv2()` and `listv3()` respectively

### Search ###
`client.AccessProfiles.search( query )`

```
  client.AccessProfiles.search( 'AD*' ).then( function ( profiles ) { 
      ....
  });
```
Search access profiles. query can contain wildcards

__NOTE__ Uses the V3 API, so to use the results with any wrapped V2 calls translation will be required

### Get By Name ###
`client.AccessProfiles.getByName( name [, options])`

```
  client.AccessProfiles.getByName( name ).then( function ( profile ) { 
      ....
  });
```
Get an Access Profile by name. This method is designed to return an error if more than one result is found. For multiple results, use AccessProfiles.search()

Options:
- useV2: Use the V2 API to get the Access Profile. The data returned by V2 is slightly different to V3

### Get ###
`client.AccessProfiles.get( id [, options])`

```
  client.AccessProfiles.get( id ).then( function ( profile ) { 
      ....
  });
```
Get an Access Profile

Options:
- useV2: Use the V2 API to list Access Profiles. The data returned by V2 is slightly different to V3

Methods to directly use the V2 or V3 API are available as `getv2( id )` and `getv3( id )` respectively

### Delete ###
`client.AccessProfiles.delete( id [, options])`

```
  client.AccessProfiles.delete( id ).then( function ( profile ) { 
      ....
  });
```
Delete Access Profile

Options:
- useV2: Use the V2 API to delete Access Profiles

Methods to directly use the V2 or V3 API are available as `deletev2( id )` and `deletev3( id )` respectively

### Delete ###
`client.AccessProfiles.deleteByName( id [, options])`

```
  client.AccessProfiles.deleteByName( id ).then( function ( profile ) { 
      ....
  });
```
Delete Access Profile (by name)

Options:
- useV2: Use the V2 API to delete Access Profiles

### Create ###
`client.AccessProfiles.create( json [, options])`

```
  client.AccessProfiles.create( json ).then( function ( response ) { 
      ....
  });
```
Create an Access Profile
Using names for owner and source is possible; use the attributes `ownerName` and `sourceName` instead of `ownerId` or `sourceId` to have the SDK perform a lookup into the target system.
`entitlements` must be specified as IDs (migration to value is a roadmap item)

Options:
- useV2: Use the V2 API to create Access Profiles. The data required by V2 is slightly different to V3

Methods to directly use the V2 or V3 API are available as `createv2( id )` and `createv3( id )` respectively
__NOTE__ There is currently no V3 API for this; trying to use it will return a rejected promise.

## Account Profiles ##

### List ###
`client.AccountProfiles.list()`

```
  client.AccountProfiles.list().then( function ( profiles ) { 
      ....
  });
```
List all Account profiles.

### Get ###
`client.AccountProfiles.get( id )`

```
  client.AccountProfiles.get( id ).then( function ( profile ) { 
      ....
  });
```
Get an Account Profile

### Update ###
`client.AccountProfiles.update( id, json)`

```
  client.AccountProfiles.update( id, json ).then( function ( response ) { 
      ....
  });
```
Update an Account Profile

## Clusters ##

### List ###
`client.Clusters.list()`
```
  client.Clusters.list().then( function ( clusters ) { 
      ....
  });
```
Get a list of VA Clusters

### Get by name ###
`client.Clusters.getByName( name )`
```
  client.Clusters.getByName( name ).then( function ( cluster ) { 
      ....
  });
```
Get a specific VA Cluster


## Entitlements ##

### List ###
`client.Entitlements.list( [ options ])`
```
  client.Entitlements.list().then( function ( entitlements ) { 
      ....
  });
```
Get a list of entitlements

Options:
- sourceId: id of source to constrain search
- sourceName: name of source to constrain search
- entitlements: list of values to constrain search
```
  client.Entitlements.list( {
      sourceName: 'Active Directory',
      entitlements: [
          'CN=All_Users,OU=Groups,DC=sailpoint,DC=com',
          'CN=Corporate-VPN,OU=Groups,DC=sailpoint,DC=com'
      ]
  }).then( function ( entitlements ) { 
      ....
  });
```

## Identities ##

### List ###
`client.Identities.list()`
```
  client.Identities.list().then( function ( identities ) { 
      ....
  });
```
Get a list of identities

### Get ###
`client.Identities.get( id )`
```
  client.Identities.get( id ).then( function ( identities ) { 
      ....
  });
```
Get an identity

Options:
- useV2: use the V2 API to retrieve the Identity
Methods to directly use the V2 or V3 API are available as `getv2( id )` and `getv3( id )` respectively

## Roles ##

### List ###
`client.Roles.list()`
```
  client.Roles.list().then( function ( roles ) { 
      ....
  });
```
Get a list of roles

### Get ###
`client.Roles.get( idOrName )`
```
  client.Identities.get( id ).then( function ( identity ) { 
      ....
  });
```
Get an identity

## Sources ##

### List ###
`client.Sources.list()`
```
  client.Sources.List().then( function ( sources ) { 
      ....
  });
```

Get a list of sources

### Get ###
`client.Sources.get( id [, options] )`

Get a specific source by ID.

```
    client.Sources.get( 'abcdef1234' ).then( function ( source ) {
        ....
    });
```

Get a 'clean' version of the source. Strips out all IDs as they will only be accurate in the tenant the source is extracted from
```
    client.Sources.get( 'abcdef1234', { clean: true } ).then( function ( source) {
        ....
    });
```

Get an 'exported' version of the source. This will collect sub-objects (such as Schemas) and bundle them in the response.
```
    client.Sources.get( 'abcdef1234', { clean: true, export: true } ).then( function ( object ) {
        ....
    });
```
This will return something like:
```{
    source: { <source data> },
    schemas: [
        { <schema data> },
        { <schema data> }
    ]
    ...
    connectorFiles: {
        <filename>:
    }
}
```

Options:
- export: Collect sub-objects (such as Schemas) and bundle them in the response.
- zip: return the source and related objects as a JSZip object

### Get by Name ###

Get a specific source by ID.
`client.Sources.getByName( name [, options] )`
```
    client.Sources.getByName( 'Active Direectory' ).then( function ( source) {
        ....
    });
```
The same options as `get( id )` are available


### Get Zip file ###
Alternative call to get a zip of a specific source with its associated objects
```
    client.Sources.getZip( 'abcdef1234' ).then( function ( zip ) {
        ....
    });
```
This returns a zip object (using the JSZip library) which can then be written to a file. For example:
```
client.Sources.getZip( 'abcdef1234' ).then( function (zip) {
    zip
    .generateNodeStream({type:'nodebuffer',streamFiles:true})
    .pipe(fs.createWriteStream('out.zip'))
    .on('finish', function () {
        // JSZip generates a readable stream with a "end" event,
        // but is piped here in a writable stream which emits a "finish" event.
        console.log("out.zip written.");
    });
})
```

### Create ###

Create a new source
```
client.Sources.create( object );
```
When creating a source, the object passed in can contain all the sub-objects (schemas etc.) associated with the source. At a minimum, it must contain a definition of the source:
```
{
    source: {
        description: 'My Source',
        ...
    }
}
```
It can also contain Schemas (this list continues to be extended)

Owner and Cluster can be specified by name; the SDK will look up the relevant ID in the IDN tenant


### Update ###
TODO

### Delete ###
```
var source=client.Sources.delete( 'abcdef1234' )
```

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

```
client.Schemas.create( 'abcdef1234', object );
```
Create a schema. Pass in the ID of the Source, and the object representing the schema

### Update ###
TODO

### Delete ###
```
client.Schemas.delete( 'abcdef1234', 'badc0ffee' ).then( function( ok ){
    ....
})
```
Delete a schema. Pass in the ID of the Source, and the ID of the schema

## Transforms ##

### List ###
```
  client.Transforms.List().then( function( items ){
      ....
  });
```
Return value looks like:
```
[
    {
        "attributes": null,
        "id": "ToUpper",
        "type": "upper"
    },
    {
        "attributes": null,
        "id": "ISO3166 Country Format",
        "type": "iso3166"
    },
    ....
]
```
### Get ###

Get a specific transform by ID
```
    client.Schemas.get( 'ToUpper' ).then( function (transform) {
        ....
    });
```

### Create ###
TODO

### Update ###
TODO

### Delete ###
TODO

## Account Profiles ##

### List ###
```
client.AccountProfiles.list( 'abcdef1234' ).then( function( profiles ) {
    ....
});
```
Returns a list of account profiles for the specified source. Returns an Array

### Get ###
```
client.AccountProfiles.get( 'abcdef1234', 'Create' ).then( function( profile ) {
    ....
});
```
Returns the account profile for the specified source and Usage
