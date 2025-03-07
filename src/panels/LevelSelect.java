package panels;

import java.util.ArrayList;
import character.GoblinRanger;
import character.Necromancer;
import character.Shaman;
import character.GameCharacter;
import character.Grog;
import character.Goblin;
import character.Xande;
import javafx.application.Platform;
import javafx.stage.Stage;
import player.PlayerTeam;

public class LevelSelect {

	public static void Level(int level, Stage primary) {
		switch (level) {
		case 1: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();

			enemy.add(new Goblin());
			enemy.add(new Goblin());

			FightScene level0 = new FightScene(PlayerTeam.player(), enemy, primary, "village.jpeg", "sound.wav", 1);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level0);
			});

			break;
		}
		case 2: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();

			enemy.add(new Shaman());
			enemy.add(new Necromancer());
			enemy.add(new GoblinRanger());
			enemy.add(new Shaman());

			FightScene level0 = new FightScene(PlayerTeam.player(), enemy, primary, "forest.jpeg", "sound.wav", 2);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level0);
			});
			break;

		}
		case 3: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();

			enemy.add(new GoblinRanger());
			enemy.add(new GoblinRanger());
			enemy.add(new GoblinRanger());
			enemy.add(new Necromancer());

			FightScene level0 = new FightScene(PlayerTeam.player(), enemy, primary, "tunnel.jpeg", "sound.wav", 3);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level0);
			});
			break;

		}
		case 4: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();
			enemy.add(new Grog());
			enemy.add(new Necromancer());
			enemy.add(new GoblinRanger());
			enemy.add(new GoblinRanger());

			FightScene level0 = new FightScene(PlayerTeam.player(), enemy, primary, "arena.jpeg", "sound.wav", 4);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level0);
			});
			break;

		}
		case 5: {

			ArrayList<GameCharacter> enemy = new ArrayList<>();

			enemy.add(new Xande());

			FightScene level0 = new FightScene(PlayerTeam.player(), enemy, primary, "bossroom.jpeg", "sound.wav", 5);

			Platform.runLater(() -> {
				primary.getScene().setRoot(level0);
			});

		}

		}

	}

}