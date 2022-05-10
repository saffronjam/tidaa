package model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Immutable container of related data
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class Author {
    private final int authorId;
    private final String fName;
    private final String lName;
    private final Date dateOfBirth;
    private final User createdBy;

    /**
     * Create an author
     * @param authorId
     * @param fName
     * @param lName
     * @param dateOfBirth
     * @param createdBy
     */
    public Author(int authorId, String fName, String lName, Date dateOfBirth, User createdBy) {
        this.authorId = authorId;
        this.fName = fName;
        this.lName = lName;
        this.dateOfBirth = dateOfBirth;
        this.createdBy = createdBy;
    }

    /**
     * Create an author without authorId
     * Used to temporarily store information before it exists in the database
     * @param fName
     * @param lName
     * @param dateOfBirth
     * @param createdBy
     */

    public Author(String fName, String lName, Date dateOfBirth, User createdBy) {
        this(-1 ,fName, lName, dateOfBirth, createdBy);
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public int getAuthorId() {
        return authorId;
    }
    public String getFirstName() {
        return fName;
    }
    public String getLastName() {
        return lName;
    }
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
    public User getCreatedBy() { return createdBy; }

    @Override
    public String toString(){
        return getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Author && getAuthorId() == ((Author) obj).getAuthorId();
    }
}
