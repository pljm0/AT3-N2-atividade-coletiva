import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LibraryManager {
    private static final String FILE_PATH = "C:/Users/zerol/Downloads/livros.json";
    private List<Book> books;
    private Gson gson;

    public LibraryManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        loadBooks();
    }

    private void loadBooks() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type libraryType = new TypeToken<Map<String, List<Book>>>(){}.getType();
            Map<String, List<Book>> library = gson.fromJson(reader, libraryType);
            books = library.get("livros");
        } catch (IOException e) {
            books = new ArrayList<>();
        }
    }

    private void saveBooks() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Map<String, List<Book>> library = new HashMap<>();
            library.put("livros", books);
            gson.toJson(library, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Book> listBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    public boolean rentBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title) && book.getNumberOfCopies() > 0) {
                book.setNumberOfCopies(book.getNumberOfCopies() - 1);
                saveBooks();
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                book.setNumberOfCopies(book.getNumberOfCopies() + 1);
                saveBooks();
                return true;
            }
        }
        return false;
    }
}
