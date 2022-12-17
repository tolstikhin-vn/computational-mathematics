import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/sample.fxml")));

        Scene mainScene = new Scene(root);
        stage.setTitle("Дифференциальные уравнения");
        stage.setScene(mainScene);
        stage.show();
        stage.setResizable(false);
    }
}