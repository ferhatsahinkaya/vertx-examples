package io.vertx.book.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class HelloConsumerMicroservice extends AbstractVerticle {

    private WebClient client;

    @Override
    public void start() {
        client = WebClient.create(getVertx());

        Router router = Router.router(getVertx());
        router.get("/").handler(this::callServer);

        getVertx()
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8081);
    }

    private void callServer(final RoutingContext routingContext) {
        final HttpRequest<JsonObject> request = client
                .get(8080, "localhost", "/consumer")
                .as(BodyCodec.jsonObject());

        request.send(event -> {
            if (event.failed()) {
                throw new RuntimeException();
            } else {
                routingContext
                        .response()
                        .end(event.result().body().encodePrettily());
            }
        });
    }

}
