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
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
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
        
        //List for input
        private ObservableList<String> rawData = FXCollections.observableArrayList();
        
        //List for output
        private ObservableList<Data> values = FXCollections.observableArrayList();
        
        //Columns (UI)
        private ObservableList<String> description = FXCollections.observableArrayList();
        private ObservableList<String> value = FXCollections.observableArrayList();
        private ObservableList<String> unit = FXCollections.observableArrayList();
        
        //Controller instance
	private static FXMLcontroller instance;
        
        //Modbus register indexes
        private final int FlowHigh = 1;
        private final int FlowLow = 2;
        private final int EnergyFlowLow = 3;
        private final int EnergyFlowHigh = 4;
        private final int VelocityLow = 5;
        private final int VelocityHigh = 6;
        private final int FluidSoundSpeedLow = 7;
        private final int FluidSoundSpeedHigh = 8;
        private final int PositiveAccDecFracLow = 11;
        private final int PositiveAccDecFracHigh = 12;
        
        private final int Temp1Low = 33;
        private final int Temp1High = 34;
        private final int Temp2Low = 35;
        private final int Temp2High = 36;
        private final int AI3Low = 37;
        private final int AI3High = 38;
        private final int AI4Low = 39;
        private final int AI4High = 40;
        private final int AI5Low = 41;
        private final int AI5High = 42;
        private final int CInputAI3Low = 43;
        private final int CInputAI3High = 44;
        private final int CInputAI4Low = 45;
        private final int CInputAI4High = 46;
        private final int CInputAI5Low = 47;
        private final int CInputAI5High = 48;
        
        private final int ErrorCode = 72;
        private final int PT100RInletHigh = 73;
        private final int PT100RInletLow = 74;
        private final int PT100ROutletHigh = 75;
        private final int PT100ROutletLow = 76;
        
        private final int SignalQuality = 92;
	
        //GUI elements
	@FXML private ListView descriptionList;
        @FXML private ListView valueList;
        @FXML private ListView unitList;
	@FXML private Button quitbutton;
	@FXML private Button refreshbutton;
        @FXML private TextArea console;

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
	
	/*
        * Button handlers
        */
        
	@FXML //Refresh button pressed
	private void refresh_btn_action(ActionEvent ev) throws Exception{
            
            //Data handling
            rawData = m.fetch(url); //Get data from live feed)
            console.setText("Data collected:\n");
            console.setText(console.getText() + m.getTime(rawData)); //Set the time when data was taken according to the raw data
            rawData = m.parse(rawData); //Remove "#:", leaving only raw data values in rawData list
                
            //Clear "values" list for new information
            values.clear();
            
            /*
            *   Fill table info      ***(table is actually three listviews that work as columns, couldnt get TableView to work)
            */
            
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

            //+acc. long
            //+acc decimal
            //-acc. long
            //-acc. decimal
            
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
            
            //Analog input AI3
            values.add(
                    new Data(
                        "Analog input AI3",
                        m.real4toFloat(rawData.get(AI3High), rawData.get(AI3Low)),
                        "mA"));
            
            //Analog input AI4
            values.add(
                    new Data(
                        "Analog input AI4",
                        m.real4toFloat(rawData.get(AI4High), rawData.get(AI4Low)),
                        "mA"));
            
            //Analog input AI5
            values.add(
                    new Data(
                        "Analog input AI5",
                        m.real4toFloat(rawData.get(AI5High), rawData.get(AI5Low)),
                        "mA"));
            
            //Error code
            values.add(
                    new Data(
                        "Error code",
                        rawData.get(ErrorCode),
                        "mA"));

            //Signal quality
            values.add(
                    new Data(
                        "Signal quality",
                        m.getLowByteDecimalString(rawData.get(SignalQuality)),
                        "0-99"));

            /*
            *   Set information to ListViews to present data to user
            */
            
            //Clear lists if refresh has been pressed earlier
            description.clear();
            value.clear();
            unit.clear();
            
            //TODO "headers", will be fixed later
            description.add("Description:");
            value.add("Value:");
            unit.add("Unit:");
            description.add("");
            value.add("");
            unit.add("");
            
            //Fill lists
            for(Data data : values){ //For every Data
                description.add(data.getDescription());
                value.add(data.getValue());
                unit.add(data.getUnit());
            }
            
            //Insert lists to columns
            descriptionList.setItems(description);
            valueList.setItems(value);
            unitList.setItems(unit);
        }
        
	@FXML //Quit button pressed
	private void quit_btn_action(ActionEvent ev){
            System.exit(0);
	}
        
        /*
        *   Mouse clicked handlers
        */
        @FXML
        private void unitCellClicked(MouseEvent click){
            if(click.getClickCount() >= 2){
                String unit = (String) unitList.getSelectionModel().getSelectedItem();
                if(unit.equals("m3/h")){
                    JOptionPane.showMessageDialog(null, "m3/h\n\nCubic meters per hour");
                }
                else if(unit.equals("C")){
                    JOptionPane.showMessageDialog(null, "C\n\nDegrees in Celsius");
                }
                else if(unit.equals("mA")){
                    JOptionPane.showMessageDialog(null, "mA\n\nMilli Ampere ( mA*1000 = A)");
                }
                else if(unit.equals("0-99")){
                    JOptionPane.showMessageDialog(null, "0-99\n\nA constant, bigger is better.");
                }
                else if(unit.equals("GJ/h")){
                    JOptionPane.showMessageDialog(null, "GJ/h\n\nGiga Joule per hour (Giga = 10^9)");
                }
            }
        }
}