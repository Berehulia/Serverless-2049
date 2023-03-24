cd .\java-function\

docker build . -t graal-function-builder --progress=plain

docker run --name graal-function graal-function-builder
docker cp graal-function:/project/native.zip .
docker rm graal-function

cd ..

sam build
sam deploy --no-confirm-changeset

$StackName = 'Serverless2049-Scenario-02'
$Query = 'Stacks[0].Outputs[?OutputKey==`FileBucketUrl`].OutputValue'

$FileBucketUrl = aws cloudformation describe-stacks --stack-name $StackName --query $Query --output text
aws s3 cp "..\20MB.txt" $FileBucketUrl
