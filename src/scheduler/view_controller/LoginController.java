/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view_controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import model.DBConnect;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author CMiller
 */
public class LoginController implements Initializable {

    Locale userLocale;
    ResourceBundle rb;

    public static String user;

    @FXML
    private PasswordField pwdPassword;
    @FXML
    private TextField txtUsername;
    @FXML
    private Label lblSignIn;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPassword;
    @FXML
    private Button btnSignIn;
    @FXML
    private Button btnExit;

    private static Timestamp convertToUTC(LocalDateTime date) {
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zStart = date.atZone(zid);
        ZonedDateTime utcStart = zStart.withZoneSameInstant(ZoneId.of("UTC"));
        date = utcStart.toLocalDateTime();
        Timestamp timestamp = Timestamp.valueOf(date);
        return timestamp;
    }

    @FXML
    private void handleSignIn(ActionEvent event) throws IOException, SQLException {
        String username = txtUsername.getText();
        String password = pwdPassword.getText();
        pwdPassword.setText("");

        if (DBConnect.validateUser(username, password)) {
            user = this.txtUsername.getText();
            logger(user);
            DBConnect.upcomingApp(
                    convertToUTC(LocalDateTime.now()),
                    convertToUTC(LocalDateTime.now().plusMinutes(15)),
                    user);
            Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(this.rb.getString("errortitle"));
            alert.setHeaderText(this.rb.getString("errortitle"));
            alert.setContentText(this.rb.getString("errorlogin"));
            alert.showAndWait();
        }
    }


    private void logger(String user) {
        try {
            String filename = "log.txt";
            String path = System.getProperty("user.dir");

            File file = new File(path, filename);

            System.out.println("Final filepath : " + file.getAbsolutePath());
            if (file.createNewFile()) {
                System.out.println("File is created!");
            }
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true))) {
                
                String content = user + " @ " + LocalDateTime.now().toString() + System.lineSeparator();
                bw.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(this.rb.getString("confirmExit"));
        alert.setHeaderText(this.rb.getString("confirmExit"));
        alert.setContentText(this.rb.getString("confirmDialogue"));
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> System.exit(0));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Locale.setDefault(new Locale("de","DE"));
        //Locale.setDefault(new Locale("fr","FR"));
        this.rb = ResourceBundle.getBundle("resources/Login", Locale.getDefault());

        lblUsername.setText(this.rb.getString("username"));
        lblPassword.setText(this.rb.getString("password"));
        lblSignIn.setText(this.rb.getString("title"));
        btnExit.setText(this.rb.getString("btnExit"));
        btnSignIn.setText(this.rb.getString("btnSignIn"));

    }

}
