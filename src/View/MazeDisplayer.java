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
import java.util.Observable;
import java.util.Observer;

/**
 * Created by eyal8_000 on 16/06/2017.
 */
public class MazeDisplayer extends Canvas implements Serializable {

    Position startPosition;
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

    public void getHint()
    {
        hint++;
    }

    public void zeroHint()
    {
        hint = 0;
    }
    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Position getGoalPosition() {
        return goalPosition;
    }

    public void setGoalPosition(Position goalPosition) {
        this.goalPosition = goalPosition;
    }

    public void setCharacterPosition(int row, int column) {
        if(goalPosition.getRowIndex() == row && goalPosition.getColumnIndex() == column)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Who is you?");
            alert.setHeaderText("We are the champions!!!");
            alert.setContentText("You found the caramel!");

            alert.showAndWait();
        }
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
        return 10;
    }

    @Override
    public double minHeight(double width) {
        return 10;
    }

    @Override
    public double maxWidth(double height) {
        return 800;
    }

    @Override
    public double maxHeight(double width) {
        return 800;
    }

    @Override
    public void resize(double width, double height) {
        super.setHeight(height);
        super.setWidth(width);
        // redraw();
    }
    public void redraw() {
        if (maze != null) {
            double canvasHeight = getWidth();
            double canvasWidth = getHeight();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;
            System.out.println("canvasHeight   " + canvasHeight + "\n");
            System.out.println("canvasWidth   " + canvasWidth+ "\n");
            System.out.println("cellHeight   " + cellHeight+ "\n");
            System.out.println("cellWidth   " + cellWidth+ "\n");
            System.out.println();
            System.out.println();
            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                        }
                    }
                }

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                ISearchingAlgorithm searchingAlgorithm = new BestFirstSearch();
                Maze m = new Maze(maze);
                m.setStart(new Position(characterPositionRow, characterPositionColumn));
                m.setGoal(goalPosition);
                ISearchable searchableMaze = new SearchableMaze(m);
                Solution solution = ((ISearchingAlgorithm)searchingAlgorithm).solve(searchableMaze);
                if(hint > 0)
                {
                    try {
                        Image hintImage = new Image(new FileInputStream(ImageFileNameHint.get()));
                        int x = 0;
                        if(hint + 1 < solution.getSolutionPath().size())
                            x = hint + 1;
                        else
                            x = solution.getSolutionPath().size();
                        for(int i = 1; i < x; i++){
                            AState astate = solution.getSolutionPath().get(i);
                            int comma = astate.getState().indexOf(44);
                            int row = Integer.parseInt(astate.getState().substring(1, comma));
                            int col = Integer.parseInt(astate.getState().substring(comma + 1, astate.getState().length() - 1));
                            gc.drawImage(hintImage, col * cellHeight, row * cellWidth, cellHeight, cellWidth);
                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("FileNotFoundException");
                    }
                }
                else if(isSolve())
                {
                    try {
                        Image solveImage = new Image(new FileInputStream(ImageFileNameSolve.get()));
                        for (AState astate :solution.getSolutionPath()) {
                            int comma = astate.getState().indexOf(44);
                            int row = Integer.parseInt(astate.getState().substring(1, comma));
                            int col = Integer.parseInt(astate.getState().substring(comma + 1, astate.getState().length() - 1));
                            gc.drawImage(solveImage, col * cellHeight, row * cellWidth, cellHeight, cellWidth);

                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("FileNotFoundException");
                    }
                }
                gc.drawImage(goalImage, goalPosition.getColumnIndex() * cellHeight, goalPosition.getRowIndex() * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
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
