package model;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.util.JSON;
import org.bson.Document;

import com.mongodb.MongoException;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class MongoDb implements BooksDbInterface {

    private MongoClient mongo;
    private MongoDatabase database;
    private String databaseName = "db_books";

    public MongoDb() {
        mongo = new MongoClient("127.0.0.1", 27017);

        MongoCredential credential = MongoCredential.createCredential("", "BooksDb", "".toCharArray());
        database = mongo.getDatabase(databaseName);
    }

    @Override
    public boolean connect(String database) {
        mongo = new MongoClient("127.0.0.1", 27017);
        databaseName = database;
        this.database = mongo.getDatabase(databaseName);
        return true;
    }

    @Override
    public void disconnect() {
        mongo.close();
    }

    @Override
    public List<Book> searchBooksByTitle(String searchFor) throws IOException, MongoException, IllegalStateException {
        return getBooks(new QueryBookByTitle(searchFor));
    }

    @Override
    public List<Book> searchBooksByIsbn(String searchFor) throws IOException, MongoException, IllegalStateException {
        return getBooks(new QueryBookByIsbn(searchFor));
    }

    @Override
    public List<Book> searchBooksByAuthor(String searchFor) throws IOException, MongoException, IllegalStateException {
        return getBooks(new QueryBookByAuthor(getAuthorByFullname(searchFor)));
    }

    @Override
    public List<Book> searchBooksByRating(String searchFor) throws IOException, MongoException, IllegalStateException {
        return getBooks(new QueryBookByRating(getBooks(new QueryAllBooks()), searchFor));
    }

    @Override
    public List<Book> searchBooksByGenre(String searchFor) throws IOException, MongoException, IllegalStateException {
        return getBooks(new QueryBookByGenre(searchFor));
    }

    @Override
    public User getUserByCredentials(String username, String password) throws MongoException, IllegalStateException {
        var documentToFind = new Document().append("username", username).append("password", password);

        FindIterable<Document> iterable = database.getCollection(DbCollections.user).find(documentToFind);

        var userDocument = iterable.first();
        if (userDocument == null) {
            throw new MongoException("Bad credentials");
        }

        return new User(
                (int) userDocument.get("id"),
                (String) userDocument.get("username"),
                (String) userDocument.get("password"),
                (String) userDocument.get("socicalSecNo"),
                User.PermissionLevel.valueOf((String) userDocument.get("permissionLevel"))
        );
    }

    @Override
    public User getUserById(int userId) throws MongoException, IllegalStateException {
        var collection = database.getCollection(DbCollections.user);
        var documentToFind = new Document().append("id", userId);
        var iterable = collection.find(documentToFind);

        var userDocument = iterable.first();
        if (userDocument == null) {
            return new User(0, "Deleted", "", "", User.PermissionLevel.NONE);
        }

        return new User(
                (int) userDocument.get("id"),
                (String) userDocument.get("username"),
                (String) userDocument.get("password"),
                (String) userDocument.get("socicalSecNo"),
                User.PermissionLevel.valueOf((String) userDocument.get("permissionLevel"))
        );
    }

    @Override
    public void addNewBook(Book book) throws MongoException, IllegalStateException, IOException {
        var collection = database.getCollection(DbCollections.book);

        var tryFindIsbn = collection.find(new Document("isbn", book.getIsbn()));
        if (tryFindIsbn.first() != null) {
            throw new MongoException("ISBN already in database (" + book.getIsbn() + ")");
        }


        // Base
        var document = new Document()
                .append("id", getIdByCollection(collection))
                .append("isbn", book.getIsbn())
                .append("title", book.getTitle())
                .append("published", book.getPublished())
                .append("storyline", book.getStoryLine())
                .append("createdBy", book.getCreatedBy().getUserId())
                .append("rating", book.getRating());

        // Authors
        var dbAuthors = new ArrayList<Integer>();
        for (var author : book.getAuthors()) {
            dbAuthors.add(author.getAuthorId());
        }
        document.append("authors", dbAuthors);

        // Genres
        var dbGenres = new ArrayList<String>();
        for (var genre : book.getGenres()) {
            dbGenres.add(genre.toString());
        }
        document.append("genres", dbGenres);

        // Reviews
        var dbReviews = new ArrayList<Document>();
        document.append("reviews", dbReviews);

        collection.insertOne(document);
    }

    @Override
    public void updateBook(int bookId, String newStoryline, ArrayList<Author> newAuthors, ArrayList<Genre> newGenres) throws MongoException, IllegalStateException {
        var collection = database.getCollection(DbCollections.book);

        var updateDocument = new Document();

        // Storyline
        updateDocument.append("storyline", newStoryline);

        // Authors
        if (!newAuthors.isEmpty()) {
            var authorIds = new ArrayList<Integer>();
            for (var author : newAuthors) {
                authorIds.add(author.getAuthorId());
            }
            updateDocument.append("authors", authorIds);
        }

        // Genres
        if (!newAuthors.isEmpty()) {
            var genres = new ArrayList<String>();
            for (var genre : newGenres) {
                genres.add(genre.toString());
            }
            updateDocument.append("genres", genres);
        }

        collection.updateOne(new Document("id", bookId), new Document("$set", updateDocument));
    }

    @Override
    public void removeBook(int bookId) throws MongoException, IllegalStateException {
        var collection = database.getCollection(DbCollections.book);
        collection.deleteOne(new Document("id", bookId));
    }

    @Override
    public void rateBook(int bookId, int userId, Date date, String description, int rating) throws MongoException, IllegalStateException {
        var collection = database.getCollection(DbCollections.book);

        var books = getBooks(new QueryBookById(bookId));
        if (books.isEmpty()) {
            throw new MongoException("Book ID was not found in database");
        }

        // TODO: Uncomment
//        var book = books.get(0);
//        for (var review : book.getReviews()) {
//            if (review.getUser().getUserId() == userId) {
//                throw new MongoException("User have already reviewed book");
//            }
//        }

        var review = new Document()
                .append("writtenBy", userId)
                .append("date", date)
                .append("description", description)
                .append("rating", rating);

        collection.updateOne(Filters.eq("id", bookId), Updates.addToSet("reviews", review));
    }

    @Override
    public void addNewAuthor(Author author) throws MongoException, IllegalStateException, IOException {
        var collection = database.getCollection(DbCollections.author);

        var document = new Document()
                .append("id", getIdByCollection(collection))
                .append("fName", author.getFirstName())
                .append("lName", author.getLastName())
                .append("dateOfBirth", author.getDateOfBirth())
                .append("createdBy", author.getCreatedBy().getUserId());

        collection.insertOne(document);
    }

    @Override
    public List<Author> getAllAuthors() throws MongoException, IllegalStateException {
        var collection = database.getCollection(DbCollections.author);
        var authorIterable = collection.find();
        return getAuthorsByIterable(authorIterable);
    }

    private List<Book> getBooks(QueryBookInterface queryBookInterface) {

/*
db.c.aggregate({$match: {query: "iPad"}}, {$unwind:"$rating"}, {$project:
{_id:0,q:"$query",i:"$rating.inq"}}, {$group:{_id: "$q", av: {$avg:"$i"}}});


db.companies.aggregate([
  { $match:  { "founded_year":2004 } },
  { $project: { founded_year:1,
                moreThanFive: { $gt: [ {$size: "$external_links" }, 5 ] } } },
  { $match: { moreThanFive : true }} ,
])

 */

        var collection = database.getCollection(DbCollections.book);

        var documentToFind = queryBookInterface.getQuery();
        var books = new ArrayList<Book>();
        if (documentToFind == null) {
            // Return early if queryInterface failed to generate document
            return books;
        }
        var bookIterable = collection.find(documentToFind);

        for (var bookDocument : bookIterable) {
            var authors = new ArrayList<Author>();
            for (var id : bookDocument.get("authors", ArrayList.class)) {
                var authorDocumentToFind = new Document().append("id", id);
                FindIterable<Document> authorIterable = database.getCollection(DbCollections.author).find(authorDocumentToFind);
                var authorDocument = authorIterable.first();
                if (authorDocument != null) {
                    User createdBy = getUserById((int) authorDocument.get("createdBy"));
                    Author author = new Author((int) authorDocument.get("id"),
                            (String) authorDocument.get("fName"),
                            (String) authorDocument.get("lName"),
                            (Date) authorDocument.get("dateOfBirth"),
                            createdBy);
                    authors.add(author);
                }
            }

            var genres = new ArrayList<Genre>();
            for (var genre : bookDocument.getList("genres", String.class)) {
                genres.add(Genre.valueOf(genre));
            }

            var reviews = new ArrayList<Review>();
            for (var dbReview : bookDocument.getList("reviews", Document.class)) {
                User writtenBy = getUserById((int) dbReview.get("writtenBy"));
                var review = new Review(
                        writtenBy,
                        (Date) dbReview.get("date"),
                        (String) dbReview.get("description"),
                        (int) dbReview.get("rating")
                );
                reviews.add(review);
            }

            User createdBy = getUserById((int) bookDocument.get("createdBy"));
            float rating = 0.0f;
            for (var review : reviews) {
                rating += review.getRating();
            }
            if (!reviews.isEmpty()) {
                rating /= reviews.size();
            }

            var book = new Book(
                    (int) bookDocument.get("id"),
                    (String) bookDocument.get("isbn"),
                    (String) bookDocument.get("title"),
                    (Date) bookDocument.get("published"),
                    authors,
                    (String) bookDocument.get("storyline"),
                    genres,
                    createdBy,
                    rating,
                    reviews
            );

            books.add(book);
        }
        return books;
    }

    private void ensureDatabaseConnection() throws IllegalStateException {
        if (mongo == null) {
            throw new IllegalStateException("Not connected to database");
        }
    }

    private List<Author> getAuthorByFullname(String fullname) {
        var collection = database.getCollection(DbCollections.author);

        String[] splited = fullname.split("\\s+");

        var documentToFind = new Document();
        if (splited.length == 1) {
            Pattern regex = Pattern.compile(splited[0], Pattern.CASE_INSENSITIVE);
            var documentList = new ArrayList<Document>();
            documentList.add(new Document("fName", regex));
            documentList.add(new Document("lName", regex));
            documentToFind.append("$or", documentList);
        } else if (splited.length == 2) {
            Pattern regexFirst = Pattern.compile(splited[0], Pattern.CASE_INSENSITIVE);
            Pattern regexLast = Pattern.compile(splited[1], Pattern.CASE_INSENSITIVE);

            var documentList = new ArrayList<Document>();
            documentList.add(new Document("fName", regexFirst));
            documentList.add(new Document("lName", regexLast));

            documentToFind.append("$and", documentList);
        }
        var authorIterable = collection.find(documentToFind);

        return getAuthorsByIterable(authorIterable);
    }

    private List<Author> getAuthorsByIterable(Iterable<Document> findIterable) {
        var authors = new ArrayList<Author>();
        for (var authorDocument : findIterable) {
            User createdBy = getUserById((int) authorDocument.get("createdBy"));
            Author author = new Author(
                    (int) authorDocument.get("id"),
                    (String) authorDocument.get("fName"),
                    (String) authorDocument.get("lName"),
                    (Date) authorDocument.get("dateOfBirth"),
                    createdBy);
            authors.add(author);
        }
        return authors;
    }

    private int getIdByCollection(MongoCollection<Document> collection) throws MongoException {
        var idIteratable = collection.find().sort(new Document("id", -1)).limit(1);
        var idObject = idIteratable.first();
        if (idObject == null) {
            return 0;
        }
        return (int) idObject.get("id") + 1;
    }
}
