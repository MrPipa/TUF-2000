package tuf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Olli Arokari
 */
public class TUF extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setResizable(false);
            FXMLLoader main = new FXMLLoader(getClass().getResource("view/FXMLview.fxml"));
            Scene mainScene = new Scene(main.load());
            primaryStage.setScene(mainScene);			
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void main(String[] args) {
            launch(args);
    }
}
