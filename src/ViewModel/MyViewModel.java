package ViewModel;

import View.MazeDisplayer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.*;
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
        }
        catch (IOException e)
        {
            System.out.println("IOException");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("ClassNotFoundException");
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
        /*try {
            FileOutputStream fo = new FileOutputStream(outputFile);
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(mazeDisplayer);
            fo.close();
            os.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            return;
        }
        catch (IOException e)
        {
            System.out.println("IOException");
            return;
        }*/
    }
}
