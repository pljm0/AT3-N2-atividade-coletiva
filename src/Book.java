import com.google.gson.annotations.SerializedName;

public class Book {
    @SerializedName("titulo")
    private String title;
    @SerializedName("autor")
    private String author;
    @SerializedName("genero")
    private String genre;
    @SerializedName("exemplares")
    private int numberOfCopies;

    public Book(String title, String author, String genre, int numberOfCopies) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.numberOfCopies = numberOfCopies;
    }

    // Getters e Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }
}
