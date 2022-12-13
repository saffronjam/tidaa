package model;

import org.bson.Document;

import java.util.regex.Pattern;

/**
 * Query Book by title
 *
 * @author Elias Alyoussef & Emil Karlsson
 * @implements QueryBookInterface
 */
public class QueryBookByTitle implements QueryBookInterface {

    private final String title;

    public QueryBookByTitle(String title) {
        this.title = title;
    }

    @Override
    public Document getQuery() {
        Pattern regex = Pattern.compile(title, Pattern.CASE_INSENSITIVE);
        return new Document("title", regex);
    }
}
