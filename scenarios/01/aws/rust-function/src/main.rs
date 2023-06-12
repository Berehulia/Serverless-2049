use async_once::AsyncOnce;
use aws_sdk_dynamodb::{types::AttributeValue, Client};
use lambda_http::{run, service_fn, Body, Error, Request, Response};
use lazy_static::lazy_static;
use uuid::Uuid;

lazy_static! {
    static ref CLIENT: AsyncOnce<Client> = AsyncOnce::new(async {
        let config = aws_config::load_from_env().await;
        Client::new(&config)
    });
}

async fn function_handler(_event: Request) -> Result<Response<Body>, Error> {
    
    let id = Uuid::new_v4().to_string();
    
    let request = CLIENT
        .get()
        .await
        .put_item()
        .table_name("sample-table")
        .item("id", AttributeValue::S(String::from(&id)));

    request.send().await?;

    let message = format!("Created record with id = {id}");

    Ok(Response::builder()
        .status(200)
        .header("content-type", "text/html")
        .body(message.into())
        .map_err(Box::new)?)
}

#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt()
        .with_max_level(tracing::Level::INFO)
        .with_target(false)
        .without_time()
        .init();

    run(service_fn(function_handler)).await
}
