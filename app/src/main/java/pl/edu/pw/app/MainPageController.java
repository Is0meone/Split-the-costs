package pl.edu.pw.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private AnchorPane mainPane;
    private Stage primaryStage;
    private AnchorPane userPane;

    @FXML
    private Text userId;

    @FXML
    private Text userBalance;
    @FXML
    private Text userGreet;

    @FXML
    private ListView<String> friendsListView;

    @FXML
    private Label listLabel;
    private String token;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO
        // initialize friendsListView, userBalance, userGreet, userId
    }

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }

    @FXML
    private void handleSearchUsersButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-search-view.fxml"));
        AnchorPane userSearchView = loader.load();
        UserSearchController userSearchController = loader.getController();

        userSearchController.setUserPane(mainPane);

        mainPane.getChildren().setAll(userSearchView);
    }

    @FXML
    private void handleManageAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("split-expense-view.fxml"));
        AnchorPane splitExpenseView = loader.load();
        SplitExpenseController splitExpenseController = loader.getController();

        // Przekazanie informacji/parametrów do kontrolera sceny "split-expense-view" (opcjonalne)
        // splitExpenseController.setSomeData(someData);

        mainPane.getChildren().setAll(splitExpenseView);
    }


    @FXML
    private void handleLogOutButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        AnchorPane loginView = loader.load();
        LoginController loginController = loader.getController();
        userPane.getChildren().setAll(loginView);
    }


    public void setUserId(String usrId) {
        StringBuilder id = new StringBuilder();
        id.append(userId.getText());
        id.append(" " + usrId);
        userId.setText(id.toString());
    }

    public void setUserGreet(String username) {
        StringBuilder usrname = new StringBuilder();
        usrname.append(userGreet.getText());
        usrname.append(" " + username + "!");
        userGreet.setText(usrname.toString());
    }

    public void setToken(String token) {
        this.token = token;
    }



    /*@FXML
    private void handleExpenseViewAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-expense-view.fxml"));
        AnchorPane splitExpenseView = loader.load();
        SplitExpenseController splitExpenseController = loader.getController();

        // Przekazanie informacji/parametrów do kontrolera sceny "split-expense-view" (opcjonalne)
        // splitExpenseController.setSomeData(someData);

        mainPane.getChildren().setAll(splitExpenseView);
    }*/


}
