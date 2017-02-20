/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tuf.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import tuf.model.Methods;

/**
 *
 * @author Olli
 */
public class FXMLcontroller implements Initializable {
	
	private Methods m;
	private URL url;
        private ObservableList<String> output;
        private ObservableList<String> templist;
        
        private int indexTemp1High = 33;
        private int indexTemp1Low = 34;
        private int indexTemp2High = 35;
        private int indexTemp2Low = 36;
        private int indexSignalQuality = 92;
	
	private static FXMLcontroller instance;
	
	@FXML
	public ListView<String> listview;
	
	@FXML
	public Button quitbutton;
	
	@FXML
	public Button refreshbutton;
        
        @FXML
        public Text timestamp;
	
	@Override
	public void initialize(URL location, ResourceBundle resources){
            try {
                // TODO Controller init
                
                instance = this;
                m = Methods.getInstance();
                instance.url = new URL("http://tuftuf.gambitlabs.fi/feed.txt");
                
                listview.setPrefSize(900, 780);
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(FXMLcontroller.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	//TODO Button handlers
	@FXML
	private void refresh_btn_action(ActionEvent ev) throws Exception{
                output = FXCollections.observableArrayList();
                templist = m.fetch(url); //Get data
                timestamp.setText(m.getTime(templist)); //Set the time when data was taken according to the raw data
                templist = m.parse(templist); //Remove timestamp and "#:", leaving only raw data values
                output.add("Temperature #1 (inlet, C):\t" + m.getREAL4Value(templist.get(indexTemp1High), templist.get(indexTemp1Low)));
                output.add("Temperature #2 (outlet, C):\t" + m.getREAL4Value(templist.get(indexTemp2High), templist.get(indexTemp2Low)));
                output.add("Signal quality (0-99): " + m.getSignalQuality(templist.get(91))); //Set #92 (signal quality) to human readable value
                listview.setItems(output);
        }
        
	@FXML
	private void quit_btn_action(ActionEvent ev){
		System.exit(0);
	}
}