# Scenario 1 - Python implementation
In this scenario Lambda function (written in Python) generates a random UUID and saves it to the Dynamodb table.

Project structure:
- `python_scenario_01` - source code for the Lambda function.
- `events` - invocation events that you can use to invoke the function.
- `tests` - Unit tests for the Lambda function source code. 
- [template.yaml](./template.yaml) - AWS SAM template that defines the application's AWS resources.
- [Makefile](./Makefile) - defines ready to use tasks to run tests and deployment 

The application uses several AWS resources, including Lambda functions and an API Gateway API. 
These resources are defined in the [template.yaml](./template.yaml). You can update the template to add AWS resources 
through the same deployment process that updates your application code.

The function is intended to be invoked using HTTP request, refer to CloudFormation stack outputs after deployment 
for the endpoint URLs.

Two types of Lambda function will be deployed for different architectures:
* `PythonScenario01Function` for `x86_64`
* `PythonScenario01FunctionArm` for `arm64`

## Prerequisites
It is assumed that you have AWS CLI and AWS SAM CLI (Serverless Application Model) installed and configured.

If not, please follow the following instructions to install these tools:
* AWS CLI - https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
* AWS SAM CLI - https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html

Testing and deployment was automated with [GNU make](https://www.gnu.org/software/make/manual/make.html), 
so it is recommended to use Linux.

If you use Windows, you can use [WSL2](.https://learn.microsoft.com/en-us/windows/wsl/install) to run Ubuntu and 
access Windows drives without extra setup steps. 

The deployment was tested on Ubuntu with Python 3.9.

## Running Tests
To run tests use `make test`

## Deployment
Use `make deploy` for deployment.

To delete deployed resources use `make deploy-clean`