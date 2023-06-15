package pl.edu.pw.app;

import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class ManageDebtsController implements Initializable {
    @FXML
    private Text totalDebt;
    @FXML
    private Text totalOwedToYou;
    @FXML
    private ListView<String> yourDebts;
    @FXML
    private ListView<String> othersDebtsToYou;
    @FXML
    private Text message;
    @FXML
    private AnchorPane expensePane;
    private String token;
    private String userId;
    private String name;
    private AnchorPane userPane;
    @FXML
    private void returnAction(ActionEvent event) throws IOException {
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
        yourDebts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    @FXML
    private void handleResolveDebt(ActionEvent event) throws IOException {
        String url = parseSelectionToUrl();
        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);


        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            message.setFill(Color.GREEN);
            message.setText("Obligation resolved!");
            getYourDebts(userId);
            getOthersDebtsToYou(userId);
            con.disconnect();
        } else {
            message.setFill(Color.RED);
            message.setText("Something went wrong :( Please try again");
            con.disconnect();
        }
    }

    private String parseSelectionToUrl(){
        String[] temp = yourDebts.getSelectionModel().getSelectedItem().split(" ");
        String creditorId = temp[4].substring(0,temp[4].length()-1);
        String obligId = temp[7].substring(0,temp[7].length()-1);
        String url = "http://localhost:8090/obligations/user/" + userId + "/accept/" + creditorId + "/" + obligId;
        return url;
    }

    public void getYourDebts(String userId) throws IOException {
        String url = "http://localhost:8090/obligations/user/" + userId + "/debts";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);


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
            parseYourDebts(responseBody);
        }
    }
    public void getOthersDebtsToYou(String userId) throws IOException {
        String url = "http://localhost:8090/obligations/user/" + userId + "/credits";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);


        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            System.out.println("Response code: " + responseCode);
            return;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            parseOthersDebtsToYou(responseBody);
        }
    }

    private void parseYourDebts(String response) {
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
        List<String> tempList = new ArrayList<>();
        if (jsonArray.size() > 0) {
            Gson gson = new Gson();
            double total = 0;
            for (JsonElement element : jsonArray) {
                StringBuilder sb = new StringBuilder();
                JsonObject jsonObject = element.getAsJsonObject();
                String status = jsonObject.get("status").getAsString();
                if (!Objects.equals(status, "PENDING")) continue;
                double amount = jsonObject.get("amount").getAsDouble();
                String obligId = jsonObject.get("id").getAsString();
                String creditorId = jsonObject.get("creditorId").getAsString();
                sb.append("Your debt to user " + creditorId + ": " + amount + " (ObligId: "+ obligId + ")");
                tempList.add(sb.toString());
                total += amount;
            }
            setTotalDebt(total);
            yourDebts.setItems(FXCollections.observableArrayList(tempList));
        }
    }

    private void parseOthersDebtsToYou(String response) {
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("obligations");
        List<String> tempList = new ArrayList<>();
        if (jsonArray.size() > 0) {
            Gson gson = new Gson();
            double total = 0;
            for (JsonElement element : jsonArray) {
                StringBuilder sb = new StringBuilder();
                JsonObject jsonObject = element.getAsJsonObject();
                String status = jsonObject.get("status").getAsString();
                if (!Objects.equals(status, "PENDING")) continue;
                double amount = jsonObject.get("amount").getAsDouble();
                String obligId = jsonObject.get("id").getAsString();
                String debtorId = jsonObject.get("debtorId").getAsString();
                sb.append("User " + debtorId + " owes you: " + amount + " (ObligId: "+ obligId + ")");
                tempList.add(sb.toString());
                total += amount;
            }
            setTotalOwedToYou(total);
            othersDebtsToYou.setItems(FXCollections.observableArrayList(tempList));
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }

    public void setTotalDebt(double totalDebt) {
        StringBuilder sb = new StringBuilder();
        sb.append(totalDebt);
        this.totalDebt.setText(sb.toString());
    }

    public void setTotalOwedToYou(double totalOwedToYou) {
        StringBuilder sb = new StringBuilder();
        sb.append(totalOwedToYou);
        this.totalOwedToYou.setText(sb.toString());
    }
}