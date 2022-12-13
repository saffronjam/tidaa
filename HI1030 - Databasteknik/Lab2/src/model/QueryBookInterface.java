package model;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

/**
 * interface for Query Book
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public interface QueryBookInterface {
    /**
     * get Prepared Statement depending on searching type
     * @return a DBObject
     * @throw MongoException
     */
    Document getQuery();
}

