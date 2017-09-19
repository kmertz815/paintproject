/*
 * MainController is the main class for the SceneBuilder App. All actions must
 * be in this MainController.java file to be referenced.
 *
 * @author Kaleb Mertz
 */

package application;

import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import static javafx.scene.paint.Color.BLACK;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;



public class MainController implements Initializable{
    
    Image img;
    File file;
    String imagepath;
    String fileName;
    String canvaspath;
    String canvasFileName;
    GraphicsContext gc;
    GraphicsContext gcTest;
    double imgHeight;
    double imgWidth;
    Canvas canvasNew;
    Color color = BLACK;
    ColorPicker colorPicker = new ColorPicker();
    
    @FXML
    Pane rootPane;
    @FXML
    StackPane stack;
    @FXML
    ImageView imgView;
    @FXML
    Canvas canvas;
    @FXML
    MenuButton freeDrawClick;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Set default background as white for Canvas snapshot:
        stack.setStyle("-fx-background-color: white");
        
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
            
            stack.setStyle("-fx-background-color: transparent;");
            
            imagepath = file.toURI().toURL().toString();
            fileName = file.getName();
            System.out.println("file:"+imagepath);
            img = new Image(imagepath);
            imgView.setImage(img);
            imgHeight = img.getHeight();
            imgWidth = img.getWidth();
            imgView.setFitHeight(imgHeight);
            imgView.setFitWidth(imgWidth);
            
            // Clear Canvas when opening new image:
            gc = canvas.getGraphicsContext2D(); 
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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

            ImageIO.write(SwingFXUtils.fromFXImage(imgView.getImage(),
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
                ImageIO.write(SwingFXUtils.fromFXImage(imgView.getImage(),
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
        
        //Pane root = new Pane();

        //StackPane holder = new StackPane();
        /*
        Canvas newCanvas = new Canvas(400, 300);

        stack.getChildren().add(newCanvas);
        rootPane.getChildren().add(stack);
        */
        stack.setStyle("-fx-background-color: white");
        //Scene scene = new Scene(rootPane, 600, 400);
        
        
        /*
        //stack.getChildren().add(new Button("OK"));
        gcTest = canvasNew.getGraphicsContext2D();
        drawShapes(gcTest);
        */
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
        
        int saveHeight;
        int saveWidth;
        
        if (img != null) {    // if an image was uploaded
            saveHeight = (int)imgView.getFitHeight();
            saveWidth = (int)imgView.getFitWidth();
        } else{               // else take original canvas size
            saveHeight = (int)canvas.getHeight();
            saveWidth = (int)canvas.getWidth();
        }
        
        WritableImage wim = new WritableImage(saveWidth, saveHeight);
        
        params.setFill(Color.TRANSPARENT);
        rootPane.snapshot(params, wim);
        
        File output = chooser.showSaveDialog(stage);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", output);
        } catch (Exception s) {
        }
        
    }
    
    
    // COLOR PICKER
    public void ColorPicker(ActionEvent event) {
        
        Stage stage = new Stage();
        
        colorPicker.setValue(color);
 
        final Circle circle = new Circle(50);
        color = colorPicker.getValue();
        circle.setFill(color);
 
        colorPicker.setOnAction((ActionEvent event1) -> {
            color = colorPicker.getValue();
            circle.setFill(color);
            // Reset Draw method (applying new color):
            //
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
    
    
    // ERASER
    public void Eraser(ActionEvent event) {
        
        /*
        // Clear away portions as the user drags the mouse
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent e) -> {
            gcTest.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
        });
        */
    }
    
    
    // CLEAR CANVAS
    public void ClearCanvas(ActionEvent event) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    
    // DRAW CONTINUOUSLY
    public void Draw(ActionEvent event) {
            
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        initDraw(graphicsContext);
        
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
                graphicsContext.stroke();
            }
        });
        
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                graphicsContext.lineTo(event.getX(), event.getY());
                graphicsContext.stroke();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                
            }
        });
        
        //stack.getChildren().add(canvas);
        
    }
    
    
    // DRAW COMMANDS
    private void initDraw(GraphicsContext gc){
        
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        
        /* Uncomment for Canvas border line:
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle
        */
        
        gc.rect(0, 0, canvasWidth, canvasHeight);
        
        //gc.fill();
        //gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(color);
        gc.setLineWidth(1);
        // gc.setLineWidth(5);

    }
    
    
    // ZOOM IN/OUT
    public void Zoom(ActionEvent event) {
        // COMING SOON
        
    }
    
    
    // RELEASE NOTES
    public void ReleaseNotes(ActionEvent event) {
    }
    
    
    // DRAW SHAPES EXAMPLE CODE
    private void drawShapes(GraphicsContext gcTest) {
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
