# Pub Sub Client

This repository provides a sample Client for subscribing to the Salesforce Pub Sub API. 
It is intended for working during development but not tested for production use-cases

The package supports Salesforce [OAuth Client - Credentials Flows](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_client_credentials_flow.htm&language=en_US&type=5) as the only authentication mechanism. 

## Usage

To run the package the following steps needs to be done

### Set your configuration file

Rename the example configuration file `src/main/resources/exampleConfiguration.copy` to `src/main/resources/exampleConfiguration.yaml`. </br>
Define *myDomain* , *clientId* and *clientSecret* according to the Client Credentials flow

### Build and run the package

*Prerequisites : ensure that Maven is installed*

1. Run `$ maven clean install`
2. Execute the jar file using `$ java -cp target/pubsub-1.0-SNAPSHOT.jar com.sfcockpit.Main exampleConfiguration.yaml TestEvent__e` (Please note - the prefix `/event/` is not required for the custom event which is passed))





