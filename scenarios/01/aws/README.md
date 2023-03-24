### Description
The function should be able to handle 100,000 parallel POST requests, each containing a UUID string in the request body, and save the received string to the DynamoDB database.

### Implementations
- Java 11

### How to deploy
To deploy, install AWS SAM and run:
- sam build
- sam deploy

### How to test
Trigger the test function with a GET request.
