import { AccessProfiles } from "./accessprofiles";

export interface IdentityNowClient {

    public AccessProfiles:AccessProfiles;
    Connectors;
    IdentityAttributes;
    public IdentityProfiles:IdentityProfiles;
    NELM;
    Roles;
    Rules;
    Schemas;
    Sources;
    public Tags:Tags;
    Transforms;
    Workgroups;
    
    getClientToken(): Promise<string>;

}