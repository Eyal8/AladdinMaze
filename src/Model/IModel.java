package Model;

import algorithms.mazeGenerators.Position;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by eyal8_000 on 16/06/2017.
 */
public interface IModel {
    int[][] getBoard();
    void generateMaze(int rows, int columns);
    void KeyPressed(KeyCode keyCode);
    void solveMaze(int rows, int columns);
    void exit();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    Position getGoalPosition();
    void save(File chosen);
    void load(File chosen);
    ArrayList<Position> getPath();
    void mouse(double cellHeight, double cellWidth, double mouseEndX, double mouseEndY);
    boolean checkWall(double cellHeight, double cellWidth, double x, double y);
    void setxCharPos(double xCharPos);
    void setyCharPos(double yCharPos);
    double getxCharPos();
    double getyCharPos();
}
