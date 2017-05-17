package io.vertx.book.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        vertx
                .eventBus()
                .<String>consumer("hello", event -> {
                    JsonObject json = new JsonObject()
                            .put("served-by", this.toString());

                    final String message = Optional.of(event)
                            .filter(e -> e.body().isEmpty())
                            .map(e -> "Hello " + e.body())
                            .orElse("Hello World!");

                    json.put("message", message);
                });
    }

}
