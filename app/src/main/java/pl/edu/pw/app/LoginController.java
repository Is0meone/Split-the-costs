package pl.edu.pw.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginController {
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    Text failedLogin;
    @FXML
    private AnchorPane userPane;

    private String token;
    private String usrId;

    private String name;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }

    public void goToRegisterView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));
        AnchorPane registerView = loader.load();
        RegisterController registerController = loader.getController();

        registerController.setUserPane(userPane);

        userPane.getChildren().setAll(registerView);
    }

    public void goToMainPage(ActionEvent event) throws IOException {
        if (!sendLoginRequest()) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageView = loader.load();
        MainPageController mainPageController = loader.getController();

        mainPageController.setUserPane(userPane);
        mainPageController.setToken(token);
        mainPageController.setUserGreet(name);
        mainPageController.setUserId(usrId);
        userPane.getChildren().setAll(mainPageView);
    }

    public boolean sendLoginRequest() throws IOException {
        String usrname = username.getText();
        String psswrd = password.getText();
        String url = "http://localhost:8090/auth/login";
        String requestBody = "{\"username\": \"" + usrname + "\", \"password\": \"" + psswrd + "\"}";

        URL address = new URL(url);
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (OutputStream outputStream = con.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
            outputStream.flush();
        }

        // Get response
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            failedLogin.setText("Wrong credentials! Please try again.");
            password.setText("");
            con.disconnect();
            return false;
        }
        name = usrname;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            token = jsonObject.get("token").getAsString();
            usrId = jsonObject.get("userId").getAsString();
        }
        con.disconnect();
        return true;
    }


}
