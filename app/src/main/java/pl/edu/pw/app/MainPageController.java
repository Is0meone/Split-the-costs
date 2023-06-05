package pl.edu.pw.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {
    @FXML
    private AnchorPane mainPane;
    private Stage primaryStage;
    private AnchorPane userPane;
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
