package com.lineup.scenario01.function.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class Function implements RequestHandler < APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent > {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  private static final URI FUNCTION_URI = URI.create( System.getenv( "FUNCTION_URL" ) );

  @Override
  public APIGatewayProxyResponseEvent handleRequest( APIGatewayProxyRequestEvent event, Context context ) {

    IntStream.range( 0, 1000 )
        .parallel()
        .mapToObj( this::buildHttpRequest )
        .map( this::sendAsyncRequest )
        .forEach( CompletableFuture::join );

    return new APIGatewayProxyResponseEvent()
        .withStatusCode( 200 );
  }

  private HttpRequest buildHttpRequest( int i ) {

    return HttpRequest
        .newBuilder( FUNCTION_URI )
        .header( "content-type", "application/json" )
        .POST( HttpRequest.BodyPublishers.ofString( UUID.randomUUID().toString() ) )
        .build();
  }

  private CompletableFuture < HttpResponse < String > > sendAsyncRequest( HttpRequest request ) {

    return HTTP_CLIENT.sendAsync( request, HttpResponse.BodyHandlers.ofString() );
  }

}