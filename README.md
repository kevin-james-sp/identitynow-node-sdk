# identitynow-services-sdk
IdentityNow SDK developed by Services

- [Intro](#intro)
- [Technology Selection](#technology-selection)
- [Usage](#usage)
    - [Proxy](#usage---proxy)

## Intro

There are several sub-projects under this GitHub repository, including both Java and Ruby SDK client implementations.  The IdentityNow Performance Engineering team is contributing to the Java SDK Client in conjunction with Services.  

## Technology Selection


|Function       |Description            |
|---------------|-----------------------|
|Build System 	|Gradle v4.9            |
|ogging System  |Log4j v2.11.0 or better|
|HTTP Client  	|OkHttp3 from Square    |
|JSON Library 	|GSON                   |
|Serializer   	|Retrofit               |

## Usage

### Usage - Proxy
Proxy can be applied by setting system properties anywhere within the application. The proxy type is one of HTTP, DIRECT or SOCKS and it's not case-sensitive. Proxy host must not be null. proxy port must be between 0 and 65535.

Proxy will be enabled for all HTTP transactions if these three system properties are set.

```java
System.setProperty("proxyType", "HTTP");
System.setProperty("proxyHost", "192.168.0.1");
System.setProperty("proxyPort", "string");
```
