package io.vertx.book.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

public class HelloConsumerMicroservice extends AbstractVerticle {

    private WebClient client;

    @Override
    public void start() {
        client = WebClient.create(vertx);

        Router router = Router.router(vertx);
        router.get("/").handler(this::callServer);

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8082);
    }

    private void callServer(final RoutingContext routingContext) {
        final HttpRequest<JsonObject> request1 = client
                .get(8080, "localhost", "/call1")
                .as(BodyCodec.jsonObject());

        final HttpRequest<JsonObject> request2 = client
                .get(8080, "localhost", "/call2")
                .as(BodyCodec.jsonObject());

        final Single<JsonObject> response1 = request1
                .rxSend()
                .map(HttpResponse::body);

        final Single<JsonObject> response2 = request2
                .rxSend()
                .map(HttpResponse::body);

        Single.zip(response1, response2, (r1, r2) ->
                new JsonObject()
                        .put("r1", r1.getString("message"))
                        .put("r2", r2.getString("message")))
                .subscribe(
                        successResponse -> routingContext.response().end(successResponse.encodePrettily()),
                        error -> {
                            throw new RuntimeException(error);
                        }
                );
    }

}
