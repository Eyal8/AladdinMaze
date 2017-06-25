package ViewModel;

import IO.MyCompressorOutputStream;
import Model.IModel;
import Model.MyModel;
import View.MazeDisplayer;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    public IModel model;

    @FXML


    //region String Property for Binding
    public StringProperty CharacterRow = new SimpleStringProperty();
    public StringProperty CharacterColumn = new SimpleStringProperty();


    public MyViewModel(MyModel model) {
        this.model = model;
    }

    public int[][] getBoard() {
        return model.getBoard();
    }

    public void generateMaze(int rows, int columns) {
        model.generateMaze(rows, columns);
    }

    public void KeyPressed(KeyCode keyCode) {
        model.KeyPressed(keyCode);
    }

    //endregion

    public void solveMaze(int rows, int columns) {
        model.solveMaze(rows, columns);
    }

    public void load(File chosen) {
        model.load(chosen);
    }

    public void save(File chosen) {
        model.save(chosen);
    }

    public void exit() {
        model.exit();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    CharacterRow.set(String.valueOf(model.getCharacterPositionRow()));
                    CharacterColumn.set(String.valueOf(model.getCharacterPositionColumn()));
                }
            });
            setChanged();
            notifyObservers();
        }
    }

    //region String Property for Binding
    public String getCharacterRow() {
        return CharacterRow.get();
    }

    public StringProperty characterRowProperty() {
        return CharacterRow;
    }

    public String getCharacterColumn() {
        return CharacterColumn.get();
    }

    public StringProperty characterColumnProperty() {
        return CharacterColumn;
    }

    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

    public Position getGoalPosition() {
        return model.getGoalPosition();
    }

    public ArrayList<Position> getPath() {
        return model.getPath();
    }

    public void mouse(double cellHeight, double cellWidth, double mouseEndX, double mouseEndY) {
        model.mouse(cellHeight, cellWidth, mouseEndX, mouseEndY);
    }

    public boolean checkWall(double cellHeight, double cellWidth, double x, double y) {
        return model.checkWall(cellHeight, cellWidth, x, y); }
    public void setxCharPos(double xCharPos){
        model.setxCharPos(xCharPos);
    }
    public void setyCharPos(double yCharPos){
        model.setyCharPos(yCharPos);
    }
    public double getxCharPos(){
        return model.getxCharPos();
    }
    public double getyCharPos(){
        return model.getyCharPos();
    }
}
