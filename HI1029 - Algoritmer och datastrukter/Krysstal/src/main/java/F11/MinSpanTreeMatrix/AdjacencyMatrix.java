/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F11.MinSpanTreeMatrix;


import java.util.Arrays;


/**
 *
 * @author bfelt
 */
public class AdjacencyMatrix {

    private int[][] wMatrix;
    private static final int XSCALE = 1000, YSCALE = 600;
    private Point[] positions;
    private int size;

    public int getSize() {
        return size;
    }
    public int[][] getwMatrix() {
        return wMatrix;
    }

    public static int getXSCALE() {
        return XSCALE;
    }

    public static int getYSCALE() {
        return YSCALE;
    }

    public AdjacencyMatrix(int size) {
        this.size=size;
        randomize(size);
    }

    public void randomize(int size) {
        this.size=size;
        positions = new Point[size];
        generatePositions(positions, XSCALE, YSCALE);
        generateMatrix(positions);
    }

    public Edge[] getTree(int[] connections){
        Edge[] tree = new Edge[connections.length-1];
        for(int i=1;i<connections.length;i++){
            tree[i-1] = new Edge(positions[i],positions[connections[i]]);
        }
        return tree;
    }

    public Point[] getPositions() {
        return positions;
    }

    private void generatePositions(Point[] positions, int xScale, int yScale) {
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Point((int) (Math.random() * xScale), (int) (Math.random() * yScale));
        }
    }

    private void generateMatrix(Point[] positions) {
        wMatrix = new int[positions.length][positions.length];
        for (int i = 0; i < wMatrix.length; i++) {
            for (int j = 0; j < wMatrix[0].length; j++) {
                wMatrix[i][j] = positions[i].distance(positions[j]);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] wRow : wMatrix) {
            sb.append(Arrays.toString(wRow));
            sb.append("\n");
        }
        //return Arrays.deepToString(wMatrix);
        return sb.toString();
    }

}
