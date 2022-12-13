package vertxms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import utilities.Requests;
import utilities.bodies.ExceptionBody;
import utilities.bodies.IdBody;
import utilities.bodies.IdStringBody;
import utilities.exceptions.BackendException;
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

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatasetRoutes {

    static HashMap<Long, int[]> cache = new HashMap<>();

    public static Router getRouter(Vertx verticle) {
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

                        var req = Requests.createGet("http://user-ms:8084/validate/" + token);
                        var idBody = Requests.getJsonObject(req, IdBody.class);

                        if (idBody.id() != -1) {
                            var date = LocalDateTime.now();
                            var loginReportJson = new JsonObject()
                                .put("timestamp", date.toString())
                                .put("userId", idBody.id());
                            client.save("user-logins", loginReportJson);

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

                    long userId = -1;
                    try {
                        userId = Long.parseLong(userIdString);
                    } catch (NumberFormatException exception) {
                        throw new BackendException("Bad userId provided", HttpStatus.BAD_REQUEST);
                    }

                    var client = MongoClient.createShared(verticle, new JsonObject(), "vertx-pool");

                    JsonObject query = new JsonObject()
                        .put("userId", userId);
                    client.find("user-logins", query, res -> {
                        if (res.succeeded()) {
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
