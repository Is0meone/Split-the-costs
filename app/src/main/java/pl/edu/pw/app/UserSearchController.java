package pl.edu.pw.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UserSearchController {
    @FXML
    private AnchorPane userPane;

    @FXML
    private void returnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageView = loader.load();
        MainPageController mainPageController = loader.getController();

        mainPageController.setUserPane(userPane);

        userPane.getChildren().setAll(mainPageView);
    }



    public void setUserPane(AnchorPane mainPane) {
    }
}
