package grid;

import character.GameCharacter;
import combat.Combat;
import character.Enemy;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class FightScene extends GridPane {

    private ArrayList<GameCharacter> players;
    private ArrayList<Enemy> enemies;
    private Canvas canvas;
    private String playerAction = ""; 

    public FightScene(ArrayList<GameCharacter> players, ArrayList<Enemy> enemies) {
        this.players = players;
        this.enemies = enemies;

        
        canvas = new Canvas(800, 800);
        this.add(canvas, 0, 0);

       
        drawScene();

       
        Button attackButton = new Button("Attack");
        attackButton.setOnAction(e -> playerAction = "attack");

        Button magicButton = new Button("Magic");
        magicButton.setOnAction(e -> playerAction = "magic");

      
        this.add(attackButton, 0, 1);
        this.add(magicButton, 1, 1);
    }

    
    public String getPlayerAction() {
        return playerAction;
    }

    private void drawScene() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
       
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
       
        gc.setFill(Color.BLUE);
        int xPos = 50;
        for (GameCharacter player : players) {
            gc.fillText("Swordman HP: " + player.getHp(), xPos, 50);
            xPos += 150; 
        }

      
        gc.setFill(Color.RED);
        int yPos = 150;
        for (Enemy enemy : enemies) {
            gc.fillText("Enemy HP: " + enemy.getHp(), 50, yPos);
            yPos += 150;  
        }
    }
}
