import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;

public class LibraryServer {
    private static final int PORT = 8080;
    private LibraryManager libraryManager;

    public LibraryServer() {
        libraryManager = new LibraryManager();
    }

    // Método para iniciar o servidor
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado na porta: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, libraryManager).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Classe interna que lida com as requisições de clientes em threads separadas
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

        // Método que executa a thread do cliente

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

        // Método para processar as requisições do cliente
        private void handleRequest(String request) {
            String[] parts = request.split(" ", 2);
            String command = parts[0];

            // Realiza ações com base no comando recebido

            switch (command) {
                case "LISTAR":
                    List<Book> books = libraryManager.listBooks();
                    out.println(new Gson().toJson(books));
                    break;
                case "ADICIONAR":
                    if (parts.length < 2) {
                        out.println("Formato de solicitação inválida para o comando ADICIONAR.");
                        return;
                    }
                    Book book = new Gson().fromJson(parts[1], Book.class);
                    libraryManager.addBook(book);
                    out.println("Livro adicionado com sucesso.");
                    break;
                case "ALUGAR":
                    if (parts.length < 2) {
                        out.println("Formato de solicitação inválida para o comando ALUGAR.");
                        return;
                    }
                    if (libraryManager.rentBook(parts[1])) {
                        out.println("Livro alugado com sucesso.");
                    } else {
                        out.println("Esse livro nao esta disponivel.");
                    }
                    break;
                case "DEVOLVER":
                    if (parts.length < 2) {
                        out.println("Formato de solicitação inválida para o comando DEVOLVER.");
                        return;
                    }
                    if (libraryManager.returnBook(parts[1])) {
                        out.println("Livro devolvido com sucesso.");
                    } else {
                        out.println("Livro nao encontrado.");
                    }
                    break;
                default:
                    out.println("Comando invalido.");
            }
        }
    }

    // Método principal para iniciar o servidor

    public static void main(String[] args) {
        LibraryServer server = new LibraryServer();
        server.start();
    }
}