package pl.edu.pw.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
        // TODO: initialize friendsListView, userGreet, userId
        try {
            double balance = getUserBalance(userId.getText());
            updateUserBalance(balance);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private double getUserBalance(String userId) throws IOException {
        return getUserLoans(userId) - getUserDebts(userId);
    }

    private double getUserDebts(String userId) throws IOException {
        String url = "http://localhost:8090/obligations/user/" + userId + "/debts";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");

        // Get response
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            // Handle the error response appropriately
            return 0.0;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            // Parse the JSON response to get the user balance
            double debt = parseUserBalanceFromJson(responseBody);
            return debt;
        }
    }

    private double getUserLoans(String userId) throws IOException {
        String url = "http://localhost:8090/obligations/user/" + userId + "/credits";


        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");

        // Get response
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            // Handle the error response appropriately
            return 0.0;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            // Parse the JSON response to get the user balance
            double loan = parseUserBalanceFromJson(responseBody);
            return loan;
        }
    }

    private double parseUserBalanceFromJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonArray debtsArray = jsonObject.getAsJsonArray("debts");
        double balance = 0.0;
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
    }


    private void updateUserBalance(double balance) {
        userBalance.setText(String.valueOf(balance));
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

}
