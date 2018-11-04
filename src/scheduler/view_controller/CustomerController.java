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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Customer;
import model.DBConnect;

/**
 * FXML Controller class
 *
 * @author CMiller
 */
public class CustomerController implements Initializable {

    @FXML
    private TableView<Customer> tvCustomer;
    @FXML
    private TableColumn<Customer, Integer> customerID;
    @FXML
    private TableColumn<Customer, String> cusNameCol;
    @FXML
    private TableColumn<Customer, String> cusAddCol;
    @FXML
    private TableColumn<Customer, String> cusCityCol;
    @FXML
    private TableColumn<Customer, String> cusCountryCol;
    @FXML
    private TableColumn<Customer, String> cusPostCol;
    @FXML
    private TableColumn<Customer, String> cusPhoneCol;
    @FXML
    private Button btnBack;
    
    private ObservableList<Customer> customers = FXCollections.observableArrayList();



    @FXML
    private void handleAddCustomer(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("AddEditCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleUpdateCustomer(ActionEvent event) throws IOException {

        Customer selCustomer = tvCustomer.getSelectionModel().getSelectedItem();

        if (selCustomer != null) {
            System.out.println(selCustomer);
            Integer SelCustomerID = selCustomer.customerIDProperty().getValue();
            System.out.println(SelCustomerID);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AddEditCustomer.fxml"));
            Parent parent = loader.load();

            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            AddEditCustomerController controller = loader.getController();
            try {
                controller.loadCustomer(SelCustomerID);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please select a customer to update");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteCustomer(ActionEvent event) throws SQLException {

        Customer selCustomer = tvCustomer.getSelectionModel().getSelectedItem();
        Integer customerId = selCustomer.getCustomerID();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete this customer?");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    try {
                        DBConnect.deleteCustomer(customerId);
                        displayCustomers();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
    }

    private void displayCustomers() throws SQLException {
        try {
            customers = DBConnect.getCustomers();
        } catch (SQLException ex) {

        }
        this.tvCustomer.setItems(customers);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        customerID.setCellValueFactory(cellData -> cellData.getValue().customerIDProperty().asObject());
        cusNameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        cusAddCol.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        cusCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        cusCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        cusPostCol.setCellValueFactory(cellData -> cellData.getValue().postCodeProperty());
        cusPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        try {
            displayCustomers();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
                
        btnBack.setOnAction(new ReportsController()::btnPush);
    }
}
