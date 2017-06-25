package View;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.input.*;
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
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Pair;

/**
 * Created by Shani on 18/06/2017.
 */
public class View implements Observer, IView {

    private int hint = 0;
    private boolean solve = false;
    private boolean mute = true;
    @FXML
    public javafx.scene.control.TextField txtfld_rowsNum;
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.RadioButton solve_button;
    public javafx.scene.control.Button hint_button;
    public javafx.scene.control.RadioButton volume_button;
    public javafx.scene.control.Label char_row_text;
    public javafx.scene.control.Label char_column_text;
    public  javafx.scene.control.MenuItem newFile;
    public javafx.scene.control.Label hints_number;
    public BorderPane board;
    public static MediaPlayer mediaPlayer;
    public static Media song;
    private double mouseStartX;
    private double mouseStartY;

    public void generateMaze() {
        solve = false;
        zeroHint();
        checkSong("");
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());
        newFile.setDisable(true);
        if(rows<10 || columns<10)
        {
            showAlert("What are you a child?","Too small dimensions - generate default 10X10 maze...");
            viewModel.generateMaze(10, 10);
        }
        else
            viewModel.generateMaze(rows, columns);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText("");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("alert.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        if(title.equals("Help")) {
            alert.setOnCloseRequest(event -> {
                mediaPlayer.pause();
                setSong("resources/music/aladdin-friendlikemehighquality_cutted.mp3");
                mediaPlayer.setVolume(0.2);
                mediaPlayer.play();
                volume_button.setSelected(false);
                mute = false;
            });
        }

