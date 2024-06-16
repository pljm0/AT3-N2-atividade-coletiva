import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;

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
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, libraryManager).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private LibraryManager libraryManager;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket, LibraryManager libraryManager) {
            this.clientSocket = socket;
            this.libraryManager = libraryManager;
            try {
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Received request: " + request);
                    handleRequest(request);
                }
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private void handleRequest(String request) {
            String[] parts = request.split(" ", 2);
            String command = parts[0];

            switch (command) {
                case "LIST":
                    List<Book> books = libraryManager.listBooks();
                    out.println(new Gson().toJson(books));
                    break;
                case "ADD":
                    if (parts.length < 2) {
                        out.println("Invalid request format for ADD command.");
                        return;
                    }
                    Book book = new Gson().fromJson(parts[1], Book.class);
                    libraryManager.addBook(book);
                    out.println("Book added successfully.");
                    break;
                case "RENT":
                    if (parts.length < 2) {
                        out.println("Invalid request format for RENT command.");
                        return;
                    }
                    if (libraryManager.rentBook(parts[1])) {
                        out.println("Book rented successfully.");
                    } else {
                        out.println("Book not available.");
                    }
                    break;
                case "RETURN":
                    if (parts.length < 2) {
                        out.println("Invalid request format for RETURN command.");
                        return;
                    }
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
    }

    public static void main(String[] args) {
        LibraryServer server = new LibraryServer();
        server.start();
    }
}
