package model;

import org.bson.Document;

import java.util.regex.Pattern;

/**
 * Query Book by title
 *
 * @author Elias Alyoussef & Emil Karlsson
 * @implements QueryBookInterface
 */
public class QueryAllBooks implements QueryBookInterface {
    @Override
    public Document getQuery() {
        Pattern regex = Pattern.compile("", Pattern.CASE_INSENSITIVE);
        return new Document("title", regex);
    }
}
