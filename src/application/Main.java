/*
 * This project is similar to the Windows program "Paint"
 *
 * author @ Kaleb Mertz
 */
package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/Main.fxml"));
            Scene scene = new Scene(root);
            
            // set icon
            primaryStage.getIcons().add(new Image("/images/banana.png"));
            primaryStage.setTitle("Wow! This Program is Amazing!");
        
            scene.getStylesheets().add(getClass().getResource("paintproject.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
