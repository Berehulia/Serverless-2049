$StackName = 'Serverless2049-Scenario-02'
$Query = 'Stacks[0].Outputs[?OutputKey==`FileBucketUrl`].OutputValue'

$FileBucketUrl = aws cloudformation describe-stacks --stack-name $StackName --query $Query --output text
aws s3 rm $FileBucketUrl --recursive

sam delete --no-prompts
