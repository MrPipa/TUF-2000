package tuf.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author Olli Arokari
 */
public class Data {
    
    private final SimpleStringProperty description;
    private final SimpleStringProperty value;
    private final SimpleStringProperty unit;
    
    public Data(String description, String value, String unit){
        this.description = new SimpleStringProperty(description);
        this.value = new SimpleStringProperty(value);
        this.unit = new SimpleStringProperty(unit);
    }
    
    public Data(String description, double value, String unit){
        this.description = new SimpleStringProperty(description);
        this.value = new SimpleStringProperty(Double.toString(value));
        this.unit = new SimpleStringProperty(unit);
    }
    
    public Data(String description, float value, String unit){
        this.description = new SimpleStringProperty(description);
        this.value = new SimpleStringProperty(Float.toString(value));
        this.unit = new SimpleStringProperty(unit);
    }
}
