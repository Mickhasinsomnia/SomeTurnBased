package application;

import java.util.ArrayList;

import character.Enemy;
import character.GameCharacter;
import character.Swordman;
import character.Wizard;
import grid.FightScene;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
       ArrayList<GameCharacter>player=new ArrayList<>();
       
       player.add(new Swordman());
       
       player.add(new Swordman());
       
       player.add(new Wizard());
       
       player.add(new Wizard());
     
       
       ArrayList<Enemy>enemy=new ArrayList<>();
//       
       enemy.add(new Enemy());
       
       enemy.add(new Enemy());
       
       enemy.add(new Enemy());
       
       enemy.add(new Enemy());
       
       
       
       FightScene use=new FightScene(player,enemy);
       
      
      Scene ans=new Scene(use,900,700);
       
      primaryStage.setScene(ans);

      primaryStage.show();
       
      primaryStage.setResizable(false);
    }

    
    public static void main(String[] args) {

    	launch(args);
    }
}
