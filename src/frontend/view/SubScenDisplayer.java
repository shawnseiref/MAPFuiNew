package frontend.view;

import backEnd.Agents.Agent;
import backEnd.Game.SubScenario;
import backEnd.MapGenerators.Map;
import backEnd.MapGenerators.Position;
import frontend.model.IModel;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SubScenDisplayer extends Canvas {

    private SubScenario game;
    private IModel.Type currentType = IModel.Type.CREATE;
    private int currentState = 0;
    private double cellSize;
    private double agentWidth;
    private boolean start = false;

    public void setCurrentType(IModel.Type currentType) {
        this.currentType = currentType;
    }

    public IModel.Type getCurrentType() {
        return currentType;
    }

    public void setMap(Map map) {
        game = new SubScenario(map);
        start = true;
        redraw();
    }

    public void nextState() {
        if (currentState < game.getSol().getSolLength() - 1)
            currentState++;
    }

    public void previousState() {
        if (currentState != 0)
            currentState--;
    }

    public void redraw() {
        if (game == null)
            getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        else if (game.getMap() != null && game.getMap().getGrid() != null) {
            char[][] grid = game.getMap().getGrid();
            setHeight(cellSize * grid.length);
            setWidth(cellSize * grid[0].length);
            try {
//                Image treeImage = null;
//                Image outOfBounds = null;
//                treeImage = new Image(this.getClass().getResourceAsStream("/Images/tree.png"));
//                outOfBounds = new Image(this.getClass().getResourceAsStream("/Images/void.jpg"));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                //Draw grid
                if (cellSize != 0)
                    drawGrid(gc, cellSize);
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == 'T') {
//                            gc.drawImage(treeImage, j * cellSize, i * cellSize, cellSize, cellSize);
                            gc.setFill(Color.gray(0.15));
                            gc.fillRect( j * cellSize, i * cellSize, cellSize, cellSize);
                        } else if (grid[i][j] == '@') {
//                            gc.drawImage(outOfBounds, j * cellSize, i * cellSize, cellSize, cellSize);
                            gc.setFill(Color.BLACK);
                            gc.fillRect( j * cellSize, i * cellSize, cellSize, cellSize);
                        }
                    }
                }
                if (currentType == IModel.Type.SIMULATE && game.getSol() != null) {
                    drawPath(gc);
                }
                if (game.getAgentsList() != null) {
                    drawAgents(gc);
                    drawTargets(gc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawPath(GraphicsContext gc) {
        agentWidth = cellSize / game.getSol().getAgentsSolutions().size();
        for (int i = 0; i < game.getSol().getAgentsSolutions().size(); i++) {
            ArrayList<Position> path = game.getSol().getAgentsSolutions().get(i).getPath();
            int j = 0;
            Color color = getColor(i);
            for (j = 0; j < currentState; j++) {
                gc.setFill(color);
                gc.fillRect(path.get(j).getX() * cellSize + i * agentWidth, path.get(j).getY() * cellSize, agentWidth, cellSize);
            }
        }
    }

    private void drawTargets(GraphicsContext gc) {
        ArrayList<Agent> agents = game.getAgentsList();
        for (int i = 0; i < agents.size(); i++) {
            drawAgent(agents.get(i).getGoalLocation().getX(), agents.get(i).getGoalLocation().getY(), i, true, gc);
            drawTarget(agents.get(i).getGoalLocation().getX(), agents.get(i).getGoalLocation().getY(), i, gc);
        }
    }

    private void drawTarget(int x, int y, int botNum, GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x * cellSize+0.1*cellSize,
                y * cellSize+0.1*cellSize,
                cellSize*0.8, cellSize*0.8);
            int stringSize = Math.max(10, (int) cellSize / 5);
            gc.setFont(new Font(stringSize));
            gc.setFill(Color.BLACK);
            gc.fillText(botNum + "", x * cellSize + cellSize / 2 - stringSize * 0.45 * ((int) Math.log10(botNum + 1) + 1), y * cellSize + cellSize / 2 + stringSize * 0.45, cellSize);
    }

    private void drawAgents(GraphicsContext gc) {
        ArrayList<Agent> agents = game.getAgentsList();
        for (int t = 0; t < agents.size(); t++) {
            drawAgent(agents.get(t).getLocation().getX(), agents.get(t).getLocation().getY(), t, true, gc);
        }
    }

    private void drawAgent(int x, int y, int botNum, boolean drawNum, GraphicsContext gc) {

        gc.setFill(drawNum? getColor(botNum): Color.RED);
        gc.fillOval(x * cellSize,
                y * cellSize,
                cellSize, cellSize);
        if (drawNum) {
            int stringSize = Math.max(10, (int) cellSize / 5);
            gc.setFont(new Font(stringSize));
            gc.setFill(Color.WHITE);
            gc.fillText(botNum + "", x * cellSize + cellSize / 2 - stringSize * 0.45 * ((int) Math.log10(botNum + 1) + 1), y * cellSize + cellSize / 2 + stringSize * 0.45, cellSize);
        }
    }

    public void drawTempAgent(int x, int y) {
        drawAgent(x, y, 0, false, getGraphicsContext2D());
    }



    private void drawGrid(GraphicsContext gc, double spacing) {
        gc.setLineWidth(1); // change the line width

        final int hLineCount = (int) Math.floor((getHeight() + 1) / spacing);
        final int vLineCount = (int) Math.floor((getWidth() + 1) / spacing);

        gc.setStroke(Color.RED);
        for (int i = 0; i < hLineCount; i++) {
            gc.strokeLine(0, snap((i + 1) * spacing), getWidth(), snap((i + 1) * spacing));
        }

        gc.setStroke(Color.BLUE);
        for (int i = 0; i < vLineCount; i++) {
            gc.strokeLine(snap((i + 1) * spacing), 0, snap((i + 1) * spacing), getHeight());
        }
    }

    private double snap(double y) {
        return ((int) y) + 0.5;
    }


    private Color getColor(int t) {
        int ID = t + 1;
        int r = (ID & 4) >> 2,
                g = (ID & 2) >> 1,
                b = ID & 1,
                h = (ID & 8) >> 3;
        return (Color.rgb(100 * r + h * 80, 140 * g + h * 80, 100 * b + h * 80));
    }


    public void setGame(SubScenario game) {
        this.game = game;
    }

    public int getStateNum() {
        return currentState;
    }

    public void changeSize(double zoomFactor) {
        char[][] grid = game.getMap().getGrid();
        if (cellSize * zoomFactor * grid.length < 8192 && cellSize * zoomFactor * grid[0].length < 8192) {
            cellSize = cellSize * zoomFactor;
            redraw();
        }
    }

    public double getSize() {
        if (game == null)
            return 0;
        return cellSize;
    }

    public void newMap() {
        double size = computeSize();
        cellSize = size;
        redraw();
    }


    private double computeSize() {
        char[][] grid = game.getMap().getGrid();
        double size = Math.max(100, Math.min(getParent().getParent().getBoundsInParent().getWidth() / grid.length, getParent().getParent().getBoundsInParent().getHeight() / grid[0].length));
        size = Math.min(8192 / Math.max(grid.length, grid[0].length), size);
        return size;
    }
}
