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
    private static final String FILE_PATH = "C:\\Users\\zerol\\Downloads\\livros.json"; // Caminho do arquivo JSON que armazena os livros
    private List<Book> books;
    private Gson gson;
    public LibraryManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        loadBooks();
    }

    // Método para carregar os livros do arquivo JSON
    private void loadBooks() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type libraryType = new TypeToken<Map<String, List<Book>>>(){}.getType();
            Map<String, List<Book>> library = gson.fromJson(reader, libraryType);
            books = library.get("livros");
        } catch (IOException e) {
            books = new ArrayList<>();
        }
    }

    // Método para salvar os livros no arquivo JSON
    private void saveBooks() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Map<String, List<Book>> library = new HashMap<>();
            library.put("livros", books);
            gson.toJson(library, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para listar todos os livros da biblioteca
    public List<Book> listBooks() {
        return books;
    }

    // Método para adicionar um novo livro à biblioteca
    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    // Método para alugar um livro da biblioteca
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

    // Método para devolver um livro à biblioteca
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
