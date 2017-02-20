package tuf.model;

/**
 * @author Olli Arokari
 */
public class Data {
    
    private String description = "Default description";
    private String value = "";
    private String unit = "";
    
    public Data(String description, String value, String unit){
        this.description = description;
        this.value = value;
        this.unit = unit;
    }
    
    public Data(String description, double value, String unit){
        this.description = description;
        this.value = Double.toString(value);
        this.unit = unit;
    }
    
    public Data(String description, float value, String unit){
        this.description = description;
        this.value = Float.toString(value);
        this.unit = unit;
    }
    
    public String getDescription(){
        return description;
    }
    public String getValue(){
        return value;
    }
    public String getUnit(){
        return unit;
    }
}
