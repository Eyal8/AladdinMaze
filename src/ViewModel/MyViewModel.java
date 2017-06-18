package ViewModel;

import View.MazeDisplayer;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
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
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Observable;

public class MyViewModel extends Observable {

    @FXML
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Button solve_button;
    public  javafx.scene.control.Label Char_row;
    public  javafx.scene.control.Label Char_column;

    //region String Property for Binding
    public StringProperty CharacterRow = new SimpleStringProperty();
    public StringProperty CharacterColumn = new SimpleStringProperty();

    public String getCharacterRow() {
        return (mazeDisplayer!= null) ? mazeDisplayer.getCharacterPositionRow() + "" : "1";
    }

    public String getCharacterColumn() {
        return (mazeDisplayer!= null) ? mazeDisplayer.getCharacterPositionColumn() + "" : "1";
    }

    public int[][] getNewMaze(int height, int width) {
        IMazeGenerator mazeGenerator = new MyMazeGenerator();
        Maze maze = mazeGenerator.generate(height,width);
       // mazeDisplayer.setMaze(maze.getMaze());
        mazeDisplayer.setStartPosition(maze.getStartPosition());
        mazeDisplayer.setGoalPosition(maze.getGoalPosition());
        mazeDisplayer.setCharacterPositionColumn(mazeDisplayer.getStartPosition().getColumnIndex());
        mazeDisplayer.setCharacterPositionRow(mazeDisplayer.getStartPosition().getRowIndex());
        return maze.getMaze();
    }

    public void generateMaze() {
        solve_button.setDisable(false);
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());
        if(rows<10 || columns<10)
        {
            showAlert("The maze is too small! what are you child?");

        }
        this.mazeDisplayer.setMaze(getNewMaze(rows,columns));
        mazeDisplayer.requestFocus();
        //mazeDisplayer.addEventFilter(MouseEvent.MOUSE_CLICKED, (event -> mazeDisplayer.requestFocus()));
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void solveMaze(ActionEvent actionEvent) {

        showAlert("Solving maze..");
        mazeDisplayer.setSolve(true);
        mazeDisplayer.redraw();
        mazeDisplayer.setSolve(false);

    }

    public void KeyPressed(KeyEvent keyEvent) {

        int characterRow = mazeDisplayer.getCharacterPositionRow();
        int characterColumn = mazeDisplayer.getCharacterPositionColumn();
        if (keyEvent.getCode() == KeyCode.UP) {
            if(mazeDisplayer.getMaze()[characterRow - 1][characterColumn] == 0)
                mazeDisplayer.setCharacterPosition(characterRow - 1, characterColumn);
          //  else
           //     mazeDisplayer.setCharacterPosition(characterRow, characterColumn);
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            if(mazeDisplayer.getMaze()[characterRow + 1][characterColumn] == 0)
            mazeDisplayer.setCharacterPosition(characterRow + 1, characterColumn);
          //  else
           //     mazeDisplayer.setCharacterPosition(characterRow, characterColumn);
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            if(mazeDisplayer.getMaze()[characterRow][characterColumn + 1] == 0)
                mazeDisplayer.setCharacterPosition(characterRow, characterColumn + 1);
           // else
          //      mazeDisplayer.setCharacterPosition(characterRow, characterColumn);
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
            if(mazeDisplayer.getMaze()[characterRow][characterColumn - 1] == 0)
                mazeDisplayer.setCharacterPosition(characterRow, characterColumn - 1);
           // else
            //    mazeDisplayer.setCharacterPosition(characterRow, characterColumn);
        }
        notifyObservers(mazeDisplayer);
        keyEvent.consume();
    }



    //endregion

    public void setRows(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
            if (rows < 10 || columns < 10) {
                showAlert("The maze is too small! what are you child?");
            }
            txtfld_columnsNum.requestFocus();
            keyEvent.consume();
        //    mazeDisplayer.setMaze(getNewMaze(rows,columns));
          //  mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
        }
    }

    public void setCols(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
            if (rows < 10 || columns < 10) {
                showAlert("The maze is too small! what are you child?");

            }
            mazeDisplayer.setMaze(getNewMaze(rows,columns));
            if(solve_button.isDisable())
                solve_button.setDisable(false);
            mazeDisplayer.requestFocus();
            keyEvent.consume();
            //mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
        }
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
}
