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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SplitExpenseController implements Initializable {
    @FXML
    private ListView<String> friendlist;
    @FXML
    private TextField expenseName;
    @FXML
    private TextField amount;
    @FXML
    private Button split;
    @FXML
    private Text message;

    private AnchorPane userPane;
    private String token;


    private String name;


    private String userId;

    @FXML
    private AnchorPane splitPane;

    @FXML
    public void returnAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageView = loader.load();
        MainPageController mainPageController = loader.getController();
        initializeMainPage(mainPageController);
        userPane.getChildren().setAll(mainPageView);
    }

    public void initializeMainPage(MainPageController mainPageController) throws IOException {
        mainPageController.setUserPane(userPane);
        mainPageController.setToken(token);
        mainPageController.setUserGreet(name);
        mainPageController.setName(name);
        mainPageController.setUserId(userId);
        mainPageController.setTextUserId(userId);
        mainPageController.updateUserBalance(mainPageController.getUserBalance(userId));
        mainPageController.initializeFriendsList(userId);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        friendlist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void handleSplit(ActionEvent event) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < friendlist.getSelectionModel().getSelectedItems().size(); i++) {
            String temp = friendlist.getSelectionModel().getSelectedItems().get(i);
            String[] set = temp.split(" ");
            temp = set[2].substring(0,set[2].length()-1);
            if (i == friendlist.getSelectionModel().getSelectedItems().size() - 1){
                sb.append("\"" + temp + "\"]");
                break;
            }
            sb.append("\"" + temp + "\", ");
        }

        String requestBody = "{\"description\": \"" + expenseName.getText() + "\"," +
                " \"users\": " + sb + ", \"amount\": \"" + amount.getText() +"\"}";
        String url = "http://localhost:8090/obligations/user/" + userId + "/split";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try (OutputStream outputStream = con.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
            outputStream.flush();
        }

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            message.setText("Expense splitted successfully!");
            message.setFill(Color.GREEN);
            con.disconnect();
        } else {
            message.setText("Something went wrong :(. Please try again.");
            message.setFill(Color.RED);
            con.disconnect();
        }


    }

    public void setFriendlist(List<String> friends) {
        this.friendlist.setItems(FXCollections.observableArrayList(friends));
    }



    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }

    public void setName(String name) {
        this.name = name;
    }

}
