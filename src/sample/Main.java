package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 300, 275);
    //    scene.getStylesheets().add

        //primaryStage.setResizable(false); size of window not dynamic
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //stage - the outside of the window
    //scene - the inside of the app

    public static void main(String[] args) {
        launch(args);
    }
}
