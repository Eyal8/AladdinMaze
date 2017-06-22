package Model;

import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import View.MazeDisplayer;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import test.RunCommunicateWithServers;

import java.io.*;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eyal8_000 on 16/06/2017.
 */
public class MyModel extends Observable implements IModel {

    private Maze maze;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private int characterPositionRow;
    private int characterPositionColumn;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    public  MyModel()
    {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());    }

    public void startServers() {

        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public int[][] getBoard() {
        return maze.getMaze();
    }

    public void generateMaze(int rows, int columns){
      //  solve_button.setDisable(false);
        threadPool.execute(() -> {
            IMazeGenerator mazeGenerator = new MyMazeGenerator();
            maze = mazeGenerator.generate(rows,columns);
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            setChanged();
            notifyObservers();
        } );


    }


    public void KeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.UP) {
            if(maze.getMaze()[characterPositionRow - 1][characterPositionColumn] == 0)
                characterPositionRow--;
        }
        if (keyCode == KeyCode.DOWN) {
            if(maze.getMaze()[characterPositionRow + 1][characterPositionColumn] == 0)
                characterPositionRow++;
        }
        if (keyCode == KeyCode.RIGHT) {
            if(maze.getMaze()[characterPositionRow][characterPositionColumn + 1] == 0)
                characterPositionColumn++;
        }
        if (keyCode == KeyCode.LEFT) {
            if(maze.getMaze()[characterPositionRow][characterPositionColumn - 1] == 0)
                characterPositionColumn--;
        }
        setChanged();
        notifyObservers();
    }



    //endregion

/*
    public void load()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("load maze");
        fc.setInitialDirectory(new File("./savedMazes"));
        File chosen = fc.showOpenDialog(null);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./savedMazes/" + chosen.getName());
            ObjectInputStream is = new ObjectInputStream(fis);
            mazeDisplayer = (MazeDisplayer) is.readObject();
            generateMaze();
            //mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
            is.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            showAlert("FileNotFoundException");

        }
        catch (IOException e)
        {
            System.out.println("IOException");
            showAlert("IOException");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("ClassNotFoundException");
            showAlert("ClassNotFoundException");

        }
    }

    public void save()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Saving the maze");
        fc.setInitialDirectory(new File("./savedMazes"));
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        File chosen = fc.showSaveDialog(null);
        try {
            File file = new File("./savedMazes/" + chosen.getName());
            FileOutputStream fo = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(mazeDisplayer);
            fo.close();
            os.close();
        } catch (IOException ex) {
            System.out.println("IOException");
        }
    }

    public void exit(){
        Platform.exit();
    }

    public void aboutTheProgrammers(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Who are we?");
        alert.setHeaderText("information about us");
        alert.setContentText("Our names are Eyal Arviv and Shani Houri and we are totaly awesome!");

        alert.showAndWait();
    }*/
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


}
