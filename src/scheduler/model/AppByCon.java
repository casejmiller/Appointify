/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author CMiller
 */
public class AppByCon {
    
    private final StringProperty name;
    private final IntegerProperty count;
    
    public AppByCon(String name, int count) {
        this.name = new SimpleStringProperty(name);
        this.count = new SimpleIntegerProperty(count);
    }


    public StringProperty nameProperty() {
        return name;
    }
    
    public IntegerProperty countProperty() {
        return count;
    }

}
