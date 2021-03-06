package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by eyal8_000 on 16/06/2017.
 */
public class MazeDisplayer extends Canvas implements Serializable {

    private ArrayList<Position> path;
    Position goalPosition;
    private int[][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private boolean solve = false;
    private int hint = 0;

    public MazeDisplayer()
    {
        widthProperty().addListener(evt -> redraw());
        heightProperty().addListener(evt -> redraw());
    }
    public boolean isSolve() {
        return solve;
    }

    public void setSolve(boolean solve) {
        this.solve = solve;
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
      //  redraw();
    }

    public void setPath(ArrayList<Position> path)
    {
        this.path = path;
    }

    public void getHint()
    {
        hint++;
    }

    public void zeroHint()
    {
        hint = 0;
    }

    public Position getGoalPosition() {
        return goalPosition;
    }

    public void setGoalPosition(Position goalPosition) {
        this.goalPosition = goalPosition;
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public int[][] getMaze() {
        return maze;
    }

    public void setCharacterPositionRow(int characterPositionRow) {
        this.characterPositionRow = characterPositionRow;
    }

    public void setCharacterPositionColumn(int characterPositionColumn) {
        this.characterPositionColumn = characterPositionColumn;
    }

    public StringProperty imageFileNameWallProperty() {
        return ImageFileNameWall;
    }

    public StringProperty imageFileNameCharacterProperty() {
        return ImageFileNameCharacter;
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public double minWidth(double height) {
        return 400;
    }

    @Override
    public double minHeight(double width) {
        return 400;
    }

    @Override
    public double maxWidth(double height) {
        return 1000;
    }

    @Override
    public double maxHeight(double width) {
        return 1000;
    }

    @Override
    public void resize(double width, double height) {
        super.setHeight(height);
        super.setWidth(width);
        // redraw();
    }
    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;
            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                Image hintImage = new Image(new FileInputStream(ImageFileNameHint.get()));
                Image solveImage = new Image(new FileInputStream(ImageFileNameSolve.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                if(hint > 0 && path != null && !isSolve()) {

                    int x = 0;
                    if (hint + 1 < path.size())
                        x = hint + 1;
                    else
                        x = path.size();
                    for (int i = 1; i < x; i++) {
                        int row = path.get(i).getRowIndex();
                        int col = path.get(i).getColumnIndex();
                        gc.drawImage(hintImage, col * cellWidth, row * cellHeight, cellWidth, cellHeight);

                    }
                }
                else if(isSolve())
                {
                    for(int i = 1; i < path.size(); i++)
                    {
                            int row = path.get(i).getRowIndex();
                            int col = path.get(i).getColumnIndex();
                            gc.drawImage(solveImage, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                        }
                }
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            gc.drawImage(wallImage,  j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                gc.drawImage(goalImage, goalPosition.getColumnIndex() * cellWidth, goalPosition.getRowIndex() * cellHeight, cellWidth, cellHeight);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth,characterPositionRow * cellHeight , cellWidth, cellHeight);

            } catch (FileNotFoundException e) {
            }
        }
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();
    private StringProperty ImageFileNameSolve = new SimpleStringProperty();
    private StringProperty ImageFileNameHint = new SimpleStringProperty();

    //endregion

    public String getImageFileNameSolve() {
        return ImageFileNameSolve.get();
    }

    public void setImageFileNameSolve(String imageFileNameSolve) {
        this.ImageFileNameSolve.set(imageFileNameSolve);
    }

    public String getImageFileNameHint() {
        return ImageFileNameHint.get();
    }

    public void setImageFileNameHint(String imageFileNameHint) {
        this.ImageFileNameHint.set(imageFileNameHint);
    }
    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

}
