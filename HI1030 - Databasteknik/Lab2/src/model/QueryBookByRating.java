package model;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import javafx.util.Pair;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Query Book by Rating
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class QueryBookByRating implements QueryBookInterface {
    private List<Integer> coll = new ArrayList<Integer>();
    private final boolean invalidRating;

    public QueryBookByRating(List<Book> list, String rating) {
        invalidRating = rating == null || rating.isEmpty() || !rating.matches("[0-9]+");
        if (!invalidRating) {
            float ratingFloat = Float.parseFloat(rating);
            for (var book : list) {
                float tmp = 0.0f;
                if (!book.getReviews().isEmpty()) {
                    for (var review : book.getReviews()) {
                        tmp += review.getRating();
                    }
                    tmp /= book.getReviews().size();
                }
                float diff = tmp - ratingFloat;
                if (ratingFloat >= 1.0f && diff > -0.5f && diff < 0.5f) {
                    coll.add(book.getBookId());
                }
            }
        }
    }

    @Override
    public Document getQuery() {
        if (invalidRating || coll.isEmpty()) {
            return new Document("title", "INVALID");
        } else {
            var documentList = new ArrayList<Document>();
            for (var id : coll) {
                documentList.add(new Document("id", id));
            }
            return new Document("$or", documentList);
        }
    }
}
