import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class LibraryServer {
    private static final int PORT = 12345;
    private LibraryManager libraryManager;

    public LibraryServer() {
        libraryManager = new LibraryManager();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String request = in.readLine();
                    handleRequest(request, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(String request, PrintWriter out) {
        String[] parts = request.split(" ", 2);
        String command = parts[0];

        switch (command) {
            case "LIST":
                List<Book> books = libraryManager.listBooks();
                out.println(new Gson().toJson(books));
                break;
            case "ADD":
                Book book = new Gson().fromJson(parts[1], Book.class);
                libraryManager.addBook(book);
                out.println("Book added successfully.");
                break;
            case "RENT":
                if (libraryManager.rentBook(parts[1])) {
                    out.println("Book rented successfully.");
                } else {
                    out.println("Book not available.");
                }
                break;
            case "RETURN":
                if (libraryManager.returnBook(parts[1])) {
                    out.println("Book returned successfully.");
                } else {
                    out.println("Book not found.");
                }
                break;
            default:
                out.println("Invalid command.");
        }
    }

    public static void main(String[] args) {
        LibraryServer server = new LibraryServer();
        server.start();
    }
}
