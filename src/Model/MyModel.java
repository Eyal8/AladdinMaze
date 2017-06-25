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
import javafx.scene.input.MouseEvent;
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
    private double xCharPos;
    private double yCharPos;
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
            xCharPos = characterPositionColumn;
            yCharPos = characterPositionRow;
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
        if (keyCode == KeyCode.UP || keyCode == KeyCode.W || keyCode == KeyCode.NUMPAD5) {
            if(maze.getMaze()[characterPositionRow - 1][characterPositionColumn] == 0)
                characterPositionRow--;
        }
        if (keyCode == KeyCode.DOWN || keyCode == KeyCode.S || keyCode == KeyCode.NUMPAD2) {
            if(maze.getMaze()[characterPositionRow + 1][characterPositionColumn] == 0)
                characterPositionRow++;
        }
        if (keyCode == KeyCode.RIGHT || keyCode == KeyCode.D || keyCode == KeyCode.NUMPAD3) {
            if(maze.getMaze()[characterPositionRow][characterPositionColumn + 1] == 0)
                characterPositionColumn++;
        }
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.A || keyCode == KeyCode.NUMPAD1) {
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

    public void mouse(double cellHeight, double cellWidth, double mouseEndX, double mouseEndY)
    {
        boolean xEndOk = false;
        int newX = -1;
        boolean yEndOk = false;
        int newY = -1;
        boolean xStartOK = xCharPos >= characterPositionColumn*cellWidth && xCharPos <= characterPositionColumn*cellWidth + cellWidth;
        boolean yStartOK = yCharPos >= characterPositionRow*cellHeight && yCharPos <= characterPositionRow*cellHeight + cellHeight;
        for(int i = 0; i < maze.getMaze()[0].length; i++)
        {
            if(mouseEndX >= i*cellHeight && mouseEndX <= i*cellHeight + cellWidth)
            {
                xEndOk = true;
                newX = i;
                break;
            }
        }
        for(int i = 0; i < maze.getMaze().length; i++)
        {
            if(mouseEndY >= i*cellWidth && mouseEndY <= i*cellWidth + cellHeight)
            {
                yEndOk = true;
                newY = i;
                break;
            }
        }
        if(xStartOK && yStartOK && xEndOk && yEndOk && maze.getMaze()[newX][newY] == 0)
        {
            characterPositionRow = newY;
            characterPositionColumn = newX;
            setChanged();
            notifyObservers();
        }
    }
    public boolean checkWall(double cellHeight, double cellWidth, double x, double y){
        boolean xEndOk = false;
        int newX = -1;
        boolean yEndOk = false;
        int newY = -1;
        boolean xStartOK = xCharPos >= characterPositionColumn*cellWidth && xCharPos <= characterPositionColumn*cellWidth + cellWidth;
        boolean yStartOK = yCharPos >= characterPositionRow*cellHeight && yCharPos <= characterPositionRow*cellHeight + cellHeight;
        for(int i = 0; i < maze.getMaze()[0].length; i++)
        {
            if(y >= i*cellWidth && y <= i*cellWidth + cellWidth)
            {
                xEndOk = true;
                newX = i;
                break;
            }
        }
        for(int i = 0; i < maze.getMaze().length; i++)
        {
            if(x >= i*cellHeight && x <= i*cellHeight + cellHeight)
            {
                yEndOk = true;
                newY = i;
                break;
            }
        }
        if(maze.getMaze()[newX][newY] == 1)
            return false;
        else if(xStartOK && yStartOK && xEndOk && yEndOk) {
            xCharPos = x;
            yCharPos = y;
            characterPositionRow = newX;
            characterPositionColumn = newY;
            setChanged();
            notifyObservers();
            return true;
        }
        return true;
    }
    public void setxCharPos(double xCharPos){
        this.xCharPos = xCharPos;
    }
    public void setyCharPos(double yCharPos){
        this.yCharPos = yCharPos;
    }
    public double getxCharPos(){
        return xCharPos;
    }
    public double getyCharPos(){
        return yCharPos;
    }
    public Position getGoalPosition(){
        return maze.getGoalPosition();
    }
}
