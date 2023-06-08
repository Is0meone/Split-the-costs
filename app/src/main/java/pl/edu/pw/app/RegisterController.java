package pl.edu.pw.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RegisterController {

    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    TextField confirmPassword;
    @FXML
    Text failedRegister;
    @FXML
    private AnchorPane userPane;
    private String token;
    private String usrId;
    private String name;

    public void goToLoginView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        AnchorPane loginView = loader.load();
        LoginController loginController = loader.getController();

        loginController.setUserPane(userPane);

        userPane.getChildren().setAll(loginView);
    }

    public void goToMainPage(ActionEvent event) throws IOException {
        if (!sendRegisterRequest()) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageView = loader.load();
        MainPageController mainPageController = loader.getController();

        mainPageController.setUserPane(userPane);
        mainPageController.setToken(token);
        mainPageController.setUserGreet(name);
        mainPageController.setUserId(usrId);

        userPane.getChildren().setAll(mainPageView);
    }
    public boolean sendRegisterRequest() throws IOException {
        if (!Objects.equals(password.getText(), confirmPassword.getText())) return false;
        String usrname = username.getText();
        String psswrd = password.getText();
        String url = "http://localhost:8090/auth/register";
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
            failedRegister.setText("This user already exists! Please try again.");
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

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }
}
