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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.DBConnect;
import scheduler.model.AppByCon;
import scheduler.model.AppByMonth;
import scheduler.model.Appointment;

/**
 * FXML Controller class
 *
 * @author CMiller
 */
public class ReportsController implements Initializable {

    @FXML
    private TableView<Appointment> tvConApp;
    @FXML
    private TableColumn<Appointment, String> colDate;
    @FXML
    private TableColumn<Appointment, String> colStart;
    @FXML
    private TableColumn<Appointment, String> colEnd;
    @FXML
    private TableColumn<Appointment, String> colCustomer;
    @FXML
    private TableColumn<Appointment, String> colType;
    @FXML
    private TableColumn<Appointment, String> colDetails;
    @FXML
    private TableColumn<Appointment, String> colLocation;
    @FXML
    private ComboBox<String> cboConsultant;

    @FXML
    private ComboBox<String> cboMonth;
    @FXML
    private TableView<AppByMonth> tvAppByMonth;
    @FXML
    private TableColumn<AppByMonth, String> colTypeOfApp;
    @FXML
    private TableColumn<AppByMonth, Integer> colAppCount;

    @FXML
    private TableView<AppByCon> tvAppByCon;
    @FXML
    private TableColumn<AppByCon, String> colConsultant;
    @FXML
    private TableColumn<AppByCon, Integer> colConCount;

    @FXML
    private Button btnBack;

    ObservableList<String> consultants = FXCollections.observableArrayList();
    ObservableList<String> months = FXCollections.observableArrayList(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
    );

    @FXML
    private void handlePopulateConApp(ActionEvent event) throws SQLException {
        String selCon = cboConsultant.getSelectionModel().getSelectedItem();
        ObservableList records = DBConnect.getConApps(selCon);
        tvConApp.setItems(records);
    }

    @FXML
    private void handlePopulateAppByMonth(ActionEvent event) throws SQLException {
        String selMonth = cboMonth.getSelectionModel().getSelectedItem();
        ObservableList records = DBConnect.getAppByMonth(selMonth);
        tvAppByMonth.setItems(records);
    }

    private void initConsultants() throws SQLException {
        consultants = DBConnect.popConsultants();
        this.cboConsultant.setItems(consultants);
    }

    private void initMonths() {
        this.cboMonth.setItems(months);
    }

    private void popConCount() throws SQLException {
        ObservableList records = DBConnect.getAppByCon();
        tvAppByCon.setItems(records);
    }
    
    //used for lambda for back button, and reference for other back buttons.
    public void btnPush(ActionEvent event) {
        try{
        Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colStart.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        colEnd.setCellValueFactory(cellData -> cellData.getValue().endProperty());
        colCustomer.setCellValueFactory(cellData -> cellData.getValue().customerProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colDetails.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());
        colLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());

        colConsultant.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colConCount.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject());

        colTypeOfApp.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colAppCount.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject());

        initMonths();
        try {
            initConsultants();
            popConCount();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        //lambda for back button
        btnBack.setOnAction( (ActionEvent e) -> btnPush(e) );
    }

}
