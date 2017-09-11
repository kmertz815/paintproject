/*
 * MainController is the main class for the SceneBuilder App. All actions must
 * be in this MainController.java file to be referenced.
 *
 * @author Kaleb Mertz
 */

package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import sun.net.www.content.image.png;


public class MainController implements Initializable{
    
    Image img;
    File file;
    String imagepath;
    String fileName;
    String canvaspath;
    String canvasFileName;
    Color color;
    
    
    @FXML
    ImageView imgpic;
    @FXML
    Canvas newCanvas;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    
    // CLOSE APP
    public void CloseApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    
    
    // OPEN IMAGE FILE
    public void OpenImage(ActionEvent actionEvent) throws MalformedURLException {
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Image File");
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg",
                        "*.gif", "*.bmp", "*.tiff"));
        file = chooser.showOpenDialog(new Stage());
        
        if(file != null) {
            imagepath = file.toURI().toURL().toString();
            fileName = file.getName();
            System.out.println("file:"+imagepath);
            img = new Image(imagepath);
            imgpic.setImage(img);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Is everything Okay?");
            alert.setContentText("No image file was selected!");
            alert.showAndWait();
        }
    }
    
    
    // SAVE IMAGE
    public void SaveImage(ActionEvent actionEvent) throws IOException {
                
        if (file != null) {

            ImageIO.write(SwingFXUtils.fromFXImage(imgpic.getImage(),
                    null), "png", file);

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("What are you doing?");
            alert.setContentText("You have no image open!");
            alert.showAndWait();
        }
    }
    
    
    // Save IMAGE AS
    public void SaveImageAs(ActionEvent actionEvent) throws IOException {
        
        Stage stage = new Stage();
        
        if (file != null) {
            
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Image Files", "*"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("TIFF", "*.tiff")
            );
            chooser.setInitialFileName(fileName);
            chooser.setTitle("Save Image");

            
            File fileTemp = chooser.showSaveDialog(stage);
            
            if (fileTemp != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(imgpic.getImage(),
                        null), "png", fileTemp);
                file = fileTemp;
                fileName = file.getName();
            } else{
                // Exception handling for closing save dialog window
            }
        
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("What are you doing?");
            alert.setContentText("You have no image open!");
            alert.showAndWait();
        }
    }
    
    
    // CREATE NEW CANVAS
    public void NewCanvas(ActionEvent actionEvent) {
        
        GraphicsContext gc = newCanvas.getGraphicsContext2D();
        drawShapes(gc);
        
    }

    
    // SAVE CANVAS AS
    public void SaveCanvasAs(ActionEvent actionEvent) {
        
        Stage stage = new Stage();
        
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Image Files", "*"),
            new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
            new FileChooser.ExtensionFilter("GIF", "*.gif"),
            new FileChooser.ExtensionFilter("BMP", "*.bmp"),
            new FileChooser.ExtensionFilter("TIFF", "*.tiff")
        );
        chooser.setInitialFileName("snapshot" + new Date().getTime() + ".png");
        chooser.setTitle("Save Canvas");
            
        SnapshotParameters params = new SnapshotParameters();
        WritableImage wim = new WritableImage(300, 250);
        
        params.setFill(Color.TRANSPARENT);
        newCanvas.snapshot(params, wim);
        
        File output = chooser.showSaveDialog(stage);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", output);
        } catch (Exception s) {
        }
        
    }
    
    
    // COLOR PICKER
    public void ColorPicker(ActionEvent event) {
        
        Stage stage = new Stage();
        
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.RED);
 
        final Circle circle = new Circle(50);
        color = colorPicker.getValue();
        circle.setFill(color);
 
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                color = colorPicker.getValue();
                circle.setFill(color);
                
            }
        });
 
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.getChildren().addAll(circle, colorPicker);
 
        Scene scene = new Scene(root, 350, 130);
 
        stage.setTitle("Kaleb's Color Picker");
        stage.getIcons().add(new Image("/images/banana.png"));
 
        stage.setScene(scene);
        stage.show();
        
        
        /* This code is for a MenuButton Color Picker:
        Stage stage = new Stage();
        
        Scene scene = new Scene(new HBox(20), 400, 100);
        HBox box = (HBox) scene.getRoot();
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.RED);

        final Text text = new Text("Color picker:");
        text.setFill(colorPicker.getValue());

        colorPicker.setOnAction((ActionEvent t) -> {
          text.setFill(colorPicker.getValue());
        });

        box.getChildren().addAll(colorPicker, text);

        stage.setScene(scene);
        stage.show();
        */
    }
    
    // RELEASE NOTES
    public void ReleaseNotes(ActionEvent event) {
    }
    
    // DRAW SHAPES EXAMPLE CODE
    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }
    
    
}
