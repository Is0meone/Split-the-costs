package pl.edu.pw.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserSearchController {

    @FXML
    private ListView<String> invitationsListView;

    @FXML
    private ListView<String> friendsListView;


    private String userId;
    private List<String> friends;

    private List<String> invitations;

    private String name;
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

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;


    private String token;
    private String friendId;

    @FXML
    public void initialize() {
        acceptButton.setOnAction(this::acceptButtonClicked);
    }


    @FXML
    private void rejectButtonAction(ActionEvent actionEvent) {
        String selectedInvitation = invitationsListView.getSelectionModel().getSelectedItem();

        if (selectedInvitation != null) {
            String friendIdString = extractFriendId(selectedInvitation);
            if (friendIdString != null) {
                int friendId = Integer.parseInt(friendIdString);
                rejectAction(friendId);
                showAlert("Request Rejected", "You have rejected the friend request.");
                // Refresh the invitations list
                try {
                    initializeInvitationsList(userId);
                } catch (IOException e) {
                    e.printStackTrace();
                    showErrorAlert("Error", "An error occurred while loading the invitations list.");
                }
            } else {
                showErrorAlert("Invalid Invitation", "The selected invitation is invalid.");
            }
        } else {
            showErrorAlert("No Invitation Selected", "Please select an invitation from the list.");
        }
    }
    private void rejectAction(int friendId) {
        String requestURL = "http://localhost:8090/friends/user/" + userId + "/rejectfriendship/" + friendId;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(requestURL).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while rejecting the friend request.");
        }
    }



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
                    if (userIdString != null) {
                        int id = Integer.parseInt(userIdString);
                        sendAFriendRequest(id);
                        showAlert("Request sent", "A request has been sent to " + userName);
                        displayFriends(friends);
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


    private void sendAFriendRequest(int friendId) {
        String requestURL = "http://localhost:8090/friends/user/" + userId + "/requestoracceptfriendship/" + friendId;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(requestURL).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while sending the friend request.");
        }
    }

    private void acceptFriendInvitation(int friendId) {
        String requestURL = "http://localhost:8090/friends/user/" + userId + "/requestoracceptfriendship/" + friendId;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(requestURL).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while sending the friend request.");
        }
    }

    private void acceptButtonClicked(ActionEvent event) {
        // Get the selected item from the invitationsListView
        String selectedInvitation = invitationsListView.getSelectionModel().getSelectedItem();

        if (selectedInvitation != null) {
            // Extract the friend ID from the selected invitation
            String friendIdString = extractFriendId(selectedInvitation);
            if (friendIdString != null) {
                int friendId = Integer.parseInt(friendIdString);
                acceptFriendInvitation(friendId);
                showAlert("Request Accepted", "You have accepted the friend request.");
                try {
                    initializeInvitationsList(userId);
                    initializeFriendsList(userId);
                } catch (IOException e) {
                    e.printStackTrace();
                    showErrorAlert("Error", "An error occurred while loading the invitations list.");
                }
            } else {
                showErrorAlert("Invalid Invitation", "The selected invitation is invalid.");
            }
        } else {
            showErrorAlert("No Invitation Selected", "Please select an invitation from the list.");
        }
    }

    private String extractFriendId(String invitation) {
        // Extract the friend ID from the invitation text
        int startIndex = invitation.indexOf("(ID: ");
        int endIndex = invitation.lastIndexOf(")");
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return invitation.substring(startIndex + 5, endIndex);
        }
        return null;
    }


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


    protected void initializeFriendsList(String userId) throws IOException {
        String url = "http://localhost:8090/friends/user/" + userId + "/friends";
        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestMethod("GET");


        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            // Parse the JSON response to get the friend data
            friends = parseFriendsFromJson(responseBody);
            displayFriends(friends);
        }
    }

    protected void initializeInvitationsList(String userId) throws IOException {
        String url = "http://localhost:8090/friends/user/" + userId + "/requests";
        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestMethod("GET");


        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            // Parse the JSON response to get the friend data
            invitations = parseInvitationsFromJson(responseBody);
            displayInvitations(invitations);
        }
    }

    private void displayFriends(List<String> friends) {

        if (friends != null) {
            friendsListView.getItems().clear();
            friendsListView.getItems().addAll(friends);
        }

    }

    private void displayInvitations(List<String> invitations) {
        if (invitationsListView != null) {
            invitationsListView.getItems().clear();
            invitationsListView.getItems().addAll(invitations);
        }
    }

    private List<String> parseFriendsFromJson(String json) {
        List<String> friends = new ArrayList<>();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement element : jsonArray) {
            JsonObject friendObject = element.getAsJsonObject();
            String friendName = friendObject.get("username").getAsString();
            String friendId = friendObject.get("id").getAsString();
            friends.add(friendName + " (ID: " + friendId + ")");
        }
        return friends;
    }

    private List<String> parseInvitationsFromJson(String json) {
        List<String> friends = new ArrayList<>();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement element : jsonArray) {
            JsonObject friendObject = element.getAsJsonObject();
            String friendName = friendObject.get("username").getAsString();
            String friendId = friendObject.get("id").getAsString();
            friends.add(friendName + " (ID: " + friendId + ")      has invited you to be friends.");
        }
        return friends;
    }

    public void setControllerUserId(String text) {
        textUserId.setText(text);
    }

    public void setName(String name) {
        this.name = name;
    }

}
