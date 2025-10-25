// Класс Book со следующими полями: название, автор, год издания книги, жанр
public class Book {
    private String title;
    private String author;
    private int year;
    private String genre;

    // Константы для работы с файлами
    private static final String FIELD_SEPARATOR = ";";
    private static final int EXPECTED_FIELD_COUNT = 4;
    private static final int TITLE_INDEX = 0;
    private static final int AUTHOR_INDEX = 1;
    private static final int YEAR_INDEX = 2;
    private static final int GENRE_INDEX = 3;

    public Book(String title, String author, int year, String genre) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
    }

    // Геттеры
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getGenre() { return genre; }

    // Сеттеры
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return String.format("Название: %s, Автор: %s, Год: %d, Жанр: %s", 
                           title, author, year, genre);
    }

    /**
     * Преобразует объект Book в строку для сохранения в файл
     */
    public String toFileString() {
        return String.join(FIELD_SEPARATOR, title, author, String.valueOf(year), genre);
    }

    /**
     * Создает объект Book из строки файла
     * @param line строка в формате "название;автор;год;жанр"
     * @return объект Book или null при ошибке формата
     */
    public static Book fromFileString(String line) {
        try {
            String[] parts = line.split(FIELD_SEPARATOR);
            if (parts.length == EXPECTED_FIELD_COUNT) {
                return new Book(
                    parts[TITLE_INDEX], 
                    parts[AUTHOR_INDEX], 
                    Integer.parseInt(parts[YEAR_INDEX]), 
                    parts[GENRE_INDEX]
                );
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при чтении года издания: " + e.getMessage());
        }
        return null;
    }
}