package cs1302.gallery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.io.IOException;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.animation.Animation.Status;


/**
 * Represents an iTunes GalleryApp.
 */
public class GalleryApp extends Application {

    // For the menu
    MenuBar menuBar;
    Menu menu;
    MenuItem exit;
    Menu help;
    MenuItem about;

    // For the toolbar
    HBox toolbar;
    Button play;
    Label text;
    TextField search;
    Button update;
    Timeline timeline;

    // For the main content
    VBox content;
    HBox ivc1;
    HBox ivc2;
    HBox ivc3;
    HBox ivc4;
    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    ImageView iv4;
    ImageView iv5;
    ImageView iv6;
    ImageView iv7;
    ImageView iv8;
    ImageView iv9;
    ImageView iv10;
    ImageView iv11;
    ImageView iv12;
    ImageView iv13;
    ImageView iv14;
    ImageView iv15;
    ImageView iv16;
    ImageView iv17;
    ImageView iv18;
    ImageView iv19;
    ImageView iv20;
    ImageView[] ivArray;
    ArrayList<String> stringList;

    // For the progress bar
    HBox bottom;
    ProgressBar progress;
    Label credits;
    Alert exception;
    Alert not21;

    /**
     * Initializes instance variables, sets up the scene graph, and starts the Gallery App program.
     * @param stage stage for the gallary
     */
    @Override
    public void start(Stage stage) {
        // Instance variables for the menu
        menuBar = new MenuBar();
        help = new Menu("Help");
        menu = new Menu("File");
        menuBar.getMenus().addAll(menu, help);
        exit = new MenuItem("Exit");
        exit.setOnAction(action -> System.exit(0));
        menu.getItems().add(exit);
        about = new MenuItem("About");
        about.setOnAction(e -> aboutAuthor());
        help.getItems().add(about);
        // Instance variables for the toolbar
        toolbar = new HBox();
        // Sets up the timeline
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        EventHandler<ActionEvent> handler = event -> swapImages(stringList);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        // Sets up the buttons
        play = new Button("Pause");
        play.setOnAction(playEvent -> updateTimeline(timeline, play));
        text = new Label("Search Query:");
        search = new TextField();
        update = new Button("Update Images");
        Runnable r = () -> {
            boolean running = false;
            if (timeline.getStatus() == Status.RUNNING) {
                updateTimeline(timeline, play);
                running = true;
            } // if
            runQuery(timeline);
            if (running) {
                updateTimeline(timeline, play);
            } // if
        }; // r
        update.setOnAction(ae -> runNow(r));
        toolbar.getChildren().addAll(play, text, search, update);
        // Instance variables for main content
        content = new VBox();
        ivc1 = new HBox();
        ivc2 = new HBox();
        ivc3 = new HBox();
        ivc4 = new HBox();
        declareImages();
        content.getChildren().addAll(ivc1, ivc2, ivc3, ivc4);
        // Instance variables for progress bar
        bottom = new HBox();
        progress = new ProgressBar();
        credits = new Label("Images provided courtesy of iTunes");
        bottom.getChildren().addAll(progress, credits);
        // Sets up the stage and scene
        VBox pane = new VBox();
        pane.getChildren().addAll(menuBar, toolbar, content, bottom);
        setMyStage(stage, pane);
        initImages(timeline);
    } // start

    /**
     * Sets the stage and scene for the application and shows it.
     * @param stage stage of the application
     * @param pane VBox holding the entire GUI
     */
    public void setMyStage(Stage stage, VBox pane) {
        Scene scene = new Scene(pane);
        stage.setMinWidth(502);
        stage.setMinHeight(590);
        stage.setMaxWidth(1280);
        stage.setMaxHeight(720);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    } // setMyStage

    /**
     * sets up the images with a default search for jack johnson.
     * @param timeline an empty timeline
     */
    public void initImages(Timeline timeline) {
        // url for jack johnson search results on iTunes
        String surl = "https://itunes.apple.com/search?term=jack+johnson&limit=200&media=music";
        parseImages(surl, timeline);
    } // initImages


