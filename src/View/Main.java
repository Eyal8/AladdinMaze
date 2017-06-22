package View;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    @FXML
    private MazeDisplayer mazeDisplayer;

    @Override
    public void start(Stage primaryStage) throws Exception{

        MyModel model = new MyModel();
        model.startServers();
       // model.generateMaze(10,10);
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        primaryStage.setTitle("My Application!");
        FXMLLoader fxmlLoader = new FXMLLoader();

        Parent root = fxmlLoader.load(getClass().getResource("View.fxml").openStream());
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        View view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        primaryStage.show();
      //  model.stopServers();// exit

        //Rise Servers
        //  mazeDisplayer.prefHeight(primaryStage.getHeight());
        //  mazeDisplayer.prefWidth(primaryStage.getWidth());

   /*     SimpleDoubleProperty stageWidthProperty = new SimpleDoubleProperty(primaryStage.getWidth());
        SimpleDoubleProperty stageHeightProperty = new SimpleDoubleProperty(primaryStage.getHeight());
        stageHeightProperty.bind(stageWidthProperty.divide(2));
        stageHeightProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                primaryStage.setHeight(newValue.doubleValue());
                System.out.println("Stage height = " + newValue);
            }
        });
        stageWidthProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                primaryStage.setWidth(newValue.doubleValue());
                System.out.println("Stage width = " + newValue);
            }
        });
*/


        // animate the stage width and the scene is resized to fill the viewable area of the stage.
      /* Timeline resizer = new Timeline(
                new KeyFrame(Duration.ZERO,       new KeyValue(stageWidthProperty, 800, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(1), new KeyValue(stageWidthProperty, 500, Interpolator.EASE_BOTH))
        );
        resizer.setCycleCount(Animation.INDEFINITE);
        resizer.setAutoReverse(true);
        resizer.play();*/


    }

    /*private void setSceneEvents(final Scene scene) {
        //handles mouse scrolling
        scene.setOnScroll(
                new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {
                        event.isControlDown()
                        double xscale = page.getScaleX() * event.getDeltaY()/35;
                        double yscale= page.getScaleY() * event.getDeltaY()/35;
                        System.out.println("xscale: "+ xscale);
                        System.out.println("yscale: "+ yscale);
                        //page.setScaleX(page.getScaleX() * event.getDeltaY()/35);
                        // page.setScaleY(page.getScaleY() * event.getDeltaY()/35);
                        event.consume();
                    }
                });

    }*/

    public static void main(String[] args) {
        launch(args);
    }
}

