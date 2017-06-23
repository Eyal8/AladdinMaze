package View;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Shani on 18/06/2017.
 */
public class View implements Observer, IView {

    private boolean solve = false;
    @FXML
    public javafx.scene.control.TextField txtfld_rowsNum;
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Button solve_button;
    public javafx.scene.control.Button hint_button;
    public  javafx.scene.control.Label Char_row;
    public  javafx.scene.control.Label Char_column;
    public  javafx.scene.control.MenuItem newFile;
    public BorderPane board;
    MediaPlayer mediaPlayer;
    MediaPlayer sol;
    //public void setViewModel
    public void generateMaze() {
        mazeDisplayer.zeroHint();
        mediaPlayer.setVolume(0.1);
        mediaPlayer.play();
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());

        newFile.setDisable(true);
        if(rows<10 || columns<10)
        {
            showAlert("Too small dimensions - generate default 10X10 maze...");
            viewModel.generateMaze(10, 10);
        }
        else
            viewModel.generateMaze(rows, columns);
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
        mazeDisplayer.requestFocus();
    }
    public void KeyPressed(KeyEvent keyEvent) {
        solve = false;
        mazeDisplayer.zeroHint();
        viewModel.KeyPressed(keyEvent.getCode());
        keyEvent.consume();
    }

    public void solveMaze(ActionEvent actionEvent) {
        mazeDisplayer.zeroHint();
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());
        if(!solve) {
            showAlert("Solving maze..");
            solve = true;
        }
        else {
            showAlert("Hiding solution..");
            solve = false;
        }
        viewModel.solveMaze(rows, columns);
        actionEvent.consume();
       // mediaPlayer.pause();
     //   sol.play();
    }

    public void getHint()
    {
        mazeDisplayer.getHint();
        displayMaze(viewModel.getBoard());
    }
    @Override
    public void update(Observable o, Object arg) {
        if(o == viewModel)
        {
           // mazeDisplayer.setMaze(viewModel.getBoard());
           // Char_row.setText(viewModel.getCharacterRow());
          //  Char_column.setText(viewModel.getCharacterColumn());
            if(solve) {
                mazeDisplayer.setSolve(true);
                displayMaze(viewModel.getBoard());
            }
            else
            {
                mazeDisplayer.setSolve(false);
                displayMaze(viewModel.getBoard());
            }
            newFile.setDisable(false);
            solve_button.setDisable(false);
            hint_button.setDisable(false);
            mazeDisplayer.requestFocus();
           // mazeDisplayer.setSolve(false);
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setGoalPosition(viewModel.getGoalPosition());
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
       // CharacterRow.set(characterPositionRow + "");
      //  CharacterColumn.set(characterPositionColumn + "");
    }

    public void setRows(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            if (rows < 10) {
                showAlert("The maze is too small! what are you child?");
                viewModel.generateMaze(10, 10);
            }
            else {
                txtfld_columnsNum.requestFocus();
            }
            keyEvent.consume();
        }
    }

    public void setCols(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
            if (columns < 10) {
                showAlert("The maze is too small! what are you child?");
                viewModel.generateMaze(10, 10);
            }
            else {
                generateMaze();
                mazeDisplayer.requestFocus();
            }
        }
        keyEvent.consume();
    }
    public void zooming(ScrollEvent se)
    {
        if(se.isControlDown() && se.getDeltaY() < 0)
        {
            mazeDisplayer.setHeight(mazeDisplayer.getHeight() + se.getDeltaY());
            mazeDisplayer.setWidth(mazeDisplayer.getWidth() + se.getDeltaY());
            mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
        }
        else if(se.isControlDown() && se.getDeltaY() > 0)
        {
            mazeDisplayer.setHeight(mazeDisplayer.getHeight() + se.getDeltaY());
            mazeDisplayer.setWidth(mazeDisplayer.getWidth() + se.getDeltaY());
            mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
        }
        se.consume();
    }

    public void load()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load maze...");
        fc.setInitialDirectory(new File("./savedMazes"));
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files",".txt"));
        File chosen = fc.showOpenDialog((Stage) mazeDisplayer.getScene().getWindow());
        if(chosen != null)
            viewModel.load(chosen);
    }

    public void save()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Saving the maze");
        fc.setInitialDirectory(new File("./savedMazes"));
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", ".txt"));
        File chosen = fc.showSaveDialog((Stage) mazeDisplayer.getScene().getWindow());
        if(chosen != null)
            viewModel.save(chosen);
    }

    public void exit(){
        viewModel.exit();
        Platform.exit();
        mediaPlayer.stop();
    }

    public void aboutTheProgrammers(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Who are we?");
        alert.setHeaderText("information about us");
        alert.setContentText("Our names are Eyal Arviv and Shani Houri and we are totaly awesome!");

        alert.showAndWait();
    }
    public void aboutTheAlgorithms(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Algorithms");
        alert.setHeaderText("Information about the algorithms used in this game");
        alert.setContentText("In this game we used a few algorithms:\n" +
                "first, the algorithm to generate the maze the DFS (Depth First Search) algorithm.\n" +
                "second, the algorithm to solve the maze the Best first search algorithm.");
        // alert.setContentText("first, the algorithm to generate the maze the DFS (Depth First Search) algorithm.");
        // alert.setContentText("second, the algorithm to solve the maze the Best first search algorithm");

        alert.showAndWait();
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                board.setPrefWidth(newSceneWidth.longValue()*0.8);
                mazeDisplayer.setWidth(board.getPrefWidth());
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                board.setPrefHeight(newSceneHeight.longValue()*0.9);
                mazeDisplayer.setHeight(board.getPrefHeight());
                mazeDisplayer.redraw();
            }
        });
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        Char_column.textProperty().bind(viewModel.characterColumnProperty());
        Char_row.textProperty().bind(viewModel.characterRowProperty());
        Media song = new Media("file:///C:/Users/Shani/IdeaProjects/Project_PartC/resources/music/aladdin-friendlikemehighquality_cutted.mp3");
        mediaPlayer = new MediaPlayer(song);
       // Media solveSong = new Media("file:///C:/Users/Shani/IdeaProjects/Project_PartC/resources/music/file:///C:/Users/Shani/IdeaProjects/Project_PartC/resources/music/aladdin-awholenewworldhighquality_cutted.mp3");
      //  sol = new MediaPlayer(solveSong);
      //  mazePane.getChildren().add(mazeDisplayer);
       // mazeDisplayer.heightProperty().bind(mazePane.heightProperty());
       // mazeDisplayer.widthProperty().bind(mazePane.widthProperty());
    }
}