        alert.show();
        mazeDisplayer.requestFocus();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        solve = false;
        zeroHint();
        viewModel.KeyPressed(keyEvent.getCode());
        keyEvent.consume();
    }

    private void zeroHint()
    {
        mazeDisplayer.zeroHint();
        hint = 0;
        setHint();
        hint_button.setDisable(false);
    }
    public void solveMaze() {
        zeroHint();
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());
        if(!solve) {
            showAlert("Please wait", "Solving maze...");
            solve = true;
        }
        else {
            showAlert("Please wait", "Hiding solution...");
            solve = false;
        }
        viewModel.solveMaze(rows, columns);
    }

    public void getHint()
    {
        solve = false;
        hint++;
        setHint();
        mazeDisplayer.getHint();
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());
        viewModel.solveMaze(rows, columns);
        if(hint + 1 == viewModel.getPath().size()) {
            showAlert("You are not so smart are you?","You've reached maximum number of hints");
            hint_button.setDisable(true);
            mazeDisplayer.requestFocus();
        }
    }
    private void setHint(){
        hints_number.setText(String.valueOf(hint));
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o == viewModel)
        {
            if(solve) {
                mazeDisplayer.setSolve(true);
            }
            else
            {
                mazeDisplayer.setSolve(false);
            }
            displayMaze(viewModel.getBoard());
            newFile.setDisable(false);
            solve_button.setDisable(false);
            mazeDisplayer.requestFocus();
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setGoalPosition(viewModel.getGoalPosition());
        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setPath(viewModel.getPath());
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        if(viewModel.getGoalPosition() != null) {
            if (viewModel.getGoalPosition().getRowIndex() == characterPositionRow && viewModel.getGoalPosition().getColumnIndex() == characterPositionColumn) {
                View.mediaPlayer.pause();
                setSong("resources/music/aladdin-awholenewworldhighquality_cutted.mp3");
                View.mediaPlayer.setVolume(0.7);
                View.mediaPlayer.play();
                mute = false;
                String title= "Congratulations - You found the magic lamp!";
                String content = "Start a new game, load a game or exit";
                showAlert(title, content);
            }
        }
    }

    public void setRows(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            if (rows < 10) {
                showAlert("What are you a child?","Too small dimensions - generate default 10X10 maze...");
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
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
            if (columns < 10) {
                showAlert("What are you a child?","Too small dimensions - generate default 10X10 maze...");
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
            mazeDisplayer.redraw();
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
        solve = false;
        volume_button.setSelected(false);
        mute = false;
        if(mediaPlayer != null)
            mediaPlayer.play();
        FileChooser fc = new FileChooser();
        fc.setTitle("Load maze...");
        fc.setInitialDirectory(new File("./savedMazes"));
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files",".txt"));
        File chosen = fc.showOpenDialog((Stage) mazeDisplayer.getScene().getWindow());
        if(chosen != null) {
            viewModel.load(chosen);
            checkSong("");
            solve_button.setSelected(false);
            hint_button.setDisable(false);
        }
    }

    public void save()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Saving the maze");
        fc.setInitialDirectory(new File("./savedMazes"));
        // FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("Text files", ".txt");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", ".txt"));
        File chosen = fc.showSaveDialog((Stage) mazeDisplayer.getScene().getWindow());
        if(chosen != null)
            viewModel.save(chosen);
    }

    public void exit(){
        viewModel.exit();
        Platform.exit();
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }

    public void aboutTheProgrammers(){
        String title= "Who are we? - information about us";
        String content = "Our names are Eyal Arviv and Shani Houri and we are totaly awesome!";
        showAlert(title, content);
    }

    public void help()
    {
        if(mediaPlayer == null)
            checkSong("Help");
        else {
            mediaPlayer.stop();
            setSong("resources/music/Help.mp3");
            mediaPlayer.setVolume(0.9);
            mediaPlayer.play();
        }
        String title= "Help";
        String content = "You are Aladdin. You've been captured and in order to set free you must obtain the lamp in the cave. \n" +
                "Rules:\n -You must stay on the path.\n" +
                "-You can only move up, down, right or left.\n" +
                "-No stepping on the walls of the cave.\n" +
                "-Have fun! :D";
        showAlert(title, content);
    }
    public void aboutTheAlgorithms(){
        String title= "Information about the algorithms used in this game";
        String content = "In this game we used a few algorithms:\n\n" +
                "first, the algorithm to generate the maze the DFS (Depth First Search) algorithm.\n\n" +
                "second, the algorithm to solve the maze the Best first search algorithm.";
        showAlert(title, content);
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                board.setPrefWidth(newSceneWidth.longValue()*0.9);
                mazeDisplayer.setWidth(board.getPrefWidth());
                viewModel.setxCharPos(viewModel.getCharacterPositionColumn()*mazeDisplayer.getWidth()/mazeDisplayer.getMaze()[0].length);
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                board.setPrefHeight(newSceneHeight.longValue()*0.9);
                mazeDisplayer.setHeight(board.getPrefHeight());
                viewModel.setyCharPos(viewModel.getCharacterPositionRow()*mazeDisplayer.getWidth()/mazeDisplayer.getMaze().length);
                mazeDisplayer.redraw();
            }
        });
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        char_row_text.textProperty().bind(viewModel.CharacterRow);
        char_column_text.textProperty().bind(viewModel.CharacterColumn);
    }

    public void setSong(String url)
    {
        volume_button.setDisable(false);
        volume_button.setSelected(false);
        mute = false;
        String path = new File(url).getAbsolutePath();
        song = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(song);
    }

    private void checkSong(String s)
    {
        if(s.equals("Help"))
        {
            setSong("resources/music/Help.mp3");
            mediaPlayer.setVolume(0.9);
            mediaPlayer.play();
        }
        else if(mediaPlayer == null && song == null)
        {
            setSong("resources/music/aladdin-friendlikemehighquality_cutted.mp3");
            mediaPlayer.setVolume(0.2);
            mediaPlayer.play();
        }
        else if(mediaPlayer!= null && !song.getSource().contains("friend")) {//second song
            mediaPlayer.stop();
            setSong("resources/music/aladdin-friendlikemehighquality_cutted.mp3");
            mediaPlayer.setVolume(0.2);
            mediaPlayer.play();
        }
        mute = false;
    }
    public void properties()
    {
        boolean firstLine = true;
        String everything = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("config.properties"));
            try {
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    line = br.readLine();
                    line = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (line != null || firstLine) {
                    if(firstLine)
                    {
                        firstLine = false;
                        try {
                            line = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                everything= sb.toString();


            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        boolean finishedParsing = false;
        String[] split = everything.split("=|\n|\r");
        String content = "";
        content += "The solving maze algorithm is " + split[1]  + ".\n\n There are " + split[4] + " threads.\n\n The algorithm that generates the maze is " + split[7];
        String title= "Game properties";
        showAlert(title, content);
    }

    public void mouse1(MouseEvent me)
    {
     mouseStartX = me.getX();
     mouseStartY = me.getY();
     viewModel.setxCharPos(mouseStartX);
     viewModel.setyCharPos(mouseStartY);
     me.consume();
    }

    public void mouse2(MouseEvent me)
    {
        if(!viewModel.checkWall(mazeDisplayer.getHeight() / viewModel.getBoard().length, mazeDisplayer.getWidth() / viewModel.getBoard()[0].length, me.getX(), me.getY()))
        {
            showAlert("Warning!", "You can't move through wall!");
        }
        solve = false;
        me.consume();
    }

    public void mouse3(MouseEvent me)
    {
        viewModel.mouse(mazeDisplayer.getHeight() / viewModel.getBoard().length, mazeDisplayer.getWidth() / viewModel.getBoard()[0].length, me.getX(), me.getY());
        me.consume();
    }
    public void mute()
    {
        if(mute) {
            mediaPlayer.play();
            mute = false;
        }
        else {
            mediaPlayer.pause();
            mute = true;
        }
    }
}

