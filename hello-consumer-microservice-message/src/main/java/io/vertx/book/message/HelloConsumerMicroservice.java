package io.vertx.book.message;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Single;

public class HelloConsumerMicroservice extends AbstractVerticle {

    @Override
    public void start() {
        vertx
                .createHttpServer()
                .requestHandler(event -> {
                    final Single<JsonObject> request1 = vertx
                            .eventBus()
                            .<JsonObject>rxSend("hello", "Call1")
                            .map(Message::body);

                    final Single<JsonObject> request2 = vertx
                            .eventBus()
                            .<JsonObject>rxSend("hello", "Call2")
                            .map(Message::body);

                    Single.zip(request1, request2, (r1, r2) -> new JsonObject()
                            .put("call1", r1.getString("message") + " served by " + r1.getString("served-by"))
                            .put("call2", r2.getString("message") + " served by " + r2.getString("served-by")))
                            .subscribe(
                                    successResponse -> event.response().end(successResponse.encodePrettily()),
                                    error -> {
                                        error.printStackTrace();
                                        event.response().setStatusCode(500).end(error.getMessage());
                                    }
                            );
                })
                .listen(8083);
    }

}
