package pl.edu.pw.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserSearchController {
    @FXML
    private ListView<String> friendsListView;
    private ObservableList<String> friendsList;

    private String userId;
    private Stage primaryStage;

    @FXML
    private AnchorPane userPane;

    @FXML
    private Text textUserId;

    @FXML
    private Text userGreet;

    @FXML
    private TextField searchField;

    @FXML
    private Button addFriendButton;

    private String token;

    @FXML
    private void addFriendAction(ActionEvent actionEvent) throws IOException {
        String userName = searchField.getText();

        if (userName.length() > 0) {
            int id = Integer.parseInt(parseUserId(userName));
            if (id != 0) {
                sendAFriendRequest(id);
                friendsList.add(userName);
                showAlert("Request sent", "A request has been sent to " + userName);
            } else {
                showErrorAlert("User not found", "User with the name " + userName + " does not exist.");
            }
        } else {
            showErrorAlert("User not found", "User with the name " + userName + " does not exist.");
        }
    }


    private String getSearchUser(String userName) throws IOException {
        String url = "http://localhost:8090/users/user/" + 8 + "/find/" + userName;

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + "MasterToken");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Request failed with response code: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return parseUserId(response.toString());
        }
    }

    private String parseUserId(String response) {
        response = response.replace("[", "").replace("]", "").replace("}", "");
        String[] fields = response.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":");
            if (keyValue.length == 2 && keyValue[0].trim().equals("\"id\"")) {
                return keyValue[1].trim();
            }
        }
        return null;
    }

    private void sendAFriendRequest(int userId) throws IOException {
        String requestURL = "http://localhost:8090/friends/user/" + 8 + "/requestoracceptfriendship/" + userId;
        HttpURLConnection con = (HttpURLConnection) new URL(requestURL).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + "MasterToken");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Request failed with response code: " + responseCode);
        }
    }

    @FXML
    private void returnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageView = loader.load();
        MainPageController mainPageController = loader.getController();

        mainPageController.setUserPane(userPane);

        userPane.getChildren().setAll(mainPageView);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initialize() {
        friendsList = FXCollections.observableArrayList();
        friendsListView.setItems(friendsList);
        friendsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
}
