package tuf.model;

/**
 * @author Olli Arokari
 */
public class Data {
    
    private String description = "";
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
        System.out.println("Is: " + description + ", but about to return: " + this.description);
        return description;
    }
    public String getValue(){
        System.out.println("Is: " + value + ", but about to return: " + this.value);
        return value;
    }
    public String getUnit(){
        System.out.println("Is: " + unit+ ", but about to return: " + this.unit);
        return unit;
    }
}
