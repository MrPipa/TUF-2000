package tuf.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Olli
 */
public class Methods {

    private static Methods instance;

    public static synchronized Methods getInstance() {
        if (instance == null) {
            instance = new Methods();
        }
        return instance;
    }

    Methods() {
    }

    //Get raw data
    public ObservableList fetch(URL url) throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        url.openStream()));
        String line;
        ObservableList rawdata = FXCollections.observableArrayList();
        while ((line = br.readLine()) != null) {
            rawdata.add(line);
        }
        return rawdata;
    }

    //Parse list
    public ObservableList<String> parse(ObservableList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String[] temp = list.get(i).split(":"); //temp[0] is index and temp[1] will be the raw data value
            list.set(i, temp[1]); //replace #:[Value] with just value in list index [#-1]
        }
        list.remove(0); //Remove the timestamp (already set in GUI, now it wont come up in the list)
        return list;
    }

    //Get time
    public String getTime(ObservableList<String> list) {
        String[] temp = list.get(0).split("-"); //[year]-[month]-[day] HH:MM -> year, month, day hh:mm
        String year = temp[0];
        String month = temp[1];
        String[] dayNtime = temp[2].split(" "); //[day] hh:mm  -> day, hh:mm
        String day = dayNtime[0];
        String time = dayNtime[1];

        String timestamp = day + "." + month + "." + year + "\t" + time; //Much nicer 31.12.2017    23:59
        return timestamp;
    }

    //Get Signal Quality
    public String getSignalQuality(String s) { //Get low byte from raw integer value
        String temp = Integer.toBinaryString(Integer.parseInt(s));
        return Integer.toString( //convert the result from integer to string for final output (ListView)
                bin2int( //convert the low byte from binary to integer
                        temp.substring( //"Select" the low byte
                                Integer.toBinaryString( //Convert integer value to binary
                                        Integer.parseInt(s)) //Make integer of the raw data value
                                        .length() - 7))); //Choose only last 8 bits (LOW byte)
    }

    //Binary to Integer
    private int bin2int(String s) {
        return Integer.parseInt(s, 2); //Converting to integer from assumed BinaryString
    }

    //Temp #1 (IN)
    public String getREAL4Value(String high, String low) {
        return Float.toString(r42f(high, low));
    }

    //REAL4 to float value
    private float r42f(String high, String low) {
        //This conversion is explained by Modbus, I will not go into detail how the conversion is made.
        //I am building the equation (-1)^sign * Mantissa * 2^exp as explained by Modbus

        int sign;
        int exp;
        int mant;
        
        //Convert to binary
        String bin1 = Integer.toBinaryString(Integer.parseInt(high));
        String bin2 = Integer.toBinaryString(Integer.parseInt(low));
        
        //add missing zeros to make 16 bits
        while (bin1.length() != 16){
            bin1 = "0" + bin1; //add the zero in front where it is missing
        }
        while (bin2.length() != 16){
            bin2 = "0" + bin2; //add the zero in front where it is missing
        }
        System.out.println("high:\t" + bin1 + " : " + bin1.length());
        System.out.println("low:\t" + bin2 + " : " + bin2.length());
        

        //Combine
        String bin = bin1 + bin2; //32bit single percision
        System.out.println("combined:\t" + bin + " : " + bin.length());
        //Sign
        //From string to char so we can use "charAt" to get first bit
        //and then back to string (with new String(newchar[]{})) so it can be converted to integer
        sign = Integer.parseInt(new String(new char[]{bin.charAt(0)})); //We want the first "char" from the string => index[0]

        //Exp (= next 7 bits in decimal - 127)
        char[] next8 = new char[8];                    //First we make a char array to add the 8 bits to
        bin.getChars(1, 9, next8, 0);           //Add the bits to the array
        System.out.println("next8:");
        for (char c : next8){
            System.out.println(c);
        }
        exp = bin2int(new String(next8));   //Convert the array to a String which we can convert to Integer (the value we want)
        exp -= 127;                             //finally subtract 127 so we get the exponent according to our equation

        //Mantissa
        char[] last23 = new char[23];
        bin.getChars(9, 31, last23, 0);
        System.out.println("last23");
        for (char c : last23){
            System.out.println(c);
        }
        mant = bin2int(new String(last23));
        if((exp+127) > 0){
            mant = (mant/(8388608+1)); //8388608 is 800000 in hex which when divided by (and +1) gives the mantissa value.
        }
        
        //Value (as double to prevent loss)
        double value = Math.pow(-1, sign) * mant * Math.pow(2, exp);
        return (float) value;
    }
}
