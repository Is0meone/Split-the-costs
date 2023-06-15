package pl.edu.pw.app;

import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainPageController {
    @FXML
    private AnchorPane mainPane;
    private Stage primaryStage;
    private AnchorPane userPane;
    private String userId;
    private String name;

    private List<String> friends;

    @FXML
    private Text textUserId;

    @FXML
    private Text userBalance;
    @FXML
    private Text userGreet;

    @FXML
    private ListView<String> friendsListView;

    @FXML
    private Label listLabel;
    private String token;
    private double totalDebt;
    private double totalOwedToYou;




    private double getUserDebts(String userId) throws IOException {
        String url = "http://localhost:8090/obligations/user/" + userId + "/debts";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);


        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return 0.0;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            double amount = parseAmount(responseBody);
            totalDebt = amount;
            return amount;
        }
    }
    private double getUserLoans(String userId) throws IOException {
        String url = "http://localhost:8090/obligations/user/" + userId + "/credits";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);


        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            System.out.println("Response code: " + responseCode);
            return 0.0;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
//            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
//            double amount = jsonObject.get("total").getAsDouble();
            double amount = parseAmountCredit(responseBody);
            totalOwedToYou = amount;
            return amount;
        }
    }

    private double parseAmount(String response) {
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
        if (jsonArray.size() > 0) {


            // Calculate sum of "amount" values
            double totalAmount = 0.0;
            Gson gson = new Gson();
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String status = jsonObject.get("status").getAsString();
                if (!Objects.equals(status, "PENDING")) continue;
                double amount = jsonObject.get("amount").getAsDouble();
                totalAmount += amount;
            }
            return totalAmount;
        } else {
            return 0.0;
        }
    }

    private double parseAmountCredit(String response) {
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("obligations");
        if (jsonArray.size() > 0) {
            Gson gson = new Gson();
            double totalAmount = 0.0;
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String status = jsonObject.get("status").getAsString();
                if (!Objects.equals(status, "PENDING")) continue;
                double amount = jsonObject.get("amount").getAsDouble();
                totalAmount += amount;
            }
            return totalAmount;
        } else {
            return 0.0;
        }
    }



    private double parseUserBalanceFromJson(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonElement debtsElement = jsonObject.get("debts");

            if (debtsElement == null || debtsElement.isJsonNull() || debtsElement.isJsonArray() && debtsElement.getAsJsonArray().size() == 0) {
                return 0.0;
            }

            double balance = 0.0;
            JsonArray debtsArray = debtsElement.getAsJsonArray();
            for (JsonElement element : debtsArray) {
                JsonObject debtObject = element.getAsJsonObject();
                double amount = debtObject.get("amount").getAsDouble();
                String creditorId = debtObject.get("creditor").getAsJsonObject().get("id").getAsString();
                String debtorId = debtObject.get("debtor").getAsJsonObject().get("id").getAsString();

                // Calculate the balance based on the creditor and debtor IDs
                // Adjust the balance calculation logic according to your requirements
                if (creditorId.equals(userId)) {
                    balance -= amount;
                } else if (debtorId.equals(userId)) {
                    balance += amount;
                }
            }
            return balance;
        } catch (Exception e) {
            //e.printStackTrace();
            return 0.0;
        }
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


    private void displayFriends(List<String> friends) {
        friendsListView.getItems().clear();
        friendsListView.getItems().addAll(friends);
    }


    @FXML
    private void handleSearchUsersButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-search-view.fxml"));
        AnchorPane userSearchView = loader.load();
        UserSearchController userSearchController = loader.getController();
        userSearchController.setUserPane(mainPane);
        userSearchController.setToken(token);
        userSearchController.setUserId(userId);
        userSearchController.setName(name);
        mainPane.getChildren().setAll(userSearchView);
    }


    @FXML
    private void handleLogOutButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        AnchorPane loginView = loader.load();
        LoginController loginController = loader.getController();
        userPane.getChildren().setAll(loginView);
    }

    @FXML
    private void handleSplitExpensesAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("split-expense-view.fxml"));
        AnchorPane splitExpenseView = loader.load();
        SplitExpenseController splitExpenseController = loader.getController();
        initializeSplitExpensePage(splitExpenseController);
        userPane.getChildren().setAll(splitExpenseView);
    }

    public void initializeSplitExpensePage(SplitExpenseController splitExpenseController) throws IOException {
        splitExpenseController.setUserPane(userPane);
        splitExpenseController.setToken(token);
        splitExpenseController.setName(name);
        splitExpenseController.setUserId(userId);
        splitExpenseController.setFriendlist(friends);
    }

    @FXML
    private void handleManageDebtsAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("manage-debts.fxml"));
        AnchorPane manageDebtsView = loader.load();
        ManageDebtsController manageDebtsController = loader.getController();
        initializeManageDebtsPage(manageDebtsController);
        userPane.getChildren().setAll(manageDebtsView);
    }
    public void initializeManageDebtsPage(ManageDebtsController manageDebtsController) throws IOException {
        manageDebtsController.setUserPane(userPane);
        manageDebtsController.setToken(token);
        manageDebtsController.setName(name);
        manageDebtsController.setUserId(userId);
        manageDebtsController.getYourDebts(userId);
        manageDebtsController.getOthersDebtsToYou(userId);
        manageDebtsController.setTotalDebt(totalDebt);
        manageDebtsController.setTotalOwedToYou(totalOwedToYou);
    }


    protected void updateUserBalance(double balance) {
        userBalance.setText(String.valueOf(balance));
        if (balance > 0){
            userBalance.setFill(Color.GREEN);
        } else if (balance < 0){
            userBalance.setFill(Color.RED);
        } else {
            userBalance.setFill(Color.GRAY);
        }
    }

    protected double getUserBalance(String userId) throws IOException {
        return  getUserLoans(userId) - getUserDebts(userId);
    }

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }


    public void setTextUserId(String usrId) {
        StringBuilder id = new StringBuilder();
        id.append(textUserId.getText());
        id.append(" " + usrId);
        textUserId.setText(id.toString());

    }

    public void setUserId(String usrId) {
        userId = usrId;
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

    public void setName(String name) {
        this.name = name;
    }
}
