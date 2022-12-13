
package F13.SkylineSolver;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author bfelt
 */
public class FXMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        int startSize = 10;
        BorderPane root = new BorderPane();
        MyCanvas canvas = new MyCanvas(800, 400);//1000,600
        root.setCenter(canvas);
        final Label nrNodesCaption = new Label("Antal byggnader: ");
        Slider nrNodes = new Slider(3, 100, startSize);
        nrNodes.setBlockIncrement(1);
        final Label nrNodesValue = new Label(Integer.toString((int) nrNodes.getValue()));

        nrNodes.valueProperty().addListener((ov, oldV, newV) -> {
            nrNodesValue.setText(Integer.toString(newV.intValue()));
            canvas.generateBuildings(newV.intValue());
            canvas.draw();
            canvas.drawBuildings();
        });

        Button greedyButton = new Button();
        greedyButton.setText("Solve");
        greedyButton.setOnAction(event -> {
            var skyline = SkylineSolverFX.solve(canvas.getBuildings());
            canvas.drawBuildings();
            canvas.drawSkyline(skyline);
        });


        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(greedyButton);
        toolBar.getItems().add(nrNodesCaption);
        toolBar.getItems().add(nrNodes);
        toolBar.getItems().add(nrNodesValue);
        root.setTop(toolBar);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Skyline");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
