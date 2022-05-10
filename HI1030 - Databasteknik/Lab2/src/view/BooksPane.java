package view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Pair;
import model.*;
import org.javatuples.Triplet;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;

    private TextField searchField;
    private Label searchStatusInfo;

    private Label loginStatusInfo;

    private MenuBar menuBar;

    private final String isbnPattern = "[0-9]{13}";

    public BooksPane(BooksDbInterface booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }
    
    /**
     * Notify user on input error or exceptions.
     * 
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable(controller);
        initSearchView(controller);
        initMenus(controller);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchStatusInfo, loginStatusInfo);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable(Controller controller) {
        booksTable = new TableView<>();

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, ArrayList<Author>> authorsCol = new TableColumn<>("Authors");
        TableColumn<Book, ArrayList<Genre>> genreCol = new TableColumn<>("Genres");
        TableColumn<Book, User> createdByCol = new TableColumn<>("Created By");
        TableColumn<Book, Float> ratingCol = new TableColumn<>("Rating");
        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol, authorsCol, genreCol, createdByCol, ratingCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        authorsCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genres"));
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("ratingAsString"));

        booksTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                Book book = booksTable.getSelectionModel().getSelectedItem();
                controller.setSelectedBook(book);
            }
        });

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchStatusInfo = new Label("Enter a search string");
        loginStatusInfo = new Label("Not logged in");

        String searchFor = searchField.getText();
        SearchMode mode = searchModeBox.getValue();
        controller.onSearchSelected(searchFor, mode);

        // event handling (dispatch to controller)
        searchField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void initMenus(Controller controller)
    {
        Menu fileMenu = new Menu("File");
        MenuItem connectItem = new MenuItem("Connect");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(connectItem, disconnectItem, exitItem);

        Menu accountMenu = new Menu("Account");
        MenuItem loginItem = new MenuItem("Login");
        MenuItem logoutItem = new MenuItem("Logout");
        accountMenu.getItems().addAll(loginItem, logoutItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addBookItem = new MenuItem("Add book");
        MenuItem addAuthorItem = new MenuItem("Add author");
        manageMenu.getItems().addAll(addBookItem, addAuthorItem);

        Menu selectMenu = new Menu("Select");
        MenuItem removeBookItem = new MenuItem("Remove");
        MenuItem updateBookItem = new MenuItem("Update");
        MenuItem rateBookItem = new MenuItem("Rate");
        MenuItem viewReviewstem = new MenuItem("View reviews");
        selectMenu.getItems().addAll(removeBookItem, updateBookItem, rateBookItem, viewReviewstem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, accountMenu, manageMenu, selectMenu);

        connectItem.setOnAction((final ActionEvent e) ->
        {
            controller.onConnect();
        });

        disconnectItem.setOnAction((final ActionEvent e) ->
        {
            controller.onDisconnect();
        });

        exitItem.setOnAction((final ActionEvent e) ->
        {
            controller.onDisconnect();
            Platform.exit();
        });

        addBookItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() == null )
            {
                showAlertAndWait("You need to be logged in to create a book", Alert.AlertType.INFORMATION);
                return;
            }

            if(!controller.hasUserRight(User.PermissionLevel.CREATE))
            {
                showAlertAndWait("You don't have permission to create a book", Alert.AlertType.INFORMATION);
                return;
            }

            var dialog = createBasicDialog("Create book", "Enter details", "Create");
            var grid = createBasicGridPane();

            var title = new TextField();
            var isbn = new TextField();
            var publishedDate = new DatePicker();
            var storyline = new TextArea();
            var authorsChosenLabel = new Label();
            var genresChosenLabel = new Label();

            ObservableList<Author> authorComboBoxOptions = FXCollections.observableArrayList();
            var chosenAuthors = new ArrayList<Author>();

            List<Author> databaseAuthors = controller.getAllAuthors();
            if(databaseAuthors != null) {
                authorComboBoxOptions.addAll(databaseAuthors);
            }
            var authorComboBox = new ComboBox(authorComboBoxOptions);
            authorComboBox.setOnAction(actionEvent ->
            {
                var candidateAuthor = (Author)authorComboBox.getValue();
                if(chosenAuthors.contains(candidateAuthor))
                {
                    chosenAuthors.remove(candidateAuthor);
                }
                else {
                    chosenAuthors.add(candidateAuthor);
                }
                authorsChosenLabel.setText(chosenAuthors.toString());
            });

            var chosenGenres = new ArrayList<Genre>();
            ObservableList<Genre> genreComboBoxOptions = FXCollections.observableArrayList();
            genreComboBoxOptions.addAll(Arrays.asList(Genre.values()));
            var genreComboBox = new ComboBox(genreComboBoxOptions);
            genreComboBox.setOnAction(actionEvent ->
            {
                var candidateGenre = (Genre)genreComboBox.getValue();
                if(chosenGenres.contains(candidateGenre))
                {
                    chosenGenres.remove(candidateGenre);
                }
                else {
                    chosenGenres.add(candidateGenre);
                }
                genresChosenLabel.setText(chosenGenres.toString());
            });

            title.setPromptText("Title");
            isbn.setPromptText("ISBN");
            publishedDate.setPromptText("Published date");
            storyline.setPromptText("Story line");

            int y = 0;
            grid.add(new Label("Title:"), 0, y); grid.add(title, 1, y);
            y++;
            grid.add(new Label("ISBN:"), 0, y); grid.add(isbn, 1, y);
            y++;
            grid.add(new Label("Author:"), 0, y); grid.add(authorComboBox, 1, y);
            y++;
            grid.add(authorsChosenLabel, 1, y);
            y++;
            grid.add(new Label("Published date:"), 0, y); grid.add(publishedDate, 1, y);
            y++;
            grid.add(new Label("Story line:"), 0, y); grid.add(storyline, 1, y);
            y++;
            grid.add(new Label("Genre:"), 0, y); grid.add(genreComboBox, 1, y);
            y++;
            grid.add(genresChosenLabel, 1, y);

            dialog.getKey().getDialogPane().setContent(grid);
            Platform.runLater(title::requestFocus);

            Button createButton = (Button)dialog.getKey().getDialogPane().lookupButton(dialog.getValue());
            createButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if(title.getText().isEmpty() ||
                            isbn.getText().isEmpty() || chosenAuthors.isEmpty() ||
                            chosenGenres.isEmpty() || publishedDate.getValue() == null)
                    {
                        showAlertAndWait("Every field must contain a value (Except storyline)", Alert.AlertType.INFORMATION);
                        event.consume();
                    }
                    else if(!isbn.getText().matches(isbnPattern))
                    {
                        showAlertAndWait("ISBN must match [0-9]{13}", Alert.AlertType.INFORMATION);
                        event.consume();
                    }
                }
            });

            dialog.getKey().setResultConverter(dialogButton -> {
                if (dialogButton == dialog.getValue())
                {
                    var convertedDate = java.sql.Date.valueOf(publishedDate.getValue() );
                    return new Book(isbn.getText(), title.getText(), convertedDate, chosenAuthors, storyline.getText(), chosenGenres, controller.getUserSession().getUser(), 0.0f, new ArrayList<>() );
                }
                return null;
            });

            Optional<Book> result = dialog.getKey().showAndWait();

            result.ifPresent(createBook -> {
                controller.onCreateBook(result.get());
            });
        });

        removeBookItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() == null )
            {
                showAlertAndWait("You need to be logged in to remove a book", Alert.AlertType.INFORMATION);
                return;
            }

            if(!controller.hasUserRight(User.PermissionLevel.MODIFY))
            {
                showAlertAndWait("You don't have permission to remove a book", Alert.AlertType.INFORMATION);
                return;
            }

            if(controller.getSelectedBook() == null )
            {
                showAlertAndWait("No selected book", Alert.AlertType.INFORMATION);
                return;
            }

            var dialog = createBasicDialog("Remove book", "Confirm removal of book", "Confirm");

            dialog.getKey().setResultConverter(dialogButton -> dialogButton == dialog.getValue());

            Optional<Boolean> result = dialog.getKey().showAndWait();

            result.ifPresent(createBook -> {
                if(result.get())
                {
                    controller.onRemoveSelectedBook();
                }
            });
        });

        updateBookItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() == null )
            {
                showAlertAndWait("You need to be logged in to update a book", Alert.AlertType.INFORMATION);
                return;
            }

            if(!controller.hasUserRight(User.PermissionLevel.MODIFY))
            {
                showAlertAndWait("You don't have permission to update a book", Alert.AlertType.INFORMATION);
                return;
            }

            if(controller.getSelectedBook() == null )
            {
                showAlertAndWait("No selected book", Alert.AlertType.INFORMATION);
                return;
            }

            var dialog = createBasicDialog("Update book", "Enter new information. Leave blank to not change", "Update");
            var grid = createBasicGridPane();

            Book selectedBook = controller.getSelectedBook();

            var storyline = new TextArea(selectedBook.getStoryLine());
            var authorsChosenLabel = new Label();
            var genresChosenLabel = new Label();

            ObservableList<Author> authorComboBoxOptions = FXCollections.observableArrayList();
            var chosenAuthors = selectedBook.getAuthors();

            List<Author> databaseAuthors = controller.getAllAuthors();
            if(databaseAuthors != null) {
                authorComboBoxOptions.addAll(databaseAuthors);
            }
            var authorComboBox = new ComboBox(authorComboBoxOptions);
            authorComboBox.setOnAction(actionEvent ->
            {
                var candidateAuthor = (Author)authorComboBox.getValue();
                if(chosenAuthors.contains(candidateAuthor))
                {
                    chosenAuthors.remove(candidateAuthor);
                }
                else {
                    chosenAuthors.add(candidateAuthor);
                }
                authorsChosenLabel.setText(chosenAuthors.toString());
            });

            {
                authorsChosenLabel.setText(chosenAuthors.toString());
            }

            var chosenGenres = selectedBook.getGenres();
            ObservableList<Genre> genreComboBoxOptions = FXCollections.observableArrayList();
            genreComboBoxOptions.addAll(Arrays.asList(Genre.values()));
            var genreComboBox = new ComboBox(genreComboBoxOptions);
            genreComboBox.setOnAction(actionEvent ->
            {
                var candidateGenre = (Genre) genreComboBox.getValue();
                if (chosenGenres.contains(candidateGenre)) {
                    chosenGenres.remove(candidateGenre);
                } else {
                    chosenGenres.add(candidateGenre);
                }
                genresChosenLabel.setText(chosenGenres.toString());
            });

            {
                genresChosenLabel.setText(chosenGenres.toString());
            }

            grid.add(new Label("Storyline"), 0, 0);
            grid.add(storyline, 1, 0);
            grid.add(new Label("Authors"), 0, 1);
            grid.add(authorComboBox, 1, 1);
            grid.add(authorsChosenLabel, 1, 2);
            grid.add(new Label("Genres"), 0, 3);
            grid.add(genreComboBox, 1, 3);
            grid.add(genresChosenLabel, 1, 4);

            dialog.getKey().getDialogPane().setContent(grid);
            Platform.runLater(storyline::requestFocus);

            Button updateButton = (Button)dialog.getKey().getDialogPane().lookupButton(dialog.getValue());
            updateButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (chosenAuthors.isEmpty() || chosenGenres.isEmpty())
                    {
                        showAlertAndWait("Every field must contain a value (Except storyline)", Alert.AlertType.INFORMATION);
                        event.consume();
                    }
                }
            });

            dialog.getKey().setResultConverter(dialogButton -> {
                if (dialogButton == dialog.getValue()) {
                    return new Triplet<>(storyline.getText(), chosenAuthors, chosenGenres);
                }
                return null;
            });

            Optional<Triplet<String, ArrayList<Author>, ArrayList<Genre>>> result = dialog.getKey().showAndWait();

            result.ifPresent(updateBook -> {
                var data = result.get();
                controller.onUpdateSelectedBook(data.getValue0(), data.getValue1(), data.getValue2());
            });
        });

        rateBookItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() == null )
            {
                showAlertAndWait("You need to be logged in to rate a book", Alert.AlertType.INFORMATION);
                return;
            }

            if(controller.getSelectedBook() == null )
            {
                showAlertAndWait("No selected book", Alert.AlertType.INFORMATION);
                return;
            }

            var dialog = createBasicDialog("Rate book", "Enter details", "Rate");
            var grid = createBasicGridPane();

            ObservableList<Integer> ratingOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5);
            var descriptionTextArea = new TextArea();
            var ratingComboBox = new ComboBox(ratingOptions);

            grid.add(new Label("Decription"), 0, 0);
            grid.add(descriptionTextArea, 1, 0);
            grid.add(new Label("Rating"), 0, 1);
            grid.add(ratingComboBox, 1, 1);

            dialog.getKey().getDialogPane().setContent(grid);
            Platform.runLater(descriptionTextArea::requestFocus);

            Button rateItem = (Button)dialog.getKey().getDialogPane().lookupButton(dialog.getValue());
            rateItem.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (descriptionTextArea.getText().isEmpty() || ratingComboBox.getValue() == null)
                    {
                        showAlertAndWait("Every field must contain a value", Alert.AlertType.INFORMATION);
                        event.consume();
                    }
                }
            });

            dialog.getKey().setResultConverter(dialogButton -> {
                if (dialogButton == dialog.getValue()) {
                    return new Pair<>(descriptionTextArea.getText(), (Integer) ratingComboBox.getValue());
                }
                return null;
            });

            Optional<Pair<String, Integer>> result = dialog.getKey().showAndWait();

            result.ifPresent(rateBook -> {
                var data = result.get();
                controller.onRateSelectedBook(data.getKey(), data.getValue());
            });
        });

        viewReviewstem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getSelectedBook() == null )
            {
                showAlertAndWait("No selected book", Alert.AlertType.INFORMATION);
                return;
            }

            var dialog = new Dialog<>();
            dialog.setTitle("Reviews");

            var buttonType = new ButtonType("Dismiss", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonType);


            var grid = createBasicGridPane();

            var reviews = controller.getSelectedBook().getReviews();
            if(reviews.isEmpty())
            {
                grid.add(new Label("No reviews"), 0, 0);
            }
            else
            {
                grid.add(new Label("User"), 0, 0);
                grid.add(new Label("Rating"), 1, 0);
                grid.add(new Label("Description"), 2, 0);
                grid.add(new Label("Date"), 3, 0);
                for (int i = 0; i < reviews.size(); i++) {
                    int y = i * 2 + 1;
                    grid.add(new Separator(), 0, y);
                    grid.add(new Separator(), 1, y);
                    grid.add(new Separator(), 2, y);
                    grid.add(new Separator(), 3, y);

                    Label descriptionLabel = new Label(reviews.get(i).getDescription());
                    descriptionLabel.setWrapText(true);
                    y++;
                    grid.add(new Label( reviews.get(i).getUser().getUsername()), 0, y);
                    grid.add(new Label(String.valueOf(reviews.get(i).getRating())), 1, y);
                    grid.add(descriptionLabel, 2, y);
                    grid.add(new Label(reviews.get(i).getDate().toString()), 3, y);
                }
            }

            dialog.getDialogPane().setContent(grid);
            dialog.showAndWait();
        });

        addAuthorItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() == null )
            {
                showAlertAndWait("You need to be logged in to create an author", Alert.AlertType.INFORMATION);
                return;
            }

            if(!controller.hasUserRight(User.PermissionLevel.CREATE))
            {
                showAlertAndWait("You don't have permission to create an author", Alert.AlertType.INFORMATION);
                return;
            }

            var authors = controller.getAllAuthors();
            // Create the author dialog.
            var dialog = createBasicDialog("Add author", "Enter details", "Add");
            var authorGrid = createBasicGridPane();

            var authorFNameTextfield = new TextField();
            var authorlNameTextfield = new TextField();
            var authorDatePicker = new DatePicker();

            authorFNameTextfield.setPromptText("Author first name:");
            authorlNameTextfield.setPromptText("Author last name:");
            authorDatePicker.setPromptText("Date of birth:");

            authorGrid.add(authorFNameTextfield, 1, 0);
            authorGrid.add(authorlNameTextfield, 1, 1);
            authorGrid.add(authorDatePicker, 1, 2);

            dialog.getKey().getDialogPane().setContent(authorGrid);

            Platform.runLater(authorFNameTextfield::requestFocus);

            Button createButton = (Button)dialog.getKey().getDialogPane().lookupButton(dialog.getValue());
            createButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (authorFNameTextfield.getText().isEmpty() ||
                        authorlNameTextfield.getText().isEmpty() ||
                        authorDatePicker.getValue() == null
                    )
                    {
                        showAlertAndWait("Every field must contain a value", Alert.AlertType.INFORMATION);
                        event.consume();
                    }
                }
            });

            dialog.getKey().setResultConverter(dialogButton -> {
                if (dialogButton == dialog.getValue()) {
                    var convertedDate = java.sql.Date.valueOf(authorDatePicker.getValue() );
                    return new Author(authorFNameTextfield.getText(), authorlNameTextfield.getText(), convertedDate, controller.getUserSession().getUser());
                }
                return null;
            });

            Optional<Author> result = dialog.getKey().showAndWait();

            result.ifPresent(createAuthor -> {
                var data = result.get();
                controller.onCreateAuthor(data);
            });

        });

        loginItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() != null)
            {
                showAlertAndWait("You are already logged in.\nLog out first.", Alert.AlertType.INFORMATION);
                return;
            }

            var dialog = createBasicDialog("Login", "Enter credentials", "Login");
            var grid = createBasicGridPane();

            var usernameTextfield = new TextField();
            var passwordPasswordfield = new PasswordField();

            usernameTextfield.setPromptText("Username");
            passwordPasswordfield.setPromptText("Password");

            grid.add(usernameTextfield, 1, 0);
            grid.add(passwordPasswordfield, 1, 1);

            dialog.getKey().getDialogPane().setContent(grid);

            Platform.runLater(usernameTextfield::requestFocus);

            dialog.getKey().setResultConverter(dialogButton -> {
                if (dialogButton == dialog.getValue()) {
                    return new Pair<>(usernameTextfield.getText(), passwordPasswordfield.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.getKey().showAndWait();
            result.ifPresent(login -> {
                var data = result.get();
                controller.onLogin(data.getKey(), data.getValue());
            });
        });

        logoutItem.setOnAction((final ActionEvent e) ->
        {
            if(controller.getUserSession() == null)
            {
                showAlertAndWait("You are already logged out.", Alert.AlertType.INFORMATION);
                return;
            }
            controller.onLogout();
        });
    }

    /**
     * set the number of results
     * @param label
     */
    public void setSearchStatusInfo(String label) {
        searchStatusInfo.setText(label);
    }

    /**
     * set Info if a user has Logged in
     * @param label
     */
    public void setLoginStatusInfo(String label) {
        loginStatusInfo.setText(label);
    }

    private Pair<Dialog, ButtonType> createBasicDialog(String title, String header, String buttonName)
    {
        var dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        var buttonType = new ButtonType(buttonName, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

        return new Pair<>(dialog, buttonType);
    }

    private GridPane createBasicGridPane()
    {
        var gridPane =new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));
        return gridPane;
    }

}
