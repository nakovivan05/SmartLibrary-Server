package SmartLibrary.Models;

import java.util.ArrayList;
import java.util.List;

public class Reader extends User {
    private List<Book> borrowedBooks;
    public Reader(String username, String password)
    {
        super(username,password);
        borrowedBooks = new ArrayList<Book>();
    }
    public List<Book> getBorrowedBooks()
    {
        return this.borrowedBooks;
    }
    @Override
    public UserType getUserType()
    {
        return UserType.READER;
    }
    @Override
    public String toString()
    {
        return "Reader: " + super.toString();
    }
}
