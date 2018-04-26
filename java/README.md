This repository houses the IdentityNow Services SDK package; AKA the "Chandlery" API client.
This package implements a single .jar file in Java that communicates with IdentityNow 
organizations.  It is jointly maintained by the IdentityNow Services and Performance Engineering
teams for their various purposes and needs for configuring, testing, and interacting with
IdentityNow organizations.

For more details, see the following Harbor document: https://harbor.sailpoint.com/docs/DOC-24368

* Compiling, Building, Sanity Checking *

This package is built with Gradle version 4.7.  Once checked out building is easy:

	$ pwd
	/Users/adam.hampton/dev/identitynow-services-sdk/java
	./gradlew clean assemble

The build process produces a deliverable .jar file in the build/libs directory:

	$ ls -latr build/libs/
	-rw-r--r--  1 adam.hampton  SAILPOINT\Domain Users  21595 Apr 26 10:08 identitynow-services-sdk-0.0.2.jar

The build can be sanity checked by executing the EnvironmentReport class from the jar:

	$ java -cp build/libs/identitynow-services-sdk-0.0.2.jar sailpoint.services.idn.console.EnvironmentReport
	
	IdentityNow Services SDK - Environment Report Utility.
	Copyright (C) 2018, SailPoint Technologies, Inc.
	All Rights Reserved.


 