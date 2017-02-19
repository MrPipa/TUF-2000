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

    Methods() {}

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
                        temp.substring(     //"Select" the low byte
                                Integer.toBinaryString( //Convert integer value to binary
                                        Integer.parseInt(s)) //Make integer of the raw data value
                                .length() - 7))); //Choose only last 8 bits (LOW byte)
    }

    //Binary to Integer
    private int bin2int(String s) {
        return Integer.parseInt(s, 2); //Converting to integer from assumed BinaryString
    }

}
