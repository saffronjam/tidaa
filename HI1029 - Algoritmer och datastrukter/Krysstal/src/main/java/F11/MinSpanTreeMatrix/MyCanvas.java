/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F11.MinSpanTreeMatrix;

//import java.awt.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author bfelt
 */
public class MyCanvas extends Canvas{

    private GraphicsContext gc;
    private AdjacencyMatrix matrix;
    private static final int NODESIZE=5;//ska va udda?
    
    public MyCanvas(double width, double height, AdjacencyMatrix matrix) {
        super(matrix.getXSCALE(),matrix.getYSCALE());
        
        this.matrix=matrix;
        gc = this.getGraphicsContext2D();
        draw();
    }
    
    public void draw(){
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
        gc.setFill(Color.WHEAT);
        Point[] positions = matrix.getPositions();
        for(Point p:positions){
            gc.fillOval(p.x-NODESIZE/2,p.y-NODESIZE/2,NODESIZE,NODESIZE);
        }
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
    }

    public void drawRoute(Edge[] tree){
        draw();
        for(int i=0;i<tree.length;i++){
            gc.strokeLine(tree[i].p1.x,tree[i].p1.y,tree[i].p2.x,tree[i].p2.y);
        }
    }
}
