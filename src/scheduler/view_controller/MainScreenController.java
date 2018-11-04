/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view_controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DBConnect;
import scheduler.model.Appointment;

/**
 * FXML Controller class
 *
 * @author CMiller
 */
public class MainScreenController implements Initializable {

    @FXML
    private TableView<Appointment> tvAppointments;
    @FXML
    private TableColumn<Appointment, String> colDate;
    @FXML
    private TableColumn<Appointment, String> colStartTime;
    @FXML
    private TableColumn<Appointment, String> colEndTime;
    @FXML
    private TableColumn<Appointment, String> colCustomer;
    @FXML
    private TableColumn<Appointment, String> colConsultant;
    @FXML
    private TableColumn<Appointment, String> colType;
    @FXML
    private TableColumn<Appointment, String> colDetails;
    @FXML
    private TableColumn<Appointment, String> colLocation;
    @FXML
    private Button btnToggleWeekMonth;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblWelcome;

    private OffsetDateTime date = OffsetDateTime.now();
    private Boolean toggleWeek = false;
    private Appointment SelApt;
    public String user = LoginController.user;

    @FXML
    private void handleAddAppointment(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("AddEditAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleModifyApt(ActionEvent event) throws IOException {

        Appointment selApt = tvAppointments.getSelectionModel().getSelectedItem();

        if (selApt != null) {
            System.out.println(selApt);
            String customerName = selApt.customerProperty().getValue();
            System.out.println(customerName);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AddEditAppointment.fxml"));
            Parent parent = loader.load();

            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            AddEditAppointmentController controller = loader.getController();
            try {
                controller.loadAppointment(selApt);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please select an appointment to update");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteAppointment(ActionEvent event) throws SQLException {

        Appointment selApt = tvAppointments.getSelectionModel().getSelectedItem();
        Integer appointmentId = selApt.appointmentID().getValue();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Appointment");
        alert.setHeaderText("Delete Appointment");
        alert.setContentText("Are you sure you want to delete this appointment?");
        //Lambda for the alert response to cut down on length and improve readability.
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    try {
                        DBConnect.deleteAppointment(appointmentId);
                        popCalendar();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
    }

    @FXML
    void handleToggleWeekMonth(ActionEvent event) throws SQLException {
        if (!toggleWeek) {
            this.toggleWeek = true;
            this.date = this.date.with(TemporalAdjusters.firstDayOfMonth());
            this.btnToggleWeekMonth.setText("By Month");
        } else {
            this.btnToggleWeekMonth.setText("By Week");
            this.toggleWeek = false;
        }

        popCalendar();
    }



    
    @FXML
    void handleMoveUp(ActionEvent event) throws SQLException {
        if (this.toggleWeek == false) {
            this.date = this.date.plusMonths(1);
        } else {
            this.date = this.date.plusWeeks(1);
        }

        popCalendar();
    }

    @FXML
    void handleMoveBack(ActionEvent event) throws SQLException {
        if (this.toggleWeek == false) {
            this.date = this.date.minusMonths(1);
        } else {
            this.date = this.date.minusWeeks(1);
        }

        popCalendar();
    }
    
    
    
    private void popCalendar() throws SQLException {
        Integer dateValue;
        if (toggleWeek == true) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DayOfWeek first = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
            DayOfWeek last = DayOfWeek.of(((first.getValue() + 5) % DayOfWeek.values().length) + 1);
            String beg = this.date.with(TemporalAdjusters.previousOrSame(first)).format(dtf);
            String end = this.date.with(TemporalAdjusters.nextOrSame(last)).format(dtf);
            this.lblDate.setText(beg + " - " + end);

            TemporalField temp = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
            dateValue = this.date.get(temp) - 1;
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM yyyy");
            this.lblDate.setText(this.date.format(dtf));

            dateValue = this.date.getMonthValue();
        }
        ObservableList records = DBConnect.getAppointments(toggleWeek, dateValue);
        tvAppointments.setItems(records);
    }

    @FXML
    private void handleCustomer(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure want to exit?");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> System.exit(0));
    }

    @FXML
    private void handleReports(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colStartTime.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        colEndTime.setCellValueFactory(cellData -> cellData.getValue().endProperty());
        colCustomer.setCellValueFactory(cellData -> cellData.getValue().customerProperty());
        colConsultant.setCellValueFactory(cellData -> cellData.getValue().consultantProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colDetails.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());
        colLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());

        lblWelcome.setText("Welcome, " + user + "!");

        try {
            popCalendar();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        

    }

}
