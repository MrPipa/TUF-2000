/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tuf.controller;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import tuf.model.Data;
import tuf.model.Methods;

/**
 *
 * @author Olli
 */
public class FXMLcontroller implements Initializable {
	
        //Values
	private Methods m;
	private URL url;
        private ObservableList<String> rawData = FXCollections.observableArrayList();
        private ObservableList<Data> values = FXCollections.observableArrayList();
        private ObservableList<String> description, value, unit = FXCollections.observableArrayList();
        
        //Controller instance
	private static FXMLcontroller instance;
        
        //Modbus register indexes
        private final int FlowHigh = 1;
        private final int FlowLow = 2;
        private final int EnergyFlowHigh = 3;
        private final int EnergyFlowLow = 4;
        private final int VelocityHigh = 5;
        private final int VelocityLow = 6;
        private final int FluidSoundSpeedHigh = 7;
        private final int FluidSoundSpeedLow = 8;
        private final int PositiveAccDecFracHigh = 11;
        private final int PositiveAccDecFracLow = 12;
        private final int Temp1High = 33;
        private final int Temp1Low = 34;
        private final int Temp2High = 35;
        private final int Temp2Low = 36;
        private final int SignalQuality = 92;
	
        //GUI elements
	@FXML private ListView descriptionList;
        @FXML private ListView valueList;
        @FXML private ListView unitList;
	@FXML private Button quitbutton;
	@FXML private Button refreshbutton;
        @FXML private Text timestamp;

        @Override
	public void initialize(URL location, ResourceBundle resources){
            instance = this;
            m = Methods.getInstance();
            
            try {                
                instance.url = new URL("http://tuftuf.gambitlabs.fi/feed.txt");
            } catch (MalformedURLException ex) {
                Logger.getLogger(FXMLcontroller.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	//TODO Button handlers
	@FXML
	private void refresh_btn_action(ActionEvent ev) throws Exception{
            
            //Data handling
            rawData = m.fetch(url); //Get data from live feed)
            timestamp.setText(m.getTime(rawData)); //Set the time when data was taken according to the raw data
            rawData = m.parse(rawData); //Remove "#:", leaving only raw data values
            rawData.set(0, timestamp.getText()); //set back the value of Time, in case neede in future
                
                
            //Fill table info      ***(table is actually three listviews that work as columns, couldnt get TableView to work)
            
            //Flow rate
            values.add(
                    new Data(
                        "Flow rate",
                        m.real4toFloat(rawData.get(FlowHigh), rawData.get(FlowLow)),
                        "m3/h"));

            //Energy flow rate
            values.add(
                    new Data(
                        "Energy flow rate",
                        m.real4toFloat(rawData.get(EnergyFlowHigh), rawData.get(EnergyFlowLow)),
                        "GJ/h"));

            //TODO +acc. long
            //Todo +acc decimal
            //Todo -acc. long
            //Todo -acc. decimal
            
            //Temperature (inlet)
            values.add(
                    new Data(
                        "Temperature #1 (Inlet)",
                        m.real4toFloat(rawData.get(Temp1High), rawData.get(Temp1Low)),
                        "C"));

            //Temperature (outlet)
            values.add(
                    new Data(
                        "Temperature #2 (Outlet)",
                        m.real4toFloat(rawData.get(Temp2High), rawData.get(Temp2Low)),
                        "C"));

            //Signal quality
            values.add(
                    new Data(
                        "Signal quality",
                        m.getLowByteDecimalString(rawData.get(SignalQuality)),
                        "0-99"));

            //Add to ListViews (columns)
            //Fill lists
            
            System.out.println(values.size() + " -should be 5"); //All Data() are in values
            
            for(Data data : values){ //For every Data
                description.add(data.getDescription());
                value.add(data.getValue());
                unit.add(data.getUnit());
            }
            //Set lists to columns
            descriptionList.setItems(description);
            valueList.setItems(value);
            unitList.setItems(unit);
            
        }
        
	@FXML
	private void quit_btn_action(ActionEvent ev){
            System.exit(0);
	}
}