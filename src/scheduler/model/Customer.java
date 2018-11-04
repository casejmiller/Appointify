package model;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author CMiller
 */
public class Customer {
    private final IntegerProperty customerID;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty city;
    private final StringProperty country;
    private final StringProperty postCode;
    private final StringProperty phone;
    
    public Customer(int id, String name, String address, String city, String country, String postCode, String phone) {
        this.customerID = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.city = new SimpleStringProperty(city);
        this.country = new SimpleStringProperty(country);
        this.postCode = new SimpleStringProperty(postCode);
        this.phone = new SimpleStringProperty(phone);
        
    }
    
    public Customer() {
        this.customerID = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.city = new SimpleStringProperty();
        this.country = new SimpleStringProperty();
        this.postCode = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
    }

    public Customer(int id, String name) {
        this.customerID = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.country = new SimpleStringProperty("");
        this.postCode = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
    }
    
    public StringProperty nameProperty() {
        return name;
    }
    
    public StringProperty addressProperty() {
        return address;
    }
    
    public IntegerProperty customerIDProperty() {
        return customerID;
    }    
    
    public StringProperty cityProperty() {
        return city;
    }
    
    public StringProperty countryProperty() {
        return country;
    }
    
    public StringProperty postCodeProperty() {
        return postCode;
    }
    
    public StringProperty phoneProperty() {
        return phone;
    }
    
    public int getCustomerID() {
        return this.customerID.get();
    }
    
    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    public void setCustomerName(String name) {
        this.name.set(name);
    }

    public void setCustomerAddress(String address) {
        this.address.set(address);
    }
    
    public void setCustomerCity (String city) {
        this.city.set(city);
    }
    
    public void setCustomerCountry(String country) {
        this.country.set(country);
    }
    
    public void setCustomerPostCode(String postCode) {
        this.postCode.set(postCode);
    }
    
    public void setCustomerPhone(String phone) {
        this.phone.set(phone);
    }
}