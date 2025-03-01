package panels;

import java.util.ArrayList;

import character.Enemy;
import character.EnemyArcher;
import character.EnemyHealer;
import character.GameCharacter;
import character.Priest;
import character.Swordman;
import character.Wizard;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import player.PlayerTeam;

public class LevelSelect {

	public static void Level(int level, Stage primary) {
		switch (level) {
		case 1: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();

			enemy.add(new EnemyArcher());

			enemy.add(new EnemyArcher());
			
			enemy.add(new Enemy());
			enemy.add(new EnemyHealer());

			FightScene level0 = new FightScene(PlayerTeam.player(), enemy, primary, "sand.png", "sound.mp3", 1);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level0);
			});

			break;
		}
		case 2: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();

			enemy.add(new Enemy());
			enemy.add(new EnemyHealer());

			FightScene level1 = new FightScene(PlayerTeam.player(), enemy, primary, "finalfan.png", "sound.mp3", 2);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level1);
			});

		}

		}

	}

}