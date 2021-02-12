var obj = {
    "name": "Contractors",
    "description": "Contractors managed by the HR Team",
    "dirtyFlag": false,
    "priority": 40,
    "pwdResetPersonalPhone": true,
    "pwdResetWorkPhone": true,
    "pwdResetPhoneMask": false,
    "pwdResetPersonalEmailCode": true,
    "pwdResetWorkEmailCode": true,
    "pwdResetKba": true,
    "pwdResetDuo": false,
    "pwdResetRsa": false,
    "pwdResetSymantecVip": false,
    "pwdResetSafenet": false,
    "pwdResetMfaType": "SINGLE",
    "strongAuthPersonalEmail": false,
    "strongAuthPersonalPhone": true,
    "strongAuthWorkEmail": false,
    "strongAuthWorkPhone": true,
    "strongAuthKba": true,
    "strongAuthPassword": false,
    "strongAuthDuo": false,
    "strongAuthRsa": false,
    "strongAuthSymantecVip": false,
    "strongAuthSafenet": false,
    "authErrorText": "",
    "blockOffNetwork": false,
    "blockUntrustedGeographies": false,
    "strongAuthLogin": true,
    "strongAuthLoginOffNetwork": false,
    "strongAuthLoginUntrustedGeographies": false,
    "identityCount": 75,
    "source": {
      "name": "Contractors",
      "externalId": "2c9180856925e01b016953f5a38f201e",
      "useForProvisioning": false,
      "lastAggregated": "2020-04-15T09:26:29Z",
      "sinceLastAggregated": 9005494028
    },
    "autoInvite": false,
    "autoInvitationOption": null,
    "autoInviteLifeCycleState": null,
    "authn": {
      "version": 2,
      "name": "profile-16 OpenAM profileauth",
      "description": "profileauth configuration for profile 16",
      "owner": null,
      "lastUpdated": "2019-03-07T09:18:32Z",
      "scriptName": "openamprofileauth",
      "definitionName": "OpenAM profileauth",
      "appCount": 0,
      "userCount": 0,
      "sourceConnected": false,
      "enablePassthroughAuthn": "true",
      "passthroughVersion": "2",
      "profileId": "16",
      "ssoStatus": "complete",
      "icon": "https://files.accessiq.sailpoint.com/modules/builds/static-assets/perpetual/identitynow/icons/2.0/source/",
      "connectedApps": []
    },
    "enablePassthroughAuthn": "true",
    "attributeConfig": {
      "attributeTransforms": [
        {
          "attributeName": "uid",
          "attributes": {
            "name": "Calculate UserName for Contractors"
          },
          "type": "rule"
        },
        {
          "attributeName": "email",
          "attributes": {
            "name": "Calculate Email for HR Contractors"
          },
          "type": "rule"
        },
        {
          "attributeName": "lastname",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "lastname",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "firstname",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "firstname",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "adLastLogonDateType",
          "attributes": {
            "input": {
              "attributes": {
                "applicationId": "2c91808668825ec201689f5ed07d011b",
                "applicationName": "Active Directory [source-1460]",
                "attributeName": "lastLogonTimeStamp",
                "sourceName": "Active Directory"
              },
              "type": "accountAttribute"
            }
          },
          "type": "reference"
        },
        {
          "attributeName": "personalEmail",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "email",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "phone",
          "attributes": {
            "input": {
              "attributes": {
                "applicationId": "2c9180856925e01b016953f5a38f201e",
                "applicationName": "HR Contractors [source-1750]",
                "attributeName": "mobile",
                "sourceName": "Contractors"
              },
              "type": "accountAttribute"
            }
          },
          "type": "reference"
        },
        {
          "attributeName": "city",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "city",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "country",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "country",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "department",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "department",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "displayName",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "userid",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "identificationNumber",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "empid",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "endDate",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "contractEndDate",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "cloudLifecycleState",
          "attributes": {
            "name": "Determine State"
          },
          "type": "rule"
        },
        {
          "attributeName": "manager",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "manager",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "region",
          "attributes": {
            "input": {
              "attributes": {
                "applicationId": "2c9180856925e01b016953f5a38f201e",
                "applicationName": "HR Contractors [source-1750]",
                "attributeName": "city",
                "sourceName": "Contractors"
              },
              "type": "accountAttribute"
            }
          },
          "type": "reference"
        },
        {
          "attributeName": "salesforceUniqueId",
          "attributes": {
            "input": {
              "attributes": {
                "applicationId": "2c9180856925e01b016953f5a38f201e",
                "applicationName": "HR Contractors [source-1750]",
                "attributeName": "email",
                "sourceName": "Contractors"
              },
              "type": "accountAttribute"
            }
          },
          "type": "reference"
        },
        {
          "attributeName": "startDate",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "contractStartDate",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "status",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "status",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "title",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "title",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "userType",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "emp_type",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "workPhone",
          "attributes": {
            "applicationId": "2c9180856925e01b016953f5a38f201e",
            "applicationName": "HR Contractors [source-1750]",
            "attributeName": "telephone",
            "sourceName": "Contractors"
          },
          "type": "accountAttribute"
        },
        {
          "attributeName": "usagelocation",
          "attributes": {
            "input": {
              "attributes": {
                "applicationId": "2c9180856925e01b016953f5a38f201e",
                "applicationName": "HR Contractors [source-1750]",
                "attributeName": "country",
                "sourceName": "Contractors"
              },
              "type": "accountAttribute"
            }
          },
          "type": "reference"
        }
      ],
      "enabled": true
    },
    "configuredStates": [
      {
        "accessProfiles": null,
        "cloudStateCleanupOption": null,
        "description": null,
        "displayName": "Active",
        "emailNotificationOption": null,
        "enabled": true,
        "externalId": "2c9180866925f0790169577272fb0034",
        "externalName": "HR Contractors [cloudIdentityProfile-1551950312042]-active",
        "identityCount": 74,
        "name": "active",
        "type": null
      },
      {
        "accessProfiles": null,
        "cloudStateCleanupOption": null,
        "description": null,
        "displayName": "Inactive",
        "emailNotificationOption": null,
        "enabled": true,
        "externalId": "2c9180866925f0790169577272c80033",
        "externalName": "HR Contractors [cloudIdentityProfile-1551950312042]-inactive",
        "identityCount": 1,
        "name": "inactive",
        "type": null
      },
      {
        "accessProfiles": null,
        "cloudStateCleanupOption": null,
        "description": null,
        "displayName": "Prehire",
        "emailNotificationOption": null,
        "enabled": true,
        "externalId": "2c9180866925f07901695e40a224004c",
        "externalName": "HR Contractors [cloudIdentityProfile-1551950312042]-prehire",
        "identityCount": 0,
        "name": "prehire",
        "type": null
      },
      {
        "accessProfiles": null,
        "cloudStateCleanupOption": null,
        "description": null,
        "displayName": "managerGone",
        "emailNotificationOption": null,
        "enabled": false,
        "externalId": "2c9180876e9b3d08016ed6acbcdc01e3",
        "externalName": "Contractors - managergone [cloudLifecycle-1575559675100]",
        "identityCount": 0,
        "name": "managergone",
        "type": null
      }
    ],
    "status": "ACTIVE",
    "credentialService": {
      "name": "Active Directory",
      "useForPasswordManagement": true
    },
    "credentialServiceId": 1460,
    "externalId": "2c9180866925f07901695772726f0032"
  };

  let ret=JSON.parse(JSON.stringify(obj, (k,v) => 
    ( (k === 'id') || (k === 'created') || (k === 'modified')
    || (k == 'externalId') || (k == 'lastUpdated') || (k == 'lastAggregated') || (k == 'sinceLastAggregated') 
    || (k == 'applicationId') || (k == 'applicationName') || (k == 'externalName')
    || (k == 'credentialServiceId') 
    ) ? undefined : v )
  );  
  console.log(JSON.stringify(ret, null,2));