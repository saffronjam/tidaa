package vertxms;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.io.ObjectInputFilter;

public class MainVerticle extends AbstractVerticle {
    public static void main(String[] args) {

        JsonObject configObject = new JsonObject()
            .put("connection_string", "mongodb://vertx-db:27017/?readPreference=primary&ssl=false")
            .put("db_name", "vertx-db");

        Vertx vertx = Vertx.vertx();
        var client = MongoClient.createShared(vertx, configObject, "vertx-pool");


        client.createCollection("user-logins");
        client.createCollection("user-login-sets");
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> fut) throws Exception {    // Bind "/" to our hello message - so we are still compatible.


        vertx
            .createHttpServer()
            .requestHandler(DatasetRoutes.getRouter(vertx))
            .listen(
                // Retrieve the port from the configuration,
                // default to 8080.
                config().getInteger("http.port", 8086),
                result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }

                });
    }
}
