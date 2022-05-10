package utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import utilities.exceptions.BackendException;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class Requests {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType PNG = MediaType.parse("image/png");


    public static <T> T getJsonObject(Request request, Class<T> clazz) throws BackendException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BackendException("Failed to get response", HttpStatus.BAD_REQUEST);
            }

            if (response.body() == null) {
                throw new BackendException("Response is empty", HttpStatus.BAD_REQUEST);
            }

            var data = response.body().byteString().utf8();

            ObjectMapper objectMapper = new ObjectMapper();
            var result = objectMapper.readValue(data, clazz);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("Couldn't parse response", HttpStatus.CONFLICT);
        }
    }

    public static void execute(Request request) throws BackendException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BackendException("Failed to get response", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("Couldn't parse response", HttpStatus.CONFLICT);
        }
    }

    public static Request createGet(String url) {
        return new Request.Builder().url(url).method("GET", null).build();
    }

    public static Request createPost(String url, RequestBody body) {
        return new Request.Builder().url(url).method("POST", body).build();
    }
}