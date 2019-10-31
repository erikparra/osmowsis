package com.osmowsis;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {
    private static final int GRIDSIZE = 100;
    private static final float SIMULATION_SPEED = 0.2f;
    private static Simulation sim;
    private Stage mainWindow;
    private Timeline simulationTimeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainWindow) throws Exception {
        this.mainWindow = mainWindow;
        Stage fileChooserWindow = new Stage();
        fileChooserWindow.setTitle("OsMowSis");

        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Choose File...");

        // When the button is pressed, use the file chooser to have users select test file.
        // On success, the main window is opened. On failure an error message is displayed.
        openButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(fileChooserWindow);
            fileChooserWindow.close();
            initializeUI(file.getAbsolutePath());
        });

        // Display the file chooser window
        GridPane layout = new GridPane();
        layout.add(new Text("Select a scenario file..."), 0, 0);
        layout.add(openButton, 0, 1);
        fileChooserWindow.setScene(new Scene(layout, 300, 200));
        fileChooserWindow.show();
    }

    private void initializeUI(String filePath){
        try{
            this.mainWindow.setTitle("OsMowSis");
            sim = new Simulation();
            sim.loadStartingFile(filePath);
            updateUI(sim.getLawnState(), sim.getMowers(), Collections.EMPTY_LIST);
        } catch (Exception e) {
            e.printStackTrace();
            GridPane layout = new GridPane();
            layout.add(new Text("Error: The selected file is not formatted properly! Please close the window and try again."), 0, 0);
            this.mainWindow.setScene(new Scene(layout));
        }
        this.mainWindow.show();
    }

    private void updateUI(LawnSquare[][] state, List<LawnMower> mowers, List<ActionReport> actionReports) {
        GridPane layout = new GridPane();

        int width = state.length;
        int height = state[0].length;

        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                LawnState type = state[col][row].getState();

                // add the appropriate image to the lawn square
                layout.add(buildTileView(type), col, (height-1)-row);

                // if there is a mower on the square, add mower data
                if(type == LawnState.mower || type == LawnState.energymower) {
                    LawnMower mowRef = null;
                    for (LawnMower m : mowers) {
                        Point mowerLoc = sim.findMower(m);
                        if (mowerLoc != null && mowerLoc.x == col && mowerLoc.y == row)
                            mowRef = m;
                    }
                    layout.add(buildMowerText(mowRef), col, (height - 1) - row);
                }
            }
        }

        // todo is this needed?
//        for (LawnMower m : mowers) {
//            if(m.getState() == MowerState.crashed){
//                layout.add(buildTileView(LawnState.mower), m.getCurrentLocation().x, (height-1)- m.getCurrentLocation().y);
//                layout.add(buildMowerText(m), m.getCurrentLocation().x, (height - 1) - m.getCurrentLocation().y);
//            }
//        }

        // create the three buttons
        Button forwardButton = new Button("Next");
        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");

//        // make em BIG buttons
        forwardButton.setMinSize(GRIDSIZE, GRIDSIZE);
        playButton.setMinSize(GRIDSIZE, GRIDSIZE);
        stopButton.setMinSize(GRIDSIZE, GRIDSIZE);

        // setup event handlers for each button
        forwardButton.setOnAction(e -> takeSingleStep());
        playButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> stopSimulation());

        // add compass and buttons to the bottom of the window
        ImageView compassView = new ImageView(new Image(getClass().getResourceAsStream("/compass.bmp")));
        compassView.setFitHeight(GRIDSIZE);
        compassView.setFitWidth(GRIDSIZE);
        layout.add(compassView, 0, height);
        layout.add(forwardButton, 1, height);
        layout.add(playButton, 2, height);
        layout.add(stopButton, 3, height);

        // add the status text field on the right side of the window
        String statusText = ("Turn Count: " + sim.getNumOfTurns()) + "\n";
        statusText += String.join("\n",
                actionReports.stream().map(ActionReport::toString).collect(Collectors.toList()));
        Text statusTextCell = new Text(statusText);
        GridPane.setValignment(statusTextCell, VPos.TOP);
        layout.add(statusTextCell, width, 0, 2, 3);

        Scene scene = new Scene(layout, (width*GRIDSIZE) + 150, (height+1)*GRIDSIZE);
        mainWindow.setScene(scene);
    }

    private Text buildMowerText(LawnMower mowRef){
        String mowerText = String.format("ID: %d\nDirection: %s\nEnergy: %d\nStatus: %s",
                mowRef.getId(), mowRef.getDirection().toSimpleString(), mowRef.getEnergy(), mowRef.getState().toString());
        if(mowRef.getStalledCount() > 0)
            mowerText += "\nStalled Turns: " + mowRef.getStalledCount();
        Text mowerInfo = new Text(mowerText);
        GridPane.setValignment(mowerInfo, VPos.TOP);
        return mowerInfo;
    }

    private ImageView buildTileView(LawnState type){
        try {
            Image img;
            if (type == LawnState.grass)
                img = new Image(getClass().getResourceAsStream("/grass.bmp"));
            else if (type == LawnState.crater)
                img = new Image(getClass().getResourceAsStream("/crater.bmp"));
            else if (type == LawnState.mower)
                img = new Image(getClass().getResourceAsStream("/mower.bmp"));
            else if (type == LawnState.empty)
                img = new Image(getClass().getResourceAsStream("/empty.bmp"));
            else if (type == LawnState.energy)
                img = new Image(getClass().getResourceAsStream("/charger.bmp"));
            else if (type == LawnState.energymower)
                img = new Image(getClass().getResourceAsStream("/chargerMower.bmp"));
            else
                throw new IllegalArgumentException("");

            ImageView resizedMowImg = new ImageView(img);
            resizedMowImg.setFitHeight(GRIDSIZE);
            resizedMowImg.setFitWidth(GRIDSIZE);
            return resizedMowImg;
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("");
        }
    }

    private void takeSingleStep(){
        if(sim.hasTurn()) {
            List<ActionReport> a = sim.takeTurn();
            updateUI(sim.getLawnState(), sim.getMowers(), a);
        } else {
            stopSimulation();
            endingMessage();
        }
    }

    private void startSimulation(){
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(SIMULATION_SPEED), e -> takeSingleStep()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
    }

    private void stopSimulation(){
        if (simulationTimeline != null)
            simulationTimeline.stop();
    }

    private void endingMessage(){
        GridPane layout = new GridPane();
        layout.add(new Text(sim.getPrettyResults()), 0, 0);
        Scene scene = new Scene(layout, 300, 200);
        mainWindow.setScene(scene);
    }
}