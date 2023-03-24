cd .\java-function\

docker build . -t graal-function-builder --progress=plain

docker run --name graal-function graal-function-builder
docker cp graal-function:/project/native.zip .
docker rm graal-function

cd ..

sam build
sam deploy --no-confirm-changeset
