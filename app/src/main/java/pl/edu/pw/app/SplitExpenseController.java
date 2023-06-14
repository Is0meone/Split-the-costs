package pl.edu.pw.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplitExpenseController implements Initializable {
    @FXML
    private ListView<String> friendlist;
    @FXML
    private TextField expenseName;
    @FXML
    private TextField Amount;
    @FXML
    private Button split;
    @FXML
    private Text selected;
    private String[] friends = {"Bartek", "Tomek", "Pawel", "Kuba", "Wojciech"};
    private ObservableList<String> observableList = FXCollections.observableArrayList(friends);

    @FXML
    private AnchorPane splitPane;
    @FXML
    private void returnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageView = loader.load();
        MainPageController mainPageController = loader.getController();

        mainPageController.setUserPane(splitPane);

        splitPane.getChildren().setAll(mainPageView);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        friendlist.setItems(observableList);
        friendlist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void handleSplit(ActionEvent event){
        StringBuilder sb = new StringBuilder();
        for (Object o : friendlist.getSelectionModel().getSelectedItems()){
            sb.append(o.toString());
        }
        selected.setText(sb.toString());
    }
}
