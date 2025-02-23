package grid;

import java.util.ArrayList;

import character.Enemy;
import character.GameCharacter;
import character.Swordman;
import character.Wizard;
import javafx.stage.Stage;

public class LevelSelect {
	
	
	public static void Level(int level,Stage primary) {
		switch (level) {
		case 1:{
			ArrayList<GameCharacter>player=new ArrayList<>();
	       
		   player.add(new Swordman());	       
	       player.add(new Wizard());       
	    
	     
	       
	       ArrayList<Enemy>enemy=new ArrayList<>();
//	       
	       enemy.add(new Enemy());	       
	       enemy.add(new Enemy());	   
	       enemy.add(new Enemy());	
	      
		
		
		FightScene level0=new FightScene(player,enemy,primary,"sand.png");
		
		primary.getScene().setRoot(level0);
		
		break;
		}
		case 2:{
			
			ArrayList<GameCharacter>player=new ArrayList<>();
		       
			   player.add(new Swordman());
		            
		       ArrayList<Enemy>enemy=new ArrayList<>();
//		       
		       enemy.add(new Enemy());	       
		       enemy.add(new Enemy());	     
		       enemy.add(new Enemy());		       
		       enemy.add(new Enemy());

				FightScene level1=new FightScene(player,enemy,primary,"finalfan.png");
				primary.getScene().setRoot(level1);
			
		}
			
			
			
			
		}
	
			
			
	}
	
	
}
