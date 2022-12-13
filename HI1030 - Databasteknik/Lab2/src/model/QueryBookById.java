package model;

import org.bson.Document;

/**
 * Query Book by title
 *
 * @author Elias Alyoussef & Emil Karlsson
 * @implements QueryBookInterface
 */
public class QueryBookById implements QueryBookInterface {

    private final int id;

    public QueryBookById(int id) {
        this.id = id;
    }

    @Override
    public Document getQuery() {
        return new Document("id", id);
    }
}
