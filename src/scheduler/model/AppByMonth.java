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
public class AppByMonth {
    
    private final StringProperty type;
    private final IntegerProperty count;
    
    public AppByMonth(String type, int count) {
        this.type = new SimpleStringProperty(type);
        this.count = new SimpleIntegerProperty(count);
    }


    public StringProperty typeProperty() {
        return type;
    }
    
    public IntegerProperty countProperty() {
        return count;
    }

}
