package Model;

import Client.*;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import View.MazeDisplayer;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
    private  ArrayList<Position> path = new ArrayList<Position>();
    public  MyModel()
    {
        maze = new Maze(new int[0][0]);
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

    public ArrayList<Position> getPath()
    {
        return path;
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


        threadPool.submit(() -> {
            generatingMazeClient(rows, columns);
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            setChanged();
            notifyObservers();
        } );
       /* threadPool.execute(() -> {
            generatingMazeClient(rows, columns);
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            setChanged();
            notifyObservers();
        } );*/


    }

    private void generatingMazeClient (int rows, int columns){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, columns};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(rows * columns) + 6];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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

    public void solveMaze(int rows, int columns){
        threadPool.submit(() -> {
            solvingMazeClient(rows, columns);
            setChanged();
            notifyObservers();
        } );
    }

    private void solvingMazeClient(int rows, int columns)
    {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                      //  MyMazeGenerator mg = new MyMazeGenerator();
                       // Maze maze = mg.generate(rows, columns);
                        maze.setStart(new Position(characterPositionRow, characterPositionColumn));
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject();
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        path = new ArrayList<Position>();
                        for(int i = 0; i < mazeSolutionSteps.size() - 1; ++i) {
                            AState astate = mazeSolutionSteps.get(i);
                            int comma = astate.getState().indexOf(44);
                            int row = Integer.parseInt(astate.getState().substring(1, comma));
                            int col = Integer.parseInt(astate.getState().substring(comma + 1, astate.getState().length() - 1));
                            path.add(new Position(row, col));
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
    }
    public void load(File chosen)
    {
        byte mazeBytes[] = new byte[0];
        try {
            FileInputStream fis = new FileInputStream("./savedMazes/" + chosen.getName());
            FileInputStream tmp = new FileInputStream("./savedMazes/" + chosen.getName());
            InputStream is = new MyDecompressorInputStream(fis);
            mazeBytes = new byte[tmp.read()*tmp.read()+6];
            is.read(mazeBytes);
            is.close();
            fis.close();
            tmp.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        }
        catch (IOException e)
        {
            System.out.println("IOException");
        }
        maze  = new Maze(mazeBytes);
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
    }

    public void save(File chosen)
    {
        try {
            FileOutputStream file = new FileOutputStream(chosen);
            OutputStream os = new MyCompressorOutputStream(file);
            maze.setStart(new Position(characterPositionRow, characterPositionColumn));
            os.write(maze.toByteArray());
            os.flush();
            os.close();
        } catch (IOException ex) {
            System.out.println("IOException");
        }
    }

    public void exit(){
        stopServers();
        threadPool.shutdown();
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

    public Position getGoalPosition(){
        return maze.getGoalPosition();
    }


}
