package oop.ryhmatoo.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.val;
import oop.ryhmatoo.client.socket.ServerConnection;

public class GUIApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("IP Checker");

        // create a grid pane for the user interface
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // add IP address label and text field to the grid pane
        Label ipLabel = new Label("Enter IP address:");
        grid.add(ipLabel, 0, 0);
        TextField ipTextField = new TextField();
        grid.add(ipTextField, 1, 0);

        // add connect button to the grid pane
        Button connectButton = new Button("Connect");
        grid.add(connectButton, 1, 1);

        // set action for connect button
        connectButton.setOnAction(e -> {
            String ip = ipTextField.getText();
            try (ServerConnection conn = ServerConnection.connect(ip)){
                primaryStage.hide();
                showLoginPage(conn);
            } catch (Exception exception) {
                //add an error message to the screen
                val errorLabel = new Label("Connection failed");
                grid.add(errorLabel, 1, 2);
            }
        });

        // create scene and set it in the stage
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLoginPage(ServerConnection conn) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        // create a grid pane for the login UI
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));

        // add username label and text field to the login UI
        Label usernameLabel = new Label("Username:");
        loginGrid.add(usernameLabel, 0, 0);
        TextField usernameTextField = new TextField();
        loginGrid.add(usernameTextField, 1, 0);

        // add password label and password field to the login UI
        Label passwordLabel = new Label("Password:");
        loginGrid.add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        loginGrid.add(passwordField, 1, 1);

        // add login button to the login UI
        Button loginButton = new Button("Login");
        loginGrid.add(loginButton, 1, 2);

        // set action for login button
        loginButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            try {
                conn.start(username, password);
                loginStage.hide();
                showChatPage(conn);
            } catch (ServerConnection.LoginException ex) {
                Label errorLabel = new Label(ex.getMessage());
                loginGrid.add(errorLabel, 1, 3);
            }
        });

        // create scene and set it in the login stage
        Scene loginScene = new Scene(loginGrid, 400, 300);
        loginStage.setScene(loginScene);
        loginStage.show();

        //add a button to create a new user
        Button createNewUserButton = new Button("Create new user");
        loginGrid.add(createNewUserButton, 1, 3);

        createNewUserButton.setOnAction(e -> {
            loginStage.hide();
            showUserCreationPage(conn);
        });
    }

    public void showUserCreationPage(ServerConnection conn){
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

        createUserButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            String color = colorTextField.getText();

            try {
                if(username.isEmpty() || password.isEmpty() || color.isEmpty()) {
                    Label errorLabel = new Label("Please fill all fields");
                    userCreationGrid.add(errorLabel, 1, 5);
                } else if (!color.equals("red") && !color.equals("green") && !color.equals("blue")) {
                    Label errorLabel = new Label("Color must be red, green or blue");
                    userCreationGrid.add(errorLabel, 1, 5);
                } else {
                    conn.createNewUser(username, password, color);
                    userCreationStage.hide();
                    showLoginPage(conn);
                }
            } catch (ServerConnection.LoginException exception) {
                Label errorLabel = new Label(exception.getMessage());
                userCreationGrid.add(errorLabel, 1, 5);
            }
        });

        Scene userCreationScene = new Scene(userCreationGrid, 400, 300);
        userCreationStage.setScene(userCreationScene);
        userCreationStage.show();
    }

    public void showChatPage(ServerConnection conn){

    }

    public static void main(String[] args) {
        launch();
    }
}
