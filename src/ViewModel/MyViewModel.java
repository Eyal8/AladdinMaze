package ViewModel;

import View.MazeDisplayer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Random;

public class MyViewModel {

    @FXML
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;

    int[][] mazeData = { // a stub...
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1},
            {0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1},
            {1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1}
    };

    public int[][] getNewMaze(int width, int height) {
        Random rand = new Random();
        int[][] newMaze = new int[width][height];
        for (int i = 0; i < newMaze.length; i++) {
            for (int j = 0; j < newMaze[i].length; j++) {
                newMaze[i][j] = Math.abs(rand.nextInt() % 2);
            }
        }
        return newMaze;
    }

    public void generateMaze() {
       // int rows = Integer.valueOf(txtfld_rowsNum.getText());
       // int columns = Integer.valueOf(txtfld_columnsNum.getText());
        //this.mazeDisplayer.setMaze(getNewMaze(rows,columns));
        this.mazeDisplayer.setMaze(mazeData);
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void solveMaze(ActionEvent actionEvent) {
        showAlert("Solving maze..");
    }

    public void KeyPressed(KeyEvent keyEvent) {

        int characterRow = mazeDisplayer.getCharacterPositionRow();
        int characterColumn = mazeDisplayer.getCharacterPositionColumn();

        if (keyEvent.getCode() == KeyCode.UP) {
            mazeDisplayer.setCharacterPosition(characterRow - 1, characterColumn);
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            mazeDisplayer.setCharacterPosition(characterRow + 1, characterColumn);
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            mazeDisplayer.setCharacterPosition(characterRow, characterColumn + 1);
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
            mazeDisplayer.setCharacterPosition(characterRow, characterColumn - 1);
        }
        keyEvent.consume();
    }

    //region String Property for Binding
    public StringProperty CharacterRow = new SimpleStringProperty();
    public StringProperty CharacterColumn = new SimpleStringProperty();

    public String getCharacterRow() {
        return (mazeDisplayer!= null) ? mazeDisplayer.getCharacterPositionRow() + "" : "1";
    }

    public String getCharacterColumn() {
        return (mazeDisplayer!= null) ? mazeDisplayer.getCharacterPositionColumn() + "" : "1";
    }

    //endregion

}
