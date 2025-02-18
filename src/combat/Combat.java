package combat;

import character.GameCharacter;
import character.Enemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Combat {

	public static ArrayList<Enemy> getEnemies(ArrayList<Enemy> b) {
		return b;
	}

	public static void checkForDeadCharacters(ArrayList<GameCharacter> a, ArrayList<Enemy> b) {

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
