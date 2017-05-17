package io.vertx.book.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.ext.web.Router.router;
import static java.util.Optional.ofNullable;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        Router router = router(getVertx());
        router.get("/").handler(this::jsonResponse);
        router.get("/:name").handler(this::jsonResponse);

        getVertx()
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private void jsonResponse(final RoutingContext routingContext) {
        final String message = "Hello " + ofNullable(routingContext.pathParam("name")).orElse("World!");
        JsonObject json = new JsonObject().put("message", message);
        routingContext
                .response()
                .end(json.encodePrettily());
    }

}
