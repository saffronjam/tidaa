package model;

import com.mongodb.MongoException;
import org.bson.Document;

import java.util.regex.Pattern;

/**
 * Query Book by ISBN
 *
 * @author Elias Alyoussef & Emil Karlsson
 * @implement QueryBookInterface
 */
public class QueryBookByIsbn implements QueryBookInterface {

    private final String isbn;

    public QueryBookByIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public Document getQuery() {
        Pattern regex = Pattern.compile(isbn, Pattern.CASE_INSENSITIVE);
        return new Document("isbn", regex);
    }
}
