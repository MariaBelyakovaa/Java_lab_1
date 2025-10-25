import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Library {
    private ArrayList<Book> books;
    private static final String FILENAME = "library.txt";
    
    // Константы 
    public static final int MIN_YEAR = 1000;
    public static final int MAX_YEAR = 2100;


    public Library() {
        books = new ArrayList<>();
    }

    // Добавление книги
    public void addBook(Book book) {
        if (book != null) {
            books.add(book);
        }
    }
    // Удаление книги
    public boolean removeBook(int index) {
        return index >= 0 && index < books.size() && books.remove(index) != null;
    }

    public Book getBook(int index) {
        return (index >= 0 && index < books.size()) ? books.get(index) : null;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books); 
    }

    // Поиск книги
    private List<Book> searchBooks(Predicate<Book> condition) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (condition.test(book)) {
                result.add(book);
            }
        }
        return result;
    }

    // Специализированные методы поиска через searchBooks
    public List<Book> searchByTitle(String title) {
        return searchBooks(book -> 
            book.getTitle().toLowerCase().contains(title.toLowerCase()));
    }

    public List<Book> searchByAuthor(String author) {
        return searchBooks(book -> 
            book.getAuthor().toLowerCase().contains(author.toLowerCase()));
    }

    public List<Book> searchByYear(int year) {
        return searchBooks(book -> book.getYear() == year);
    }

    public List<Book> searchByGenre(String genre) {
        return searchBooks(book -> 
            book.getGenre().toLowerCase().contains(genre.toLowerCase()));
    }

    // Вспомогательные методы
    public int getBookCount() {
        return books.size();
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    
    // Проверка корректности года издания
    public static boolean isValidYear(int year) {
        return year >= MIN_YEAR && year <= MAX_YEAR;
    }

    // Работа с файлами
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
            for (Book book : books) {
                writer.println(book.toFileString());
            }
            System.out.println("Данные успешно сохранены в файл: " + FILENAME);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        books.clear();
        File file = new File(FILENAME);
        
        if (!file.exists()) {
            System.out.println("Файл с данными не найден. Будет создан новый.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            int loadedCount = 0;
            int errorCount = 0;
            
            while ((line = reader.readLine()) != null) {
                Book book = Book.fromFileString(line);
                if (book != null) {
                    books.add(book);
                    loadedCount++;
                } else {
                    errorCount++;
                }
            }
            
            System.out.printf("Загружено книг: %d, с ошибками: %d%n", loadedCount, errorCount);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке из файла: " + e.getMessage());
        }
    }
}