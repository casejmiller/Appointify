
package scheduler.view_controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Customer;
import model.DBConnect;
import scheduler.model.Appointment;

/**
 * FXML Controller class
 *
 * @author CMiller
 */
public class AddEditAppointmentController implements Initializable {

    @FXML
    private Label lblAppointment;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtDetails;
    @FXML
    private TextField txtLocation;
    @FXML
    private DatePicker dateDate;
    @FXML
    private ComboBox<String> cboStart;
    @FXML
    private ComboBox<String> cboEnd;
    @FXML
    private ComboBox<Customer> cboCustomerName;
    @FXML
    private Button btnBack;

    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<String> times = FXCollections.observableArrayList();
    private static ZoneId zID = ZoneId.systemDefault();
    public Integer modifyID = null;
    public String user = LoginController.user;

    private void initCustomers() throws SQLException {
        customers = DBConnect.customerCombo();
        this.cboCustomerName.setItems(customers);

        this.cboCustomerName.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer.nameProperty().get();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });

    }

    private void initTimes() {
        LocalTime start = LocalTime.of(0, 0);

        for (int i = 0; i <= 95; i++) {
            this.times.add(start.toString());
            start = start.plusMinutes(15);
        }

        this.cboStart.setItems(this.times);
        this.cboEnd.setItems(this.times);
    }

    public static Timestamp convertUTC(String date, String time) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.parse(date + " " + time + ":00", dtf);
        ZonedDateTime zdt = localTime.atZone(zID);
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        localTime = utc.toLocalDateTime();
        Timestamp ts = Timestamp.valueOf(localTime);
        return ts;
    }

    @FXML
    void handleSave(ActionEvent event) throws SQLException {
        if (isValid()) {
            Timestamp startTime = convertUTC(this.dateDate.getValue().toString(), this.cboStart.getSelectionModel().getSelectedItem());
            Timestamp endTime = convertUTC(this.dateDate.getValue().toString(), this.cboEnd.getSelectionModel().getSelectedItem());
            if (this.modifyID == null) {
                DBConnect.addAppointment(
                        this.txtType.getText(),
                        this.cboCustomerName.getSelectionModel().getSelectedItem(),
                        this.txtLocation.getText(),
                        user,
                        startTime,
                        endTime,
                        this.txtDetails.getText()
                );
            } else {
                if (cboCustomerName.getSelectionModel().isEmpty()) {
                    DBConnect.updateAppointment(
                            modifyID,
                            this.txtType.getText(),
                            this.txtLocation.getText(),
                            startTime,
                            endTime,
                            this.txtDetails.getText()
                    );
                } else {
                    DBConnect.updateAppointment(
                            modifyID,
                            this.txtType.getText(),
                            this.cboCustomerName.getSelectionModel().getSelectedItem(),
                            this.txtLocation.getText(),
                            startTime,
                            endTime,
                            this.txtDetails.getText()
                    );
                }

            }
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static LocalDate convertDate(String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, format);
        return localDate;
    }

    private Boolean isValid() throws SQLException {
        String start = this.cboStart.getSelectionModel().getSelectedItem();
        String end = this.cboEnd.getSelectionModel().getSelectedItem();
        String date = this.dateDate.getValue().toString();
        String error;
        if (LocalTime.parse(start).getHour() >= LocalTime.parse(end).getHour()) {

            error = ("Cannot schedule start time before end time.");
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if ((LocalTime.parse(start).getHour() < 8 || LocalTime.parse(start).getHour() >= 18)
                || (LocalTime.parse(end).getHour() < 8 || LocalTime.parse(end).getHour() > 18)) {

            error = ("Please schedule appointment during business hours. (8am-6pm or 08:00-18:00)");
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }
        if (DBConnect.timeCheck(modifyID, convertUTC(date, start), convertUTC(date, end)) == true) {
            error = ("Cannot schedule overlapping appointments.");
            Alert alert = new Alert(AlertType.ERROR, error);
            alert.showAndWait();
            return false;
        }

        return true;
    }


    public void loadAppointment(Appointment app) throws SQLException {
        this.lblAppointment.setText("Modify Appointment");

        modifyID = app.getappointmentID();
        this.cboCustomerName.setPromptText(app.customerProperty().getValue());
        this.txtType.setText(app.typeProperty().getValue());
        this.txtDetails.setText(app.detailsProperty().getValue());
        this.txtLocation.setText(app.locationProperty().getValue());
        this.dateDate.setValue(convertDate(app.dateProperty().getValue()));
        this.cboStart.getSelectionModel().select(app.startProperty().getValue());
        this.cboEnd.getSelectionModel().select(app.endProperty().getValue());

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTimes();
        try {
            initCustomers();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        btnBack.setOnAction(new ReportsController()::btnPush);
    }

}
