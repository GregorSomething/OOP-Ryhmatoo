package oop.ryhmatoo.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oop.ryhmatoo.client.socket.ServerConnection;
import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GUIApp extends Application {
    private ServerConnection conn;

    private String user;
    private Channel selectedChannel;

    /**
     * Starts the GUI
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IP Checker");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label ipLabel = new Label("Enter IP address:");
        grid.add(ipLabel, 0, 0);
        TextField ipTextField = new TextField();
        grid.add(ipTextField, 1, 0);

        Button connectButton = new Button("Connect");
        grid.add(connectButton, 1, 1);

        //add a button for default address
        Button defaultButton = new Button("Default Connection");
        grid.add(defaultButton, 0, 1);
        defaultButton.setOnAction(e -> {
            String ip = "127.0.0.1:10021";
            try {
                conn = ServerConnection.connect(ip);
                primaryStage.hide();
                showLoginPage();
            } catch (Exception exception) {
                //add an error message to the screen
                Label errorLabel = new Label("Connection failed");
                grid.add(errorLabel, 1, 2);
            }
        });
        connectButton.setOnAction(e -> {
            String ip = ipTextField.getText();
            try {
                conn = ServerConnection.connect(ip);
                primaryStage.hide();
                showLoginPage();
            } catch (Exception exception) {
                //add an error message to the screen
                Label errorLabel = new Label("Connection failed");
                grid.add(errorLabel, 1, 2);
            }
        });

        keyBoardInput(grid, connectButton);
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows the login page
     */
    private void showLoginPage() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Username:");
        loginGrid.add(usernameLabel, 0, 0);
        TextField usernameTextField = new TextField();
        loginGrid.add(usernameTextField, 1, 0);

        Label passwordLabel = new Label("Password:");
        loginGrid.add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        loginGrid.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        loginGrid.add(loginButton, 1, 2);

        loginButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            try {
                if(conn.isValidCredentials(username, password).valid()){
                    loginStage.hide();
                    showChatPage(username, password);
                } else {
                    Label errorLabel = new Label("Invalid credentials");
                    loginGrid.getChildren().remove(6);
                    loginGrid.add(errorLabel, 1, 4);
                }
            } catch (Exception exception) {
                Label errorLabel = new Label("Username or password is empty.");
                if(loginGrid.getChildren().size() > 6) {
                    loginGrid.getChildren().remove(6);
                }
                loginGrid.add(errorLabel, 1, 4);
            }
        });

        Scene loginScene = new Scene(loginGrid, 400, 300);
        loginStage.setScene(loginScene);
        loginStage.show();

        Button createNewUserButton = new Button("Create new user");
        loginGrid.add(createNewUserButton, 1, 3);

        createNewUserButton.setOnAction(e -> {
            loginStage.hide();
            showUserCreationPage();
        });

        keyBoardInput(loginGrid, loginButton);
    }

    /**
     * Sets default keyboard input for a button and pane
     * @param pane pane which action is coming from
     * @param button Button which action is coming from
     */
    private void keyBoardInput(GridPane pane, Button button) {
        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                button.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                try {
                    conn.close();
                } catch (Exception e) {
                    System.err.println("Failed to close connection");
                }
                Platform.exit();
            }
        });
    }

    /**
     * Shows the user creation page
     */
    private void showUserCreationPage(){
        Stage userCreationStage = new Stage();
        userCreationStage.setTitle("Create new user");

        GridPane userCreationGrid = new GridPane();
        userCreationGrid.setAlignment(Pos.CENTER);
        userCreationGrid.setHgap(10);
        userCreationGrid.setVgap(10);
        userCreationGrid.setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Username:");
        userCreationGrid.add(usernameLabel, 0, 0);
        TextField usernameTextField = new TextField();
        userCreationGrid.add(usernameTextField, 1, 0);

        Label passwordLabel = new Label("Password:");
        userCreationGrid.add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        userCreationGrid.add(passwordField, 1, 1);

        Label colorLabel = new Label("Color:");
        userCreationGrid.add(colorLabel, 0, 3);
        TextField colorTextField = new TextField();
        userCreationGrid.add(colorTextField, 1, 3);

        Button createUserButton = new Button("Create user");
        userCreationGrid.add(createUserButton, 1, 4);

        Button backButton = new Button("Back");
        userCreationGrid.add(backButton, 1, 5);
        backButton.setOnAction(e -> {
            userCreationStage.hide();
            showLoginPage();
        });

        createUserButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            String color = colorTextField.getText();

            try {
                if(username.isEmpty() || password.isEmpty() || color.isEmpty()) {
                    Label errorLabel = new Label("Please fill all fields");
                    userCreationGrid.add(errorLabel, 1, 5);
                } else if (!color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                    Label errorLabel = new Label("Color must be hex code");
                    userCreationGrid.add(errorLabel, 1, 5);
                } else {
                    conn.createNewUser(username, password, color);
                    userCreationStage.hide();
                    showLoginPage();
                }
            } catch (ServerConnection.LoginException exception) {
                Label errorLabel = new Label(exception.getMessage());
                userCreationGrid.add(errorLabel, 1, 5);
            }
        });

        Scene userCreationScene = new Scene(userCreationGrid, 400, 300);
        userCreationStage.setScene(userCreationScene);
        userCreationStage.show();

        userCreationGrid.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                createUserButton.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                backButton.fire();
            }
        });
    }

    /**
     * Shows the chat page
     * @param username the username of the user
     * @param password the password of the user
     */
    private void showChatPage(String username, String password) {
        try {
            conn.start(username, password);
        } catch (ServerConnection.LoginException e) {
            System.err.println("Failed to start connection");
            Platform.exit();
        }
        user = username;
        selectedChannel = null;

        Stage chatStage = new Stage();
        chatStage.setTitle("Chat - Logged in as " + username);
        HBox root = new HBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        VBox channelSide = new VBox();
        channelSide.setSpacing(10);
        channelSide.setPadding(new Insets(10));

        ListView<Channel> channelList = new ListView<>();
        channelList.setMinWidth(200);
        channelList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.name() == null) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });

        channelSide.getChildren().add(channelList);

        Button createChannelButton = new Button("Create new channel");
        channelSide.getChildren().add(createChannelButton);
        createChannelButton.setOnAction(e -> createNewChannelPopup());

        root.getChildren().add(channelSide);

        VBox messageSide = new VBox();
        messageSide.setSpacing(10);
        messageSide.setPadding(new Insets(10));

        TextFlow messages = new TextFlow();
        messages.setMinWidth(600);
        messages.setMinHeight(500);
        messageSide.getChildren().add(messages);

        HBox messageInput = new HBox();
        messageInput.setSpacing(10);
        messageInput.setPadding(new Insets(10));
        TextField messageTextField = new TextField();
        messageTextField.setMinWidth(600);
        Button sendButton = new Button("Send");
        Button fileButton = new Button("File");
        messageInput.getChildren().addAll(messageTextField, sendButton, fileButton);
        messageSide.getChildren().add(messageInput);
        root.getChildren().add(messageSide);

        channelList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Channel channel = channelList.getSelectionModel().getSelectedItem();
                if (channel != null) {
                    chatStage.setTitle("Chat - " + channel.name() + " - Logged in as " + user);
                    selectedChannel = channel;
                    updateMessagesList(messages, channel);
                }
            }
        });

        sendButton.setOnAction(e -> {
            String message = messageTextField.getText();
            if (!message.isEmpty()) {
                conn.sendMessage(selectedChannel.name(), message);
                messageTextField.clear();
            }
        });

        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(chatStage);
            if (file != null) {
                try {
                    conn.sendFile(selectedChannel.name(), file, Message.Type.FILE);
                } catch (IOException ex) {
                    System.out.println("Failed to send file");
                } catch (IllegalArgumentException ex) {
                    System.out.println("Kindel viga mida peaks käsitlema: " + ex.getMessage());
                }
            }
        });

        messageTextField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                sendButton.fire();
            }
        });

        conn.registerMessageListener(message -> {
            if (message.channel().equals(selectedChannel.name())) {
                Instant instant = Instant.ofEpochSecond(message.timestamp() / 1000);
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Platform.runLater(() -> handleMessage(messages,message,formattedDateTime));
        }});
        conn.registerChannelListener(channel -> Platform.runLater(() -> updateChatsList(channelList)));

        ListView<String> userList = new ListView<>();
        userList.setMinWidth(200);
        userList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
        root.getChildren().add(userList);

        Scene chatScene = new Scene(root, 1400, 800);
        chatStage.setScene(chatScene);
        chatStage.show();

        updateChatsList(channelList);
        if(channelList.getItems().size() > 0) {
            selectedChannel = channelList.getItems().get(0);
        }
        updateMessagesList(messages, selectedChannel);
        updateUserList(userList, selectedChannel);

        conn.registerUserListener(user -> Platform.runLater(() -> updateUserList(userList, selectedChannel)));
    }

    private void updateUserList(ListView<String> userList, Channel channel) {
        userList.getItems().clear();
        List<String> activeUsers = conn.getActiveUsers();
        for(String user : channel.members()) {
            String status;
            if(activeUsers.contains(user)) {
                status = " (online)";
            } else {
                status = " (offline)";
            }
            userList.getItems().add(user + status);
        }
    }

    /**
     * Updates the list of messages
     * @param messages the text area to update
     * @param channel the channel to get the messages from
     */
    private void updateMessagesList(TextFlow messages, Channel channel) {
        messages.getChildren().clear();
        conn.getLastMessages(10, channel.name()).forEach(message -> {
            Instant instant = Instant.ofEpochSecond(message.timestamp() / 1000);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            handleMessage(messages, message, formattedDateTime);
        });
    }

    private void handleMessage(TextFlow messages, Message message, String formattedDateTime) {
        Text intro = new Text(formattedDateTime + " " + message.sender() + " ");
        intro.setFill(Color.web(message.senderColor()));
        messages.getChildren().add(intro);
        if(message.type() == Message.Type.MESSAGE) {
            Text t = new Text(message.content() + "\n");
            t.setFill(Color.web(message.senderColor()));
            messages.getChildren().add(t);
        } else if(message.type() == Message.Type.FILE) {
            Hyperlink link = new Hyperlink(message.content() + "\n");
            link.setTextFill(Color.web(message.senderColor()));
            messages.getChildren().add(link);
            link.setOnAction(e -> {
                File f = conn.getFile(message);
                //create a popup that shows the location of the file
                Stage popupStage = new Stage();
                VBox popupRoot = new VBox();
                Label titleLabel = new Label("File location");
                Label locationLabel = new Label(f.getAbsolutePath());
                Button closeButton = new Button("Close");
                popupRoot.setSpacing(10);
                popupRoot.setPadding(new Insets(10));
                popupRoot.getChildren().addAll(titleLabel, locationLabel, closeButton);
                closeButton.setOnAction(event -> popupStage.close());
                Scene popupScene = new Scene(popupRoot, 400, 100);
                popupStage.setScene(popupScene);
                popupStage.show();
            });
        }
    }

    /**
     * Creates the popup for creating a new channel
     */
    private void createNewChannelPopup() {
        // Show a popup to create a new channel
        Stage popupStage = new Stage();
        VBox popupRoot = new VBox();
        Label titleLabel = new Label("Create new channel");
        TextField nameTextField = new TextField();
        CheckComboBox<String> membersComboBox = new CheckComboBox<>();
        Button createButton = new Button("Create");

        popupRoot.setSpacing(10);
        popupRoot.setPadding(new Insets(10));
        membersComboBox.setMinWidth(200);

        List<String> allUsers = conn.getAllUsers();
        allUsers.remove(user);
        membersComboBox.getItems().addAll(allUsers);


        createButton.setOnAction(event -> {
            List<String> members = new ArrayList<>(membersComboBox.getCheckModel().getCheckedItems());
            members.add(user);
            try {
                conn.createNewChannel(nameTextField.getText(), members, Channel.Type.CHANNEL);
            } catch (Exception e) {
                Label errorLabel = new Label(e.getMessage());
                popupRoot.getChildren().add(errorLabel);
            }
            popupStage.close();
        });


        popupRoot.getChildren().addAll(titleLabel, new Label("Name:"), nameTextField,
                new Label("Members:"), membersComboBox, createButton);
        Scene popupScene = new Scene(popupRoot, 400, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    /**
     * Updates the list of chats
     * @param chats the list view to update
     */
    private void updateChatsList(ListView<Channel> chats) {
        List<Channel> channels = conn.getChats();
        chats.getItems().clear();
        chats.getItems().addAll(channels);
        chats.getSelectionModel().selectFirst();
    }

    /**
     * Launches the application
     * @param args not used
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Stops the application
     */
    @Override
    public void stop() throws Exception {
        conn.close();
        super.stop();
    }
}