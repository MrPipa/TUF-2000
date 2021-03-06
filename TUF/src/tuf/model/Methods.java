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
            list.set(i, temp[1]); //replace #:[Value] with just value
        }
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

        //TODO fix hour, seems to be two hours behind
        String timestamp = day + "." + month + "." + year + "\t" + time; //Much nicer 31.12.2017    23:59
        return timestamp;
    }

    //Get low byte in decimal (string)
    public String getLowByteDecimalString(String s) { //Get low byte from raw integer value
        int value = 0;
        if(s.length() > 8){
            //String to integer:
            int tempInt = Integer.parseInt(s);
            
            //To binary:
            String binary = Integer.toBinaryString(tempInt);
            
            //Trim to 8 last bits:
            String last8 = binary.substring(binary.length()-8, binary.length());
            
            //Convert back to integer
            tempInt = bin2int(last8);
            
            //Convert to string
            s = Integer.toString(tempInt);
        }
        return s;
    }

    //BinaryString to Integer
    private int bin2int(String s) {
        return Integer.parseInt(s, 2); //Converting to integer from assumed BinaryString
    }

    //REAL4 to float value
    public float real4toFloat(String high, String low) {
        //This conversion is explained by Modbus, I will not go into detail how the conversion is made.
        //I am building the equation (-1)^sign * Mantissa * 2^exp as explained by Modbus
        if(high.equals("0") || low.equals("0")){
            return (float) 0;
        }

        int sign;
        int exp;
        double mant;

        //Convert to binary
        String bin1 = Integer.toBinaryString(Integer.parseInt(high));
        String bin2 = Integer.toBinaryString(Integer.parseInt(low));

        //add missing zeros to make 16 bits
        while (bin1.length() != 16) {
            bin1 = "0" + bin1; //add the zero in front where it is missing
        }
        while (bin2.length() != 16) {
            bin2 = "0" + bin2; //add the zero in front where it is missing
        }

        //Combine
        String bin = bin1 + bin2; //32bit single percision

        //Sign
        //From string to char so we can use "charAt" to get first bit
        //and then back to string (with new String(newchar[]{})) so it can be converted to integer
        sign = Integer.parseInt( //Convert String to integer (the sign)
                new String( //String can be converted to Integer
                        new char[]{ //Char array can be converted to String
                            bin.charAt(0) //index 0 = first bit in bin, which is the sign
                        }));

        //Exp (= next 7 bits in decimal - 127)
        char[] next8bits = new char[8];                    //First we make a char array to add the 8 bits to
        bin.getChars(1, 9, next8bits, 0);           //Add the bits to the array
        String next8 = new String(next8bits);
        exp = bin2int(next8);   //Convert the array to a String which we can convert to Integer (the value we want)
        exp -= 127;                             //finally subtract 127 so we get the exponent according to our equation

        //Mantissa
        String last23 = bin.substring(9);   //last 23 bits
        mant = bin2int(last23);             //converted to decimal value (integer)
        if ((exp + 127) > 0) {
            mant = (mant / 8388608) + 1; //8388608 is 800000 in hex which when divided by (and +1) gives the mantissa value.
        } else {
            mant = mant / 4194304;
        }

        double a = Math.pow(-1, sign);
        double b = mant;
        double c = Math.pow(2, exp);

        //Value (as double to prevent loss, some java thing)
        double value = a * b * c;
        
        //Check if HIGH and LOW bytes were in correct order: (If outside possible 32bit single percision float value)
        float max = (float) Math.pow(6.79, 38); //checked with ModBus excel for converting REAL4 to decimal
        float min = (float) Math.pow(1.4, -45); //checked with ModBus Excel for converting REAL4 to decimal
        
        if ((value <= min && value >= -min) || (value >= max && value <=  -max)) {
            System.out.println("Wrong value, switching HIGH: " + high + " and LOW: " + low);
            return real4toFloat(low, high);
        }
        return (float) value;
    }
}
