package SmartLibrary.Models;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {
    private String title;
    private String author;
    private boolean isAvailable;
    public Book(String title, String author)
    {
        setTitle(title);
        setAuthor(author);
        isAvailable = true;
    }
    public String getTitle()
    {
        return this.title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getAuthor()
    {
        return this.author;
    }
    public void setAuthor(String author)
    {
        this.author = author;
    }
    public boolean getIsAvailable()
    {
        return this.isAvailable;
    }
    public void setIsAvailable(boolean set)
    {
        this.isAvailable = set;
    }
    @Override
    public String toString()
    {
        return "Book: " + this.getTitle()+ ", author: " + this.getAuthor();
    }
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o==null||getClass()!=o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(this.getTitle(),book.getTitle()) && Objects.equals(this.getAuthor(),book.getAuthor());
    }
}
