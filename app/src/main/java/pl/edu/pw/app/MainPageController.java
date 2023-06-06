package pl.edu.pw.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {
    @FXML
    private AnchorPane mainPane;
    private Stage primaryStage;
    private AnchorPane userPane;
    @FXML
    private Text userId;
    @FXML
    private Text userGreet;
    private String token;
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

    public void setUserId(String usrId){
        StringBuilder id = new StringBuilder();
        id.append(userId.getText());
        id.append(" " + usrId);
        userId.setText(id.toString());
    }
    public void setUserGreet(String username){
        StringBuilder usrname = new StringBuilder();
        usrname.append(userGreet.getText());
        usrname.append(" " + username + "!");
        userGreet.setText(usrname.toString());
    }
    public void setToken(String token){
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
