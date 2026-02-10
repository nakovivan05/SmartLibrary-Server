package SmartLibrary;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private final static String USERS_FILENAME = "users.bin";
    private final static String BOOKS_FILENAME = "books.bin";
    private final Object lock;
    private ServerSocket server;
    public Server()
    {
        lock = new Object();
        initAdmin();
        initBooks();
    }
    public void initAdmin ()
    {
        if(new File(USERS_FILENAME).exists())
        {
            return;
        }
        synchronized (lock)
        {
            List<User> users = new ArrayList<>();
            users.add(new Admin("admin_ivan","Bulgaria681"));
            saveUsers(users);
        }
    }
    public void initBooks()
    {
        if(new File(BOOKS_FILENAME).exists())
        {
            return;
        }
        synchronized (lock)
        {
            List<Book>books = new ArrayList<>();
            books.add(new Book("Harry Potter","J.K.Rowling"));
            saveBooks(books);
        }
    }
    public List<User> loadUsers()
    {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(USERS_FILENAME)))
        {
            return (List<User>) in.readObject();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public void saveUsers(List<User>users)
    {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILENAME)))
        {
            out.writeObject(users);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public List<Book> loadBooks()
    {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(BOOKS_FILENAME)))
        {
            return (List<Book>) in.readObject();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public void saveBooks(List<Book>books)
    {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BOOKS_FILENAME)))
        {
            out.writeObject(books);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void start()
    {
        try {
            server = new ServerSocket(8080);
            while (true) {
                Socket client = server.accept();
                Thread clientThread = new Thread(() ->
                {
                    Scanner sc = null;
                    PrintStream out = null;
                    try {
                        sc = new Scanner(client.getInputStream());
                        out = new PrintStream(client.getOutputStream());
                        userMenu(sc, out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (sc != null)
                            sc.close();
                        if (out != null)
                            out.close();
                    }
                });
                clientThread.start();

            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }
    private void userMenu(Scanner sc, PrintStream out) {
            out.println("Choose 1 to login or else to exit: ");
            String choice = sc.nextLine();
            if (choice.equals("1"))
            {
                User user = loginUser(sc, out);
                if (user != null)
                {
                    if (user instanceof Admin)
                    {
                        adminMenu(sc, out, (Admin) user);
                    }
                    else
                    {
                        readerMenu(sc, out, (Reader) user);
                    }
                }
            }
            else
            {
                out.println("Bye!");
            }
        }

    private User loginUser(Scanner sc, PrintStream out) {
        out.println("Enter username:");
        String username = sc.nextLine();
        out.println("Enter password:");
        String password = sc.nextLine();
        synchronized (lock)
        {
            List<User> users = loadUsers();
            for (User u : users) {
                if (u.getUsername().equals(username) && u.getPassword().equals(password))
                {
                    return u;
                }
            }
        }
        out.println("Invalid login!");
        return null;
    }

    private void adminMenu(Scanner sc, PrintStream out, Admin admin) {
        while (true) {
            out.println("Hi Admin! Choose 1 to register user, 2 to add book or else to exit:");
            String choice = sc.nextLine();
            if (choice.equals("1"))
            {
                registerReader(sc, out);
            }
            else if (choice.equals("2"))
            {
                addBook(sc, out);
            }
            else
            {
                break;
            }
        }
    }

    private void registerReader(Scanner sc, PrintStream out) {
        out.println("Enter username:");
        String username = sc.nextLine();
        out.println("Enter password:");
        String password = sc.nextLine();
        try {
            User newReader = UserFactory.createUser(UserType.READER, username, password);
            synchronized (lock) {
                List<User> users = loadUsers();
                users.add(newReader);
                saveUsers(users);
            }
            out.println("Reader registered!");
        }
        catch (ValidationException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void addBook(Scanner sc, PrintStream out) {
        out.println("Enter title:");
        String title = sc.nextLine();
        out.println("Enter author:");
        String author = sc.nextLine();
        synchronized (lock) {
            List<Book> books = loadBooks();
            books.add(new Book(title, author));
            saveBooks(books);
        }
        out.println("Book added!");
    }

    private void readerMenu(Scanner sc, PrintStream out, Reader reader) {
        while (true) {
            out.println("Choose 1 to see all the books, 2 to borrow a book, 3 to return a book or 4 to exit");
            String choice = sc.nextLine();
            if (choice.equals("1"))
            {
                synchronized (lock)
                {
                    for(Book book:loadBooks())
                    {
                        out.println(book.toString());
                    }
                }
            }
            else if (choice.equals("2"))
            {
                borrowBook(sc, out, reader);
            }
            else if (choice.equals("3"))
            {
                returnBook(sc, out, reader);
            }
            else
            {
                break;
            }
        }
    }

    private void borrowBook(Scanner sc, PrintStream out, Reader reader) {
        out.println("Enter book title:");
        String title = sc.nextLine();
        synchronized (lock) {
            List<Book> books = loadBooks();
            List<User> users = loadUsers();
            for (Book b : books) {
                if (b.getTitle().equalsIgnoreCase(title) && b.getIsAvailable()) {
                    b.setIsAvailable(false);
                    for (User u : users) {
                        if (u.getUsername().equals(reader.getUsername()))
                        {
                            ((Reader) u).getBorrowedBooks().add(b);
                            break;
                        }
                    }
                    saveBooks(books);
                    saveUsers(users);
                    out.println("Book borrowed!");
                    return;
                }
            }
        }
        out.println("Book is not available!");
    }

    private void returnBook(Scanner sc, PrintStream out, Reader reader) {
        out.println("Enter book title to return:");
        String title = sc.nextLine();
        synchronized (lock) {
            List<Book> books = loadBooks();
            List<User> users = loadUsers();
            for (User u : users) {
                if (u.getUsername().equals(reader.getUsername())) {
                    Reader r = (Reader) u;
                    Book found = null;
                    for (Book b : r.getBorrowedBooks()) {
                        if (b.getTitle().equalsIgnoreCase(title)) {
                            found = b;
                            break;
                        }
                    }
                    if (found != null) {
                        r.getBorrowedBooks().remove(found);
                        for (Book b : books) {
                            if (b.getTitle().equalsIgnoreCase(title)) {
                                b.setIsAvailable(true);
                                break;
                            }
                        }
                        saveBooks(books);
                        saveUsers(users);
                        out.println("Book returned!");
                        return;
                    }
                }
            }
        }
        out.println("You don`t have this book.");
    }

}
