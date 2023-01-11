package cn.tuyucheng.taketoday.logging.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Objects;

@Document(collection = "book")
public class Book {

    @MongoId
    private ObjectId id;

    private String bookName;

    private String authorName;

    public ObjectId getId() {
        return id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    //<editor-fold desc="equals方法">
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }
    //</editor-fold>

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}