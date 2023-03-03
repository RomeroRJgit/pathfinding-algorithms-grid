package graph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("graph.fxml"));
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStylesheets().add("https://fonts.googleapis.com/css2?family=Raleway:wght@300;400;500;700;800&display=swap");
        root.getStylesheets().add("https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;700;800&display=swap");
        		
        stage.setTitle("Pathfinder");		
        stage.setScene(new Scene(root));
        stage.show();
    }


    public static void main(String[] args) {		
        launch(args);			
    }
	
}
