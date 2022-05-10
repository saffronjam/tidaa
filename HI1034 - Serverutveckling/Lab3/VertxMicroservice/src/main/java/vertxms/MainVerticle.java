package vertxms;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MainVerticle extends AbstractVerticle {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> fut) {
        var configObject = new JsonObject()
            .put("connection_string", "mongodb+srv://serverlabb3:serverlabb3@cluster0.tz3ua.mongodb.net/serverlabb3" +
                "?authSource=admin&replicaSet=atlas-fdxvtm-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true")
            .put("trustAll", true);
        var client = MongoClient.createShared(vertx, configObject, "vertx-pool");

        client.createCollection("user-logins");
        client.createCollection("user-login-sets");


        final var isHeruko = System.getProperty("heroku", "true").equals("true");
        var port = config().getInteger("http.port", getPort());
        vertx
            .createHttpServer()
            .requestHandler(DatasetRoutes.getRouter(vertx, isHeruko))
            .listen(port,
                result -> {
                    if (result.succeeded()) {
                        System.out.println("Listening to port " + port + " (Heroku: " + isHeruko + ")");
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }

                });
    }

    private int getPort() {
        String portString = System.getenv("PORT");
        if (portString == null) {
            portString = "8086";
        }
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException ignored) {
            return 8086;
        }
    }
}
