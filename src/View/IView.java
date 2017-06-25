package View;

import ViewModel.MyViewModel;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Created by Shani on 20/06/2017.
 */
public interface IView {
    void generateMaze();
    void displayMaze(int[][] maze);
    void KeyPressed(KeyEvent keyEvent);
    void solveMaze();
    void getHint();
    void setRows(KeyEvent keyEvent);
    void setCols(KeyEvent keyEvent);
    void zooming(ScrollEvent se);
    void load();
    void save();
    void exit();
    void aboutTheProgrammers();
    void help();
    void aboutTheAlgorithms();
    void setResizeEvent(Scene scene);
    void setViewModel(MyViewModel viewModel);
    void setSong(String url);
    void properties();
    void mouse1(MouseEvent me);
    void mouse2(MouseEvent me);
    void mouse3(MouseEvent me);
    void mute();
}
