package combat;

import character.GameCharacter;
import character.Enemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Combat {

    public static void fight(ArrayList<GameCharacter> a, ArrayList<Enemy> b) {
        ArrayList<GameCharacter> all = new ArrayList<>();
        Scanner use = new Scanner(System.in);

        all.addAll(a);
        all.addAll(b);
        Collections.sort(all);

        for (GameCharacter current : all) {
            if (current instanceof Enemy) {
             
                Enemy enemy = (Enemy) current;
                
                GameCharacter target = a.get((int)(Math.random()*a.size()));
                enemy.takeAction(target); 
            } else {
                System.out.println(current.getClass().getSimpleName() + "'s turn. Choose an enemy to attack:");

                ArrayList<Enemy> enemies = getEnemies(b);
                for (int i = 0; i < enemies.size(); i++) {
                    System.out.println(i + 1 + ". " + enemies.get(i).getClass().getSimpleName()+ " HP: " + enemies.get(i).getHp());
                }
                int choice = use.nextInt(); 
                if (choice > 0 && choice <= enemies.size()) {
                    Enemy selectedEnemy = enemies.get(choice - 1);
                    current.attack(selectedEnemy);
                }
            }

           
            checkForDeadCharacters(a, b);
        }

        use.close();
    }

    private static ArrayList<Enemy> getEnemies(ArrayList<Enemy> b) {
        return b;
    }

    private static void checkForDeadCharacters(ArrayList<GameCharacter> a, ArrayList<Enemy> b) {
        
        a.removeIf(character -> character.getHp() <= 0);
        b.removeIf(enemy -> enemy.getHp() <= 0);

        
        for (GameCharacter character : a) {
            System.out.println(character.getClass().getSimpleName() + " HP: " + character.getHp());
        }
        for (Enemy enemy : b) {
            System.out.println(enemy.getClass().getSimpleName() + " HP: " + enemy.getHp());
        }
    }
}
