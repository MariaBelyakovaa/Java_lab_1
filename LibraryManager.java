import java.util.List;
import java.util.Scanner;

public class LibraryManager {
    private Library library;
    private Scanner scanner;
    
    // Константы меню
    private static final int MENU_ADD_BOOK = 1;
    private static final int MENU_EDIT_BOOK = 2;
    private static final int MENU_SHOW_ALL = 3;
    private static final int MENU_SEARCH = 4;
    private static final int MENU_SAVE = 5;
    private static final int MENU_LOAD = 6;
    private static final int MENU_EXIT = 7;
    
    private static final int SEARCH_BY_TITLE = 1;
    private static final int SEARCH_BY_AUTHOR = 2;
    private static final int SEARCH_BY_YEAR = 3;
    private static final int SEARCH_BY_GENRE = 4;

    public LibraryManager() {
        library = new Library();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();
        manager.run();
    }

    public void run() {
        library.loadFromFile();
        
        while (true) {
            showMenu();
            int choice = getIntInput("Выберите пункт меню: ");
            
            switch (choice) {
                case MENU_ADD_BOOK -> addBook();
                case MENU_EDIT_BOOK -> editBook();
                case MENU_SHOW_ALL -> showAllBooks();
                case MENU_SEARCH -> searchBooks();
                case MENU_SAVE -> library.saveToFile();
                case MENU_LOAD -> library.loadFromFile();
                case MENU_EXIT -> {
                    System.out.println("Выход из программы");
                    library.saveToFile();
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n=== Менеджер библиотеки ===");
        System.out.println(MENU_ADD_BOOK + ". Добавить книгу");
        System.out.println(MENU_EDIT_BOOK + ". Редактировать книгу");
        System.out.println(MENU_SHOW_ALL + ". Показать все книги");
        System.out.println(MENU_SEARCH + ". Найти книгу");
        System.out.println(MENU_SAVE + ". Сохранить в файл");
        System.out.println(MENU_LOAD + ". Загрузить из файла");
        System.out.println(MENU_EXIT + ". Выход");
    }

    private void addBook() {
        System.out.println("\n=== Добавление новой книги ===");
        
        String title = getNonEmptyInput("Введите название: ");
        String author = getNonEmptyInput("Введите автора: ");
        int year = getValidYearInput("Введите год издания: ");
        String genre = getNonEmptyInput("Введите жанр: ");
        
        Book book = new Book(title, author, year, genre);
        library.addBook(book);
        System.out.println("Книга успешно добавлена!");
    }

    private void editBook() {
        if (library.isEmpty()) {
            System.out.println("Так как библиотека пуста, книг для редактирования нет");
            return;
        }
        
        showAllBooks();
        int index = getIntInput("Введите номер книги для редактирования: ") - 1;
        
        Book book = library.getBook(index);
        if (book == null) {
            System.out.println("Неверный номер книги");
            return;
        }
        
        System.out.println("Редактирование книги: " + book);
        
        updateField("название", book::setTitle);
        updateField("автора", book::setAuthor);
        updateYearField(book);
        updateField("жанр", book::setGenre);
        
        System.out.println("Книга успешно отредактирована!");
    }

    // Методы для ввода данных
    private String getNonEmptyInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Поле не может быть пустым");
        }
    }

    private int getValidYearInput(String prompt) {
        while (true) {
            int year = getIntInput(prompt);
            if (Library.isValidYear(year)) {
                return year;
            }
            System.out.printf("Год должен быть между %d и %d%n", 
                Library.MIN_YEAR, Library.MAX_YEAR);
        }
    }

    private void updateField(String fieldName, java.util.function.Consumer<String> setter) {
        System.out.printf("Введите новый %s (оставьте пустым для сохранения текущего): ", fieldName);
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            setter.accept(input);
        }
    }

    private void updateYearField(Book book) {
        Integer year = getOptionalIntInput("Введите новый год издания (оставьте пустым для сохранения текущего): ");
        if (year != null && Library.isValidYear(year)) {
            book.setYear(year);
        } else if (year != null) {
            System.out.println("Некорректный год. Значение не изменено");
        }
    }

    private void showAllBooks() {
        if (library.isEmpty()) {
            System.out.println("Библиотека пуста");
            return;
        }
        
        System.out.println("\n=== Список всех книг ===");
        List<Book> allBooks = library.getAllBooks();
        for (int i = 0; i < allBooks.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, allBooks.get(i));
        }
    }

    private void searchBooks() {
        if (library.isEmpty()) {
            System.out.println("Библиотека пуста. Нечего искать");
            return;
        }
        
        System.out.println("\n=== Поиск книг ===");
        System.out.println(SEARCH_BY_TITLE + ". По названию");
        System.out.println(SEARCH_BY_AUTHOR + ". По автору");
        System.out.println(SEARCH_BY_YEAR + ". По году");
        System.out.println(SEARCH_BY_GENRE + ". По жанру");
        
        int choice = getIntInput("Выберите тип поиска: ");
        List<Book> foundBooks = performSearch(choice);
        
        displaySearchResults(foundBooks);
    }

    private List<Book> performSearch(int choice) {
        return switch (choice) {
            case SEARCH_BY_TITLE -> {
                String title = getNonEmptyInput("Введите название для поиска: ");
                yield library.searchByTitle(title);
            }
            case SEARCH_BY_AUTHOR -> {
                String author = getNonEmptyInput("Введите автора для поиска: ");
                yield library.searchByAuthor(author);
            }
            case SEARCH_BY_YEAR -> {
                int year = getValidYearInput("Введите год для поиска: ");
                yield library.searchByYear(year);
            }
            case SEARCH_BY_GENRE -> {
                String genre = getNonEmptyInput("Введите жанр для поиска: ");
                yield library.searchByGenre(genre);
            }
            default -> {
                System.out.println("Неверный выбор.");
                yield List.of();
            }
        };
    }

    private void displaySearchResults(List<Book> foundBooks) {
        if (foundBooks.isEmpty()) {
            System.out.println("Книги не найдены.");
        } else {
            System.out.println("\n=== Найденные книги ===");
            for (int i = 0; i < foundBooks.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, foundBooks.get(i));
            }
        }
    }

    // Методы для ввода чисел
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введите целое число");
            }
        }
    }

    private Integer getOptionalIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введите целое число или оставьте пустым");
            }
        }
    }
}