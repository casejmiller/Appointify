/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view_controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;
import model.DBConnect;

/**
 * FXML Controller class
 *
 * @author CMiller
 */
public class AddEditCustomerController implements Initializable {

    @FXML
    private Label lblCusTitle;
    @FXML
    private TextField txtCusName;
    @FXML
    private TextField txtCusAddress;
    @FXML
    private TextField txtCusCity;
    @FXML
    private TextField txtCusCountry;
    @FXML
    private TextField txtCusPostal;
    @FXML
    private TextField txtCusPhone;

    Customer selCustomer;

    Integer addressID = 0;

    @FXML
    private void handleSave(ActionEvent event) throws SQLException {
        if (isValid()) {
            if (addressID == 0) {
                String country = this.txtCusCountry.getText();
                String city = this.txtCusCity.getText();
                String cusName = this.txtCusName.getText();
                String cusAddress = this.txtCusAddress.getText();
                String cusPostal = this.txtCusPostal.getText();
                String cusPhone = this.txtCusPhone.getText();
                Integer countryID = DBConnect.selectCountry(country);
                Integer cityID = DBConnect.selectCity(city, countryID);
                Integer addressID = DBConnect.addAddress(cusAddress, cityID, cusPostal, cusPhone);
                DBConnect.addCustomer(cusName, addressID);
                System.out.println(addressID);
            } else {
                String country = this.txtCusCountry.getText();
                String city = this.txtCusCity.getText();
                String cusName = this.txtCusName.getText();
                String cusAddress = this.txtCusAddress.getText();
                String cusPostal = this.txtCusPostal.getText();
                String cusPhone = this.txtCusPhone.getText();
                Integer countryID = DBConnect.selectCountry(country);
                Integer cityID = DBConnect.selectCity(city, countryID);
                DBConnect.updateAddress(addressID, cusAddress, cityID, cusPostal, cusPhone);
                DBConnect.updateCustomer(selCustomer.getCustomerID(), addressID, cusName);
            }
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to Cancel?");
        alert.showAndWait().ifPresent((response -> {
            if (response == ButtonType.OK) {
                try {
                    Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }));

    }

    private Boolean isValid() {
        String error;
        if (this.txtCusName.getText().isEmpty()) {
            error = "Please enter a customer name.";
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if (this.txtCusAddress.getText().isEmpty()) {
            error = "Please enter an address.";
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if (this.txtCusCity.getText().isEmpty()) {
            error = "Please enter a city.";
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if (this.txtCusCountry.getText().isEmpty()) {
            error = "Please enter a country.";
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if (this.txtCusPostal.getText().isEmpty()) {
            error = "Please enter a postal code.";
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if (this.txtCusPhone.getText().isEmpty()) {
            error = "Please enter a phone number.";
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        return true;
    }
    
    
    
    public void loadCustomer(Integer customerID) throws SQLException {
        this.lblCusTitle.setText("Update Customer");

        this.addressID = DBConnect.selectAddressID(customerID);
        DBConnect.selectCustomer(customerID);

        this.selCustomer = DBConnect.getSelectedCustomer();

        this.txtCusName.setText(selCustomer.nameProperty().getValue());
        this.txtCusAddress.setText(selCustomer.addressProperty().getValue());
        this.txtCusCity.setText(selCustomer.cityProperty().getValue());
        this.txtCusCountry.setText(selCustomer.countryProperty().getValue());
        this.txtCusPostal.setText(selCustomer.postCodeProperty().getValue());
        this.txtCusPhone.setText(selCustomer.phoneProperty().getValue());

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
