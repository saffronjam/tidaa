/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F13.SkylineSolver;

//import java.awt.Point;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyCanvas extends Canvas {

    private GraphicsContext gc;
    private static final int NODESIZE = 5;
    private List<Building> buildings;

    private double width, height;

    public MyCanvas(double width, double height) {
        super(width, height);
        this.width = width;
        this.height = height;

        gc = this.getGraphicsContext2D();
        generateBuildings(10);
        draw();
        drawBuildings();
    }

    public void generateBuildings(int count) {
        var random = new Random();
        buildings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int x1 = random.nextInt((int) this.width);
            int height = random.nextInt((int) (this.height * 0.9));
            int x2 = (int) Math.min(x1 + 25 + random.nextInt(300), width - 1);
            buildings.add(new Building(x1, x2, height));
        }
    }

    public void draw() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
        gc.setFill(Color.WHEAT);
        gc.setStroke(Color.RED);
    }

    public void drawSkyline(List<Point> skyline) {
        gc.setStroke(Color.PAPAYAWHIP);

        for (var p : skyline) {
            p.y = (int) (height - p.y);
        }

        gc.setLineWidth(10.0f);
        var pStart = skyline.get(0);
        gc.strokeLine(pStart.x, height, pStart.x, pStart.y);
        for (int i = 0; i < skyline.size() - 1; i++) {
            var p1 = skyline.get(i);
            var p2 = skyline.get(i + 1);
            gc.strokeLine(p1.x, p1.y, p2.x, p1.y);
            gc.strokeLine(p2.x, p1.y, p2.x, p2.y);
        }
    }

    public void drawBuildings() {
        gc.setLineWidth(2);
        int i = 0;
        for (var b : buildings) {
            setColor(++i % 10);
            var h = height - b.height;
            gc.strokeLine(b.x1, height, b.x1, h);
            gc.strokeLine(b.x1, h, b.x2, h);
            gc.strokeLine(b.x2, h, b.x2, height);
        }
    }

    private void setColor(int r) {
        switch (r) {
            case 0 -> gc.setStroke(Color.RED);
            case 1 -> gc.setStroke(Color.BEIGE);
            case 2 -> gc.setStroke(Color.GREEN);
            case 3 -> gc.setStroke(Color.YELLOW);
            case 4 -> gc.setStroke(Color.CYAN);
            case 5 -> gc.setStroke(Color.PINK);
            case 6 -> gc.setStroke(Color.PALEGOLDENROD);
            case 7 -> gc.setStroke(Color.DARKGREEN);
            case 8 -> gc.setStroke(Color.BLUE);
            case 9 -> gc.setStroke(Color.PURPLE);
            case 10 -> gc.setStroke(Color.VIOLET);
            default -> gc.setStroke(Color.WHITE);
        }
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
