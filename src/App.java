import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class App extends Application {

	@Override
	public void start(javafx.stage.Stage primaryStage) {
        TextField textField = new TextField("ssss");
        textField.setPromptText("Enter your name");

        VBox vbox = new VBox(textField);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 300, 200);

        // TODO: Add your UI initialization code here
		primaryStage.setTitle("JavaFX Application");
        primaryStage.setScene(scene); 
		primaryStage.show();
	}
}
