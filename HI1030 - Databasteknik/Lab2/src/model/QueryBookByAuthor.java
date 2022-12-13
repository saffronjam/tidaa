package model;

import com.mongodb.MongoException;
import org.bson.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Query Book by Author
 *
 * @author Elias Alyoussef & Emil Karlsson
 * @implement QueryBookInterface
 */
public class QueryBookByAuthor implements QueryBookInterface {
    private final List<Author> authors;
    private final boolean badAuthorsIds;

    public QueryBookByAuthor(List<Author> authors) {
        this.authors = authors;
        badAuthorsIds = authors.isEmpty();
    }

    @Override
    public Document getQuery() {
        if (badAuthorsIds) {
            return null;
        }
        var documentList = new ArrayList<Document>();
        for (var author : authors) {
            documentList.add(new Document("authors", author.getAuthorId()));
        }
        return new Document("$or", documentList);
    }
}
