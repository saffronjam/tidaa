package vertxms;

import com.google.gson.Gson;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.springframework.http.HttpStatus;
import utilities.Requests;
import utilities.bodies.ExceptionBody;
import utilities.bodies.IdBody;
import utilities.bodies.IdStringBody;
import utilities.exceptions.BackendException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class DatasetRoutes {

    static HashMap<Long, int[]> cache = new HashMap<>();

    public static Router getRouter(Vertx verticle, boolean isHeruko) {
        var router = Router.router(verticle);

        router.route().handler(CorsHandler.create("*")
            .allowedMethod(HttpMethod.GET)
            .allowedMethod(HttpMethod.POST)
            .allowedMethod(HttpMethod.OPTIONS)
            .allowCredentials(true)
            .allowedHeader("Access-Control-Allow-Method")
            .allowedHeader("Access-Control-Allow-Origin")
            .allowedHeader("Access-Control-Allow-Credentials")
            .allowedHeader("Content-Type")).handler(BodyHandler.create());

        router
            .post("/report")
            .consumes("*/json")
            .handler(rc -> {
                try {
                    JsonObject json = rc.getBodyAsJson();
                    var token = json.getString("token", null);
                    if (token != null) {
                        var client = MongoClient.createShared(verticle, new JsonObject(), "vertx-pool");

                        String userMsIp = isHeruko ? "https://serverlabb3-userMs.herokuapp.com" : "http://localhost:8084";

                        System.out.println("Request out to " + userMsIp);

                        var req = Requests.createGet(userMsIp + "/validate/" + token);
                        IdBody idBody;


                        try {
                            idBody = Requests.getJsonObject(req, IdBody.class);
                            System.out.println("Received id: " + idBody.id());
                        } catch (BackendException exception) {
                            throw new BackendException("Failed to contact user validator: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                        }

                        if (idBody.id() != -1) {
                            var date = LocalDateTime.now();
                            var loginReportJson = new JsonObject()
                                .put("timestamp", date.toString())
                                .put("userId", idBody.id());

                            System.out.println("Putting user login in database");
                            client.save("user-logins", loginReportJson, event -> {
                                if (event.succeeded()) {
                                    System.out.println("Successfully put user in database");
                                } else {
                                    System.out.println("Failed to put user in database. Reason: " + event.cause());
                                }
                            });

                            rc.response().setStatusCode(200).end();
                        } else {
                            var exceptionBody = new ExceptionBody("Invalid user token");
                            rc.response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(400)
                                .end(new Gson().toJson(exceptionBody));
                        }
                    }
                } catch (BackendException exception) {
                    rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(400)
                        .end(new Gson().toJson(new ExceptionBody(exception.getMessage())));
                }
            });

        router
            .get("/report/:userId")
            .handler(rc -> {
                try {

                    String userIdString = rc.request().getParam("userId");
                    if (userIdString == null) {
                        throw new BackendException("No userId provided", HttpStatus.BAD_REQUEST);
                    }
                    System.out.println("Request to report user " + userIdString);


                    long userId = -1;
                    try {
                        userId = Long.parseLong(userIdString);
                    } catch (NumberFormatException exception) {
                        throw new BackendException("Bad userId provided", HttpStatus.BAD_REQUEST);
                    }

                    System.out.println("Fetching database client");
                    var client = MongoClient.createShared(verticle, new JsonObject(), "vertx-pool");
                    System.out.println("Successfully fetched database client");

                    JsonObject query = new JsonObject()
                        .put("userId", userId);
                    client.find("user-logins", query, res -> {
                        if (res.succeeded()) {
                            System.out.println("Found user from MS");
                            final var dataPoints = new Integer[10];
                            for (int i = 0; i < dataPoints.length; i++) {
                                if (dataPoints[i] == null) {
                                    dataPoints[i] = 0;
                                }
                            }

                            for (JsonObject json : res.result()) {
                                try {
                                    var report = Json.decodeValue(json.encode(), UserLoginReport.class);
                                    var diff = Math.abs((int) ChronoUnit.DAYS.between(LocalDateTime.now(), LocalDateTime.parse(report.timestamp())));
                                    if (diff < 10) {
                                        dataPoints[9 - diff]++;
                                    }
                                } catch (DecodeException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            rc.response().putHeader("content-type", "application/json").setStatusCode(200).end(new Gson().toJson(dataPoints));
                        } else {
                            System.out.println("Failed");
                            rc.response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(400)
                                .end(new Gson().toJson(new ExceptionBody("Failed to fetch user logins from database. Reason: " + res.cause())));
                        }
                    });
                } catch (Exception exception) {
                    rc.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(400)
                        .end(new Gson().toJson(new ExceptionBody(exception.getMessage())));
                }
            });

        router
            .post("/reportSet")
            .consumes("*/json")
            .handler(rc -> {
                var json = rc.getBodyAsJsonArray();

                // Add to db
                var client = MongoClient.createShared(verticle, new JsonObject(), "vertx-pool");
                var reportSetJson = new JsonObject()
                    .put("reports", json);
                client.save("user-login-sets", reportSetJson, mongoId -> {
                    if (mongoId.succeeded()) {
                        rc
                            .response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(new Gson().toJson(new IdStringBody(mongoId.result())));
                    } else {
                        rc
                            .response()
                            .setStatusCode(400) // Bad Request
                            .end();
                    }
                });

            });

        router
            .get("/reportSet/:id")
            .handler(rc -> {
                String id = rc.request().getParam("id");

                var client = MongoClient.createShared(verticle, new JsonObject(), "vertx-pool");
                JsonObject query = new JsonObject()
                    .put("_id", id);
                client.find("user-login-sets", query, res -> {
                    if (res.succeeded() && !res.result().isEmpty()) {
                        try {
                            var userLoginReportSet = Json.decodeValue(res.result().get(0).encode(), UserLoginReportSet.class);
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(new Gson().toJson(userLoginReportSet.reports()));

                        } catch (DecodeException ignore) {
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(400)
                                .end(new Gson().toJson(new ExceptionBody("Failed to fetch report set")));
                        }
                    } else {
                        rc
                            .response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(400)
                            .end(new Gson().toJson(new ExceptionBody("Failed to fetch report set")));
                    }
                });

            });

        return router;
    }
}