    /**
     * Runs a query based on the text in the text field by making a URL.
     * @param timeline an empty timeline
     */
    public void runQuery(Timeline timeline) {
        // Encodes the given term into a url for iTunes
        String searchText = search.getText();
        try {
            String term = URLEncoder.encode(searchText, "UTF-8");
            String sURL = "https://itunes.apple.com/search?term=" + term
                + "&limit=200&media=music";
            parseImages(sURL, timeline);
        } catch (UnsupportedEncodingException uee) {
            System.err.println(uee.getMessage());
        } // try
    } // runQuery


    /**
     * Downloads the images from the URL and updates the image views with them.
     * @param sURL url for the iTunes search of a term
     * @param timeline an empty timeline
     */
    public void parseImages(String sURL, Timeline timeline) {
        stringList = new ArrayList<String>();
        // Sets up an alert for errors during the process
        Platform.runLater(() -> {
            exception = new Alert(Alert.AlertType.WARNING);
            exception.setTitle("Error");
            exception.setHeaderText("There is an error with the search.");
            exception.setContentText("Please try a different term.");
            exception.setResizable(true);
        }); // runLater
        try {
            // Reads and pulls the image URLs from the search URL
            URL url = new URL(sURL);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement je = JsonParser.parseReader(reader);
            JsonObject root = je.getAsJsonObject(); // root of response
            JsonArray results = root.getAsJsonArray("results"); // "results" array
            int numResults = results.size(); // "results" array size
            // Gives an alert if less than 21 search results show up for the term
            if (numResults < 21) {
                Platform.runLater(() -> {
                    Alert not21 = new Alert(Alert.AlertType.WARNING);
                    not21.setTitle("Not Enough Results");
                    not21.setHeaderText("This search term does not have enough results.");
                    not21.setContentText("Please enter a different term.");
                    not21.setResizable(true);
                    not21.showAndWait();
                    timeline.play();
                }); //runLater
            } // if
            // Makes a new list of the json objects and removes duplicates
            stringList.clear();
            for (int z = 0; z < stringList.size(); z++) {
                System.out.println(results.get(z).getAsJsonObject());
            } // for
            for (int k = 0; k < numResults; k++) {
                JsonObject result = results.get(k).getAsJsonObject();
                if (!result.isJsonNull()) {
                    JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member
                    String newImageUrl = artworkUrl100.getAsString();
                    if (!stringList.contains(newImageUrl)) {
                        stringList.add(newImageUrl);
                    } // if
                } // if
            } // for
            // Puts the first 20 search results into the imageviews, updating the bar as it goes
            for (int j = 0; j < 20; j++) {
                Image newImage = new Image(stringList.get(j));
                int index = j;
                Platform.runLater(() -> ivArray[index].setImage(newImage));
                double progressNum = 1.0 * (j + 1) / 20;
                Platform.runLater(() -> setProgress(progressNum));
            } // for
        } catch (MalformedURLException mue) {
            exception.showAndWait();
        } catch (IOException ioe) {
            exception.showAndWait();
        } // try
    } // parseImages


    /**
     * Swaps images every 2 seconds.
     * @param stringList list of all the urls
     */
    public void swapImages(ArrayList<String> stringList) {
        // Changes the images in a random imageview every 2 seconds with math.random
        String rUrl = stringList.get((int)Math.floor(Math.random() * (stringList.size())));
        Image newImage = new Image(rUrl);
        ivArray[(int)Math.floor(Math.random() * (20))].setImage(newImage);
    } // swapImages

    /**
     * Sets the progress bar to the specified progress amount.
     * @param progressNum amount of progress
     */
    private void setProgress(final double progressNum) {
        // Sets the progress to the progress bar while on a daemon thread
        Platform.runLater(() -> progress.setProgress(progressNum));
    } // setProgress


