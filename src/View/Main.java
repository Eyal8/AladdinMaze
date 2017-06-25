package View;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;


public class Main extends Application {
    private void runMaze(Stage primaryStage)
    {
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(600);
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        primaryStage.setTitle("Help Aladding to find the magic lamp!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("View.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        View view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        primaryStage.setOnCloseRequest(event -> view.exit());
        primaryStage.show();
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage s = new Stage();
        String path = new File("resources/music/AladdinCave.mp4").getAbsolutePath();
        Media movie = new Media(new File(path).toURI().toString());
        MediaPlayer temp=new MediaPlayer(movie);
        MediaView mediaV=new MediaView(temp);
        AnchorPane anchorPane=new AnchorPane();
        temp.setAutoPlay(true);
        s.setScene(new Scene(anchorPane, 1200, 700));
        anchorPane.getChildren().add(mediaV);
        s.setTitle("Intro");
        s.setFullScreen(true);
        s.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(17));
        delay.setOnFinished( event -> {
            s.close();
            runMaze(primaryStage);
        } );
        delay.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

