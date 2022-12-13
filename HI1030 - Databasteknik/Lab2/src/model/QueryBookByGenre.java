package model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Query Book by Genre
 *
 * @author Elias Alyoussef & Emil Karlsson
 * @implement QueryBookInterface
 */
public class QueryBookByGenre implements QueryBookInterface {
    private final String bookGenre;

    public QueryBookByGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    @Override
    public Document getQuery() {
        Document regQuery = new Document()
                .append("$regex", ".*" + bookGenre + ".*")
                .append("$options", "i");
        return new Document("genres", regQuery);
    }
}
