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
import java.util.ArrayDeque;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import static javafx.scene.paint.Color.BLACK;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
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
    WritableImage wim;
    int saveHeight;
    int saveWidth;
    int lineWidth;
    Rectangle dragBox = new Rectangle(0, 0, 0, 0);
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    ImageView imgViewCut;
    
    private Pair<Double, Double> initialTouch;
    ArrayDeque<WritableImage> dequeUndo = new ArrayDeque<>();
    ArrayDeque<WritableImage> dequeRedo = new ArrayDeque<>();
    
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
    @FXML
    Slider lineWidthSlider;
    @FXML
    Circle colorCircle;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Set default background as white for Canvas snapshot:
        rootPane.setStyle("-fx-background-color: white");
        //canvas.setStyle("-fx-background-color: white");
        
        lineWidthSlider.setValue(5);
        
        screenshot();  // take initial screenshot
        loadScreenshot();
        dequeUndo.push(wim);
        
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
            
            rootPane.setStyle("-fx-background-color: transparent");
            
            screenshot();
            loadScreenshot();
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
    
    
    // SAVE IMAGE AS
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
            
        screenshot();
        
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
 
        Circle colorCircle2 = new Circle(50);
        
        color = colorPicker.getValue();
        colorCircle.setFill(color);
        colorCircle2.setFill(color);
 
        colorPicker.setOnAction((ActionEvent event1) -> {
            color = colorPicker.getValue();
            colorCircle.setFill(color);
            colorCircle2.setFill(color);
            // Reset Draw method (applying new color):
            //
        });
 
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.getChildren().addAll(colorCircle2, colorPicker);
        
        
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
        
        clearMouseEvents();
        
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        initDraw(graphicsContext);
        gc.setStroke(color.WHITE);
        
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
                graphicsContext.stroke();
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                graphicsContext.lineTo(event.getX(), event.getY());
                graphicsContext.stroke();
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
            }
        });
        
    }
    
    
    // CLEAR CANVAS
    public void ClearCanvas(ActionEvent event) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    
    // DRAW CONTINUOUSLY
    public void Draw(ActionEvent event) {
        
        clearMouseEvents();
        
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        initDraw(graphicsContext);
        
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
                graphicsContext.stroke();
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                graphicsContext.lineTo(event.getX(), event.getY());
                graphicsContext.stroke();
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
            }
        });
        
        /*
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
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
            }
        });
        
        //stack.getChildren().add(canvas);
        
        */
    }
    
    
    // DRAW LINE
    public void DrawLine(ActionEvent event) {
        
        clearMouseEvents();
        loadScreenshot();
        
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        initDraw(graphicsContext);

        
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                screenshot();
                loadScreenshot();
                //GraphicsContext context = canvas.getGraphicsContext2D();
                //initDraw(context);

                initialTouch = new Pair<>(event.getX(), event.getY());
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                //GraphicsContext context = canvas.getGraphicsContext2D();
                graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                graphicsContext.strokeLine(initialTouch.getKey(), initialTouch.getValue(), event.getX(), event.getY());
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
            }
        });
        
        /* Other option for mouse events:
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>(){

                @Override
                public void handle(MouseEvent event) {

                    screenshot();
                    loadScreenshot();
                    //GraphicsContext context = canvas.getGraphicsContext2D();
                    //initDraw(context);

                    initialTouch = new Pair<>(event.getX(), event.getY());
                }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                //GraphicsContext context = canvas.getGraphicsContext2D();
                graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                graphicsContext.strokeLine(initialTouch.getKey(), initialTouch.getValue(), event.getX(), event.getY());
            }
        });
        
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
            }
        });
        */
        
        
    }
    
    
    // DRAW SQUARE
    public void DrawSquare(ActionEvent event) {
        
        clearMouseEvents();
        loadScreenshot();
        
        Rectangle dragBox = new Rectangle(0, 0, 0, 0);
        dragBox.setStroke(color);
        dragBox.setStrokeWidth(lineWidthSlider.getValue());
        dragBox.setFill(color.TRANSPARENT);
        dragBox.setVisible(false);
        
        rootPane.getChildren().add(dragBox);
        
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                loadScreenshot();
                
                dragBox.setVisible(true);
                dragBox.setTranslateX(event.getX());
                dragBox.setTranslateY(event.getY());
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                dragBox.setWidth(event.getX() - dragBox.getTranslateX());
                dragBox.setHeight(event.getY() - dragBox.getTranslateY());    
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
                dragBox.setVisible(false); // screenshot taken, so remove box
            }
        });
        
        /*
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                
                loadScreenshot();
                
                dragBox.setVisible(true);
                dragBox.setTranslateX(event.getX());
                dragBox.setTranslateY(event.getY());
            }
        });
        
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                dragBox.setWidth(event.getX() - dragBox.getTranslateX());
                dragBox.setHeight(event.getY() - dragBox.getTranslateY());                
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
                new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                screenshot();
                loadScreenshot();
                dequeUndo.push(wim);
                //dragBox.setVisible(false);
            }
        });
        */
        
        
    }
    
    
    // DRAW CIRCLE
    public void DrawCircle(ActionEvent event) {
        // COMING SOON
    }
    
    
    // INSERT TEXT
    public void InsertText(ActionEvent event) {
        
        clearMouseEvents();
        loadScreenshot();
        
        TextField textField = new TextField ();
        HBox hbox = new HBox(textField);
        hbox.setMinWidth(800);
        
        textField.setStyle("-fx-text-inner-color: black;"
                + "-fx-background-color: transparent;");
        textField.setPromptText("Click Enter Once You've Completed Typing");
        hbox.setHgrow(textField, Priority.ALWAYS);
        
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                
                hbox.setVisible(true);
                hbox.setTranslateX(event.getX());
                hbox.setTranslateY(event.getY());
                
                rootPane.getChildren().add(hbox);

                rootPane.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
                    if (ev.getCode() == KeyCode.ENTER) {
                        screenshot();
                        dequeUndo.push(wim);
                        hbox.setVisible(false);
                        loadScreenshot();
                        ev.consume(); 
                    }
                });
            }
        });
        
        
        
    }
    
    
    // CUT AND MOVE
    public void CutAndMove(ActionEvent event) {
        
        clearMouseEvents();
        
        // Draw Rectangle for Cut boundaries
        Rectangle dragBox = new Rectangle(0, 0, 0, 0);
        dragBox.setStroke(color.BLACK);
        dragBox.setStrokeWidth(1);
        dragBox.getStrokeDashArray().addAll(3.0,7.0,3.0,7.0);
        dragBox.setFill(color.TRANSPARENT);
        dragBox.setVisible(false);
        
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                loadScreenshot();
                
                dragBox.setVisible(true);
                dragBox.setTranslateX(event.getX());
                dragBox.setTranslateY(event.getY());
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                dragBox.setWidth(event.getX() - dragBox.getTranslateX());
                dragBox.setHeight(event.getY() - dragBox.getTranslateY());    
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                
                dragBox.setVisible(false);
                
                SnapshotParameters params = new SnapshotParameters();
        
                WritableImage imgCut = new WritableImage((int)dragBox.getWidth(),
                        (int)dragBox.getHeight());

                params.setFill(Color.TRANSPARENT);
                Rectangle2D viewport = new Rectangle2D(
                        dragBox.getTranslateX(), 
                        dragBox.getTranslateY() + 117,  // 117 adds the Menu height
                        dragBox.getWidth(), dragBox.getHeight());
                params.setViewport(viewport);
                rootPane.snapshot(params, imgCut);

                //Image imgCut = new Image("/images/banana.png");  // for testing

                imgViewCut = new ImageView();
                imgViewCut.setVisible(true);
                imgViewCut.setImage(imgCut);
                
                rootPane.getChildren().addAll(imgViewCut);
                
                imgViewCut.setCursor(Cursor.HAND);
                imgViewCut.setTranslateX(dragBox.getTranslateX());
                imgViewCut.setTranslateY(dragBox.getTranslateY());
                imgViewCut.setOnMousePressed(ImageViewOnMousePressedEventHandler);
                imgViewCut.setOnMouseDragged(ImageViewOnMouseDraggedEventHandler);
                imgViewCut.setOnMouseReleased(ImageViewOnMouseReleasedEventHandler);
                
                //Group root = new Group();
                //rootPane.getChildren().addAll(imgViewCut);

            }
        });
        
        rootPane.getChildren().addAll(dragBox);
        
    }
    
    
    // UNDO
    public void Undo(ActionEvent event) {
        wim = dequeUndo.pop();
        loadScreenshot();
        dequeRedo.push(wim);
    }
    
    
    // REDO
    public void Redo(ActionEvent event) {
        wim = dequeRedo.pop();
        loadScreenshot();
        dequeUndo.push(wim);
    }
    
    
    // ZOOM IN/OUT
    public void Zoom(ActionEvent event) {
        // COMING SOON
        
    }
    
    
    // Drawing commands
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
        gc.setLineWidth(lineWidthSlider.getValue());
        // gc.setLineWidth(5);

    }
    
    
    // Clear all running mouse action events
    public void clearMouseEvents(){
        canvas.setOnMouseClicked(null);
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        canvas.setOnMouseReleased(null);
        //dragBox.setOnDragOver(null);
        //dragBox.setOnDragDetected(null);
        //imgViewCut.setOnMousePressed(null);
        //imgViewCut.setOnMouseDragged(null);      
    }
    
    
    // Take a screenshot of current imageView
    public void screenshot(){
        SnapshotParameters params = new SnapshotParameters();

        if (img != null) {    // if an image was uploaded
            saveHeight = (int)imgView.getFitHeight();
            saveWidth = (int)imgView.getFitWidth();
        } else{               // else take original canvas size
            saveHeight = (int)canvas.getHeight();
            saveWidth = (int)canvas.getWidth();
        }

        wim = new WritableImage(saveWidth, saveHeight);

        params.setFill(Color.TRANSPARENT);
        rootPane.snapshot(params, wim);
    }
    
    
    // Load the screenshot back onto the pane
    public void loadScreenshot(){
        stack.setStyle("-fx-background-color: transparent;");

        imgView.setImage(wim);
        imgHeight = wim.getHeight();
        imgWidth = wim.getWidth();
        imgView.setFitHeight(saveHeight);
        imgView.setFitWidth(saveWidth);
        
        //Clear Canvas when opening new image:
        gc = canvas.getGraphicsContext2D(); 
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    
    // RELEASE NOTES
    public void ReleaseNotes(ActionEvent event) {
    }
    
    
    // Draw Shapes example code
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
    
    
    
    // Event Handling
    
    EventHandler<MouseEvent> ImageViewOnMousePressedEventHandler = 
        new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            orgTranslateX = ((ImageView)(t.getSource())).getTranslateX();
            orgTranslateY = ((ImageView)(t.getSource())).getTranslateY();
            
            // Add whitespace where cutout was
            Rectangle rect = new Rectangle(
                        dragBox.getTranslateX(), 
                        dragBox.getTranslateY(),  // 117 adds the Menu height
                        dragBox.getWidth(),
                        dragBox.getHeight());
            
            rect.setStroke(color.TRANSPARENT);
            rect.setStrokeWidth(0);
            rect.setFill(color.WHITE);

            rootPane.getChildren().addAll(rect);
        }
    };

    EventHandler<MouseEvent> ImageViewOnMouseDraggedEventHandler = 
        new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
                        
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            ((ImageView)(t.getSource())).setTranslateX(newTranslateX);
            ((ImageView)(t.getSource())).setTranslateY(newTranslateY);
        }
    };
    
    EventHandler<MouseEvent> ImageViewOnMouseReleasedEventHandler = 
        new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            screenshot();
            loadScreenshot();
            dequeUndo.push(wim);
            imgViewCut.setVisible(false);
        }
    };
    
    
}


