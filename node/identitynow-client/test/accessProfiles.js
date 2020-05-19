const assert = require('assert');

const config_kev={
    tenant: 'sp-kjames',
    client_id: 'b905a326fcf34b849a86decc4ba4e4e3',
    client_secret: '8c3734d4ee4b4753f641e36e3f04b4aef71a3cb9777987ef20332cc37cd83f3f'
}

const idnClient=require('../index.js');



describe("Access Profiles", function() {
    
    this.timeout(999999); // In case we're using the debugger

    var appName='Active Directory';
    var client=idnClient.Create( config_kev );
    var prefix;
    var entitlements;
    var originalProfilesCount;

    before( async function() {

        // Create a date-based string that we will use for our Access Profiles
        let date=new Date();
        prefix='test-'+date.getFullYear()+date.getMonth()+date.getDate()+"-"+date.getHours()+date.getMinutes()+date.getSeconds();

        // 1. Get two entitlements. We'll use these when creating access profiles
        entitlements = await client.Entitlements.list( { sourceName: appName });
        assert( entitlements.length>2 );
        
    });
    
    it ('lists the access profiles', async function() {
        
        let profiles = await client.AccessProfiles.list();
        console.log('Before: Num profiles = '+profiles.length);
        originalProfilesCount = profiles.length;

    });

    it('Creates some access profiles', async function() {

        // 3. Create three new access profiles with our entitlements. Name is *datatime*_1, 2, 3
        
        console.log('name prefix: '+prefix);
        
        let profile={
            source: {
                name: appName
            },
            entitlements: [
                entitlements[0].id,                
                entitlements[1].id                
            ],
            owner: {
                type: "IDENTITY",
                name: "slpt.services"
            },
            name: "",
            type: "accessprofile"
        }
        
        for(i=1; i<4; i++) {
            profile.name=prefix+" "+i;
            profile.description=prefix+" test profile "+i;
            await client.AccessProfiles.create( profile );
        }
    });
        
    it('Does another count to check our profiles', async function() {
        // 4. Count Access Profiles. Make sure count is correct
     let profiles = await client.AccessProfiles.list( { sourceName: appName });
     assert( profiles.length = originalProfilesCount+3 );
    });

    it('Gets one our access profiles', async function() {
        // 5. Get one of our access profiles. Make sure no error
        let ent = await client.AccessProfiles.get( prefix+' 1');
        assert( ent!=null );
    });

    it('Uses V2 to get an access profile', async function() {

        // 5a. Get one of our access profiles with specific version
        let ent;
        for(i=0;i<5;i++) {
            ent = await client.AccessProfiles.get( prefix+' 1', { useV2: true });
            if (ent) break;
            else {
                console.log('5a. Waiting for search engine to catch up..');
                await new Promise(resolve => setTimeout( resolve, 2000));
            }
        }
        assert( ent!=null );
    });
    
    // These next tests are ones we just have to trust to the successful return message
    // The search service we use for .search() and .list() doesn't update for several minutes
    // on a delete..
    it('Deletes one access profile', async function() {
        // 6. delete one of our access profiles        
        await client.AccessProfiles.deleteByName( prefix+' 1');        

    });
        
    it('Checks we can\'t delete mulitple profiles without the \'multiple\' flag', async function() {        
        // 6a. assert that deleting multiple with out the 'multiple' flag fails
        assert.throws( function() {
            client.AccessProfiles.deleteByName( prefix+'*' ).then( success => {
                console.log('Just deleted multiple profiles without flag. This is wrong');
            }, err => {
                throw new Error( err );
            });
        })
    });
    
    it('Deletes using a wildcard', async  function() {
        // 7. delete the other two with multiple flag
        await client.AccessProfiles.deleteByName( prefix+'*', { multiple: true } );
    });

    // it('Validates the count against the original count', async function() {
    //     // 8. List Access Profiles. Make sure count matches original
    //     let profiles;
    //     for(i=0;i<5;i++) {
    //         profiles = await client.AccessProfiles.list( { sourceName: appName });
    //         if ( profiles.length == originalProfilesCount ) break;
    //         else {
    //             console.log('Waiting for search engine to catch up..');
    //             await new Promise(resolve => setTimeout( resolve, 2000));
    //         }
    //     }
    //     assert( profiles.length == originalProfilesCount );
    // });
    
});