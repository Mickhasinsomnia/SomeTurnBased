package application;

import grid.LevelSelect;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {
   
    private Stage primary;
    public static VBox menu;
    
    @Override
    public void start(Stage primaryStage) {
       
    	Button startButton=setButton(1);
    	Button startButton2=setButton(2);
        primary=primaryStage;
        
        VBox root = new VBox(20); 
        root.setAlignment(Pos.CENTER); 
        root.getChildren().addAll(startButton, startButton2);
        Main.menu=root;
       
       
       
       Scene scene = new Scene(root, 900, 700); 
       primaryStage.setTitle("Start Game");
       primaryStage.setScene(scene);
       primaryStage.show();
   
       
      
       
      primaryStage.setResizable(false);
    }

    
    public static void main(String[] args) {

    	launch(args);
    }
    
    private Button setButton(int level) {
    	Button startButton = new Button("Level "+level);
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24)); 
        startButton.setMinWidth(200);  
        startButton.setMinHeight(50);
        startButton.setOnMouseClicked(e->{
     	   LevelSelect.Level(level, primary);
        });
    	return startButton;
    }
}
