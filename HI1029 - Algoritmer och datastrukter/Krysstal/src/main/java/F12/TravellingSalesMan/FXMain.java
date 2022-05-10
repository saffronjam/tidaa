
package F12.TravellingSalesMan;

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

/**
 *
 * @author bfelt
 */
public class FXMain extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        int startSize=10;
        AdjacencyMatrix matrix = new AdjacencyMatrix(startSize);
        BorderPane root = new BorderPane();
        MyCanvas canvas = new MyCanvas(800,400,matrix);//1000,600
        root.setCenter(canvas);
        final Label nrNodesCaption = new Label("Antal städer: ");
        Slider nrNodes = new Slider(3,10000,startSize);
        nrNodes.setBlockIncrement(1);
        //nrNodes.setMin(1);
        //nrNodes.setMax(30);
        //nrNodes.setValue(startSize);
        final Label nrNodesValue = new Label(Integer.toString((int)nrNodes.getValue()));
        final Label distanceCaption = new Label("Distance: ");
        final Label distance = new Label("-    ");

        nrNodes.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> ov, Number oldV, Number newV){
                nrNodesValue.setText(Integer.toString(newV.intValue()));
                matrix.randomize(newV.intValue());
                canvas.draw();
            }
        });

        Button greedyButton = new Button();
        greedyButton.setText("Greedy");
        greedyButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                int w[][] = matrix.getwMatrix();
                int[] route = new int[w.length];
                int d= TravelingSalesMan.minDistanceGreedy(w,route);
                distance.setText(Integer.toString(d));
                canvas.drawRoute(matrix.getRoute(route));
            }
        });


        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(greedyButton);
        toolBar.getItems().add(nrNodesCaption);
        toolBar.getItems().add(nrNodes);
        toolBar.getItems().add(nrNodesValue);
        toolBar.getItems().add(distanceCaption);
        toolBar.getItems().add(distance);
        root.setTop(toolBar);

        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Travelling salesman");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    
}
