package pl.edu.pw.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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

public class RegisterController {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Text failedRegister;
    @FXML
    private AnchorPane userPane;
    @FXML
    private Label listLabel;
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
        mainPageController.setTextUserId(usrId);

        userPane.getChildren().setAll(mainPageView);
    }

    public void setUserPane(AnchorPane userPane) {
        this.userPane = userPane;
    }

    public boolean sendRegisterRequest() throws IOException {
        if (!validatePasswords()) {
            return false;
        }

        String username = getUsername();
        String password = getPassword();
        String url = "http://localhost:8090/auth/register";
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        HttpURLConnection con = createConnection(url, "POST");
        if (con == null) {
            return false;
        }

        try (OutputStream outputStream = con.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
            outputStream.flush();
        }

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            handleFailedRegister("This user already exists! Please try again.");
            return false;
        }

        processResponse(con);
        return true;
    }

    private boolean validatePasswords() {
        String passwordText = password.getText();
        String confirmPasswordText = confirmPassword.getText();

        if (passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            handleFailedRegister("Password fields cannot be empty! Please try again.");
            return false;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            handleFailedRegister("Passwords don't match! Please try again.");
            password.setText("");
            confirmPassword.setText("");
            return false;
        }

        return true;
    }


    private String getUsername() {
        return username.getText();
    }

    private String getPassword() {
        return password.getText();
    }

    private HttpURLConnection createConnection(String url, String requestMethod) {
        try {
            URL address = new URL(url);
            HttpURLConnection con = (HttpURLConnection) address.openConnection();
            con.setRequestMethod(requestMethod);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            return con;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleFailedRegister(String errorMessage) {
        failedRegister.setText(errorMessage);
        password.setText("");
    }

    private void processResponse(HttpURLConnection con) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            token = jsonObject.get("token").getAsString();
            usrId = jsonObject.get("userId").getAsString();
            name = getUsername();
        }
    }
}
