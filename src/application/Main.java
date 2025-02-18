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
import javafx.scene.layout.StackPane;
import javafx.application.Platform;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
       ArrayList<GameCharacter>player=new ArrayList<>();
       
       player.add(new Swordman());
       
       player.add(new Swordman());
     
       
       ArrayList<Enemy>enemy=new ArrayList<>();
//       
       enemy.add(new Enemy());
//       
       enemy.add(new Enemy());
       
       
       FightScene use=new FightScene(player,enemy);
       
       
       
       StackPane root=new StackPane();
       root.getChildren().add(use);
       Scene ans=new Scene(root,750,750);
       
       primaryStage.setScene(ans);
      primaryStage.show();
       
       
    }

    
    public static void main(String[] args) {
//    	 ArrayList<GameCharacter>player=new ArrayList<>();
//    
//       player.add(new Swordman());
//       
//       player.add(new Wizard());
//       
//       
//       ArrayList<Enemy>enemy=new ArrayList<>();
//       
//       enemy.add(new Enemy());
//       
//       enemy.add(new Enemy());
//       enemy.add(new Enemy());
//       enemy.add(new Enemy());
//       
//       Combat.fight(player, enemy);
    	launch(args);
    }
}