    /**
     * Pauses and plays the timeline when needed.
     * @param timeline playing the images
     * @param play button to play and pause
     */
    public void updateTimeline(Timeline timeline, Button play) {
        // Swaps play and pause modes depending on what it is currently on
        if (timeline.getStatus() == Status.PAUSED) {
            timeline.play();
            Platform.runLater(() -> play.setText("Pause"));
        } else if (timeline.getStatus() == Status.RUNNING) {
            timeline.pause();
            Platform.runLater(() -> play.setText("Play"));
        } // if
    } // updateTimeline


    /**
     * Pops up an about the author alert message.
     */
    public void aboutAuthor() {
        // Creates and formats the alert
        Alert authorAlert = new Alert(Alert.AlertType.INFORMATION);
        authorAlert.setTitle("About Gavin Gaude");
        authorAlert.setHeaderText("About Gavin Gaude");
        Label label = new Label("Name: Gavin Gaude    Email: gwg08270@uga.edu    Version: 1.0");
        label.setWrapText(true);
        authorAlert.getDialogPane().setContent(label);
        authorAlert.setResizable(true);
        // Creates the image and puts it in the imageview
        Image gavin;
        gavin = new Image("https://pkimgcdn.peekyou.com/bd62352c50b03594142c7be466be3c1f.jpeg");
        ImageView pictureFrame = new ImageView(gavin);
        authorAlert.setGraphic(pictureFrame);
        authorAlert.showAndWait();
    } // aboutAuthor


    /**
     * Makes a new thread and runs it. Credit to the Intro to Java Threads reading.
     * @param target a runnable object
     */
    public static void runNow(Runnable target) {
        // Creates a daemon thread from target and runs it
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } // runNow


    /**
     * Initializes the 20 image view variables.
     */
    public void declareImages() {
        // Ititializes each of the 20 imageviews
        ImageView iv1 = new ImageView();
        ImageView iv2 = new ImageView();
        ImageView iv3 = new ImageView();
        ImageView iv4 = new ImageView();
        ImageView iv5 = new ImageView();
        ImageView iv6 = new ImageView();
        ImageView iv7 = new ImageView();
        ImageView iv8 = new ImageView();
        ImageView iv9 = new ImageView();
        ImageView iv10 = new ImageView();
        ImageView iv11 = new ImageView();
        ImageView iv12 = new ImageView();
        ImageView iv13 = new ImageView();
        ImageView iv14 = new ImageView();
        ImageView iv15 = new ImageView();
        ImageView iv16 = new ImageView();
        ImageView iv17 = new ImageView();
        ImageView iv18 = new ImageView();
        ImageView iv19 = new ImageView();
        ImageView iv20 = new ImageView();
        // sets the height of the 20 image views to 130 pixels
        iv1.setFitHeight(130);
        iv2.setFitHeight(130);
        iv3.setFitHeight(130);
        iv4.setFitHeight(130);
        iv5.setFitHeight(130);
        iv6.setFitHeight(130);
        iv7.setFitHeight(130);
        iv8.setFitHeight(130);
        iv9.setFitHeight(130);
        iv10.setFitHeight(130);
        iv11.setFitHeight(130);
        iv12.setFitHeight(130);
        iv13.setFitHeight(130);
        iv14.setFitHeight(130);
        iv15.setFitHeight(130);
        iv16.setFitHeight(130);
        iv17.setFitHeight(130);
        iv18.setFitHeight(130);
        iv19.setFitHeight(130);
        iv20.setFitHeight(130);
        // Makes an imageview array and adds them to HBoxes for the scene graph
        ivArray = new ImageView[]{iv1, iv2, iv3, iv4, iv5,iv6, iv7, iv8, iv9, iv10,
                                  iv11, iv12, iv13, iv14, iv15, iv16, iv17, iv18, iv19, iv20};
        ivc1.getChildren().addAll(iv1, iv2, iv3, iv4, iv5);
        ivc2.getChildren().addAll(iv6, iv7, iv8, iv9, iv10);
        ivc3.getChildren().addAll(iv11, iv12, iv13, iv14, iv15);
        ivc4.getChildren().addAll(iv16, iv17, iv18, iv19, iv20);
    } // declareImages

} // GalleryApp
