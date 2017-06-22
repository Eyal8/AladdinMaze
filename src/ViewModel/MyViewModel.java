package ViewModel;

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
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import java.io.*;
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

 /*   public void setRows(KeyCode keyCode)
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
    }*/

      /* public void load()
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
    }*/

    public void exit() {
        model.exit();
    }

    public void aboutTheProgrammers() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Who are we?");
        alert.setHeaderText("information about us");
        alert.setContentText("Our names are Eyal Arviv and Shani Houri and we are totaly awesome!");

        alert.showAndWait();
    }

    public void aboutTheAlgorithms() {
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

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
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

    public Position getGoalPosition(){return model.getGoalPosition();}

}
