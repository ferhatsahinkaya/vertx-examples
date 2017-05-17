package io.vertx.book.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        vertx
                .eventBus()
                .<String>consumer("hello", event -> event.reply(
                        new JsonObject()
                                .put("served-by", this.toString())
                                .put("message",
                                        Optional.of(event.body())
                                                .filter(e -> !e.isEmpty())
                                                .map(e -> "Hello " + e)
                                                .orElse("Hello World!"))));
    }

}
