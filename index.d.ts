import { IdentityNowClient } from "./IdentityNowClient";

declare module 'identitynow-sdk';

export function Create( config:any ): IdentityNowClient;
export { IdentityNowClient } from "./IdentityNowClient";

