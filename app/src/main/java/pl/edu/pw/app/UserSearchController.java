package pl.edu.pw.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private String friendId;


    @FXML
    private void addFriendAction(ActionEvent actionEvent) {
        String userName = searchField.getText();
        String url = "http://localhost:8090/users/user/" + userId + "/find/" + userName;

        try {
            URL address = new URL(url);
            HttpURLConnection con = (HttpURLConnection) address.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);
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
                if (userName.length() > 0) {
                    String userIdString = parseUserId(response.toString());
                    System.out.println(userIdString);
                    if (userIdString != null) {
                        int id = Integer.parseInt(userIdString);
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
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while processing the request.");
        }
    }

    private String parseUserId(String response) {
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
        if (jsonArray.size() > 0) {
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            return jsonObject.get("id").getAsString();
        }
        return null;
    }


    private void sendAFriendRequest(int userId) {
        String requestURL = "http://localhost:8090/friends/user/" + userId + "/requestoracceptfriendship/" + userId;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(requestURL).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + "MasterToken");

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while sending the friend request.");
        }
    }

    @FXML
    private void returnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
            AnchorPane mainPageView = loader.load();
            MainPageController mainPageController = loader.getController();

            mainPageController.setUserPane(userPane);

            userPane.getChildren().setAll(mainPageView);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while returning to the main page.");
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setFriendId(String friendId) {
        this.friendId = friendId;
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

    public void setControllerUserId(String text) {
        textUserId.setText(text);
    }
    public void setControllerToken(String token) {
        this.token = token;
    }
}
