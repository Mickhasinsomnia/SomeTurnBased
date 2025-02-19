package grid;

import character.*;
import combat.Combat;
import character.Enemy;
import javafx.application.Platform;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

public class FightScene extends Pane {

	private ArrayList<GameCharacter> players;
	private ArrayList<Enemy> enemies;
	private Pane pane;
	private boolean pressed;
	private int choice;
	private int type;

	public FightScene(ArrayList<GameCharacter> players, ArrayList<Enemy> enemies) {
		this.players = players;
		this.enemies = enemies;
		pane = new Pane();
		pane.setMinSize(700, 700);
		this.getChildren().add(pane);
		pressed = false;

		drawScene();

		Button attackButton = new Button("Attack");
		Button magicButton = new Button("Magic");

		attackButton.setLayoutX(700);
		attackButton.setLayoutY(650);
		magicButton.setLayoutX(800);
		magicButton.setLayoutY(650);

		magicButton.setMinSize(50, 30);
		attackButton.setMinSize(50, 30);

		this.getChildren().add(attackButton);
		this.getChildren().add(magicButton);

		Thread combatThread = new Thread(() -> {
			while (players.size() > 0 && enemies.size() > 0) {
				ArrayList<GameCharacter> all = new ArrayList<>();
				all.addAll(players);
				all.addAll(enemies);
				Collections.sort(all);

				for (GameCharacter current : all) {
					if (current instanceof Enemy) {
						Enemy enemy = (Enemy) current;

						ArrayList<GameCharacter> alivePlayers = new ArrayList<>();
						for (GameCharacter player : players) {
							if (player.getHp() > 0) {
								alivePlayers.add(player);
							}
						}

						if (!alivePlayers.isEmpty()) {
							GameCharacter target = alivePlayers.get((int) (Math.random() * alivePlayers.size()));
							enemy.takeAction(target);
						}
					} else {
						choice = -1;
						if (enemies.size() <= 0) {
							Platform.exit();
						}
						System.out.println(current.getClass().getSimpleName() + "'s turn. Choose an enemy to attack:");
						ArrayList<Enemy> chooseEnemy = Combat.getEnemies(enemies);
						for (int i = 0; i < chooseEnemy.size(); i++) {
							System.out.println(i + 1 + ". " + enemies.get(i).getClass().getSimpleName() + " HP: "
									+ enemies.get(i).getHp());
						}

						attackButton.setOnMouseClicked(event -> {
							if (choice != -1) {
								pressed = true;
								type = 1;
							}

						});

						magicButton.setOnMouseClicked(event -> {
							if (choice != -1) {
								pressed = true;
								type = 2;
							}
						});
						while (!pressed) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						System.out.println();
						if (choice >= 0 && choice < enemies.size()) {
							Enemy selectedEnemy = enemies.get(choice);
							System.out.println("1: attack");
							System.out.println("2: magic");
							int damageAction = type;
							if (damageAction == 1) {
								current.attack(selectedEnemy);
							} else if (damageAction == 2) {
								current.magic(selectedEnemy);
							}
						}
					
					}
					checkForDeadCharacters(players, enemies);
					Platform.runLater(() -> drawScene());
					pressed = false;

				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (players.size() > enemies.size()) {
				System.out.println("We win");
			} else {
				System.out.println("Try again");
			}
			Platform.exit();

		});

		combatThread.start();
	}

	private void drawScene() {
		Image img = new Image("file:res/finalfan.png");
		BackgroundImage bgImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		this.setBackground(new Background(bgImg));

	

		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(100, 250));

		use.add(new Pair<>(200, 550));

		use.add(new Pair<>(250, 375));

		pane.getChildren().clear();

		int index = 0;
		for (GameCharacter player : players) {
			Text first = new Text(player.getClass().getSimpleName() +" "+" HP: " + player.getHp()+" "+" MP: "+player.getMana());
			first.setFont(Font.font(20));
			first.setLayoutX(use.get(index).getKey());
			first.setLayoutY(use.get(index).getValue());

			Image imgchar = new Image("file:res/yourImage.png");
			if (player instanceof Wizard) {
				imgchar = new Image("file:res/wizz.png");
			}
			ImageView rep = new ImageView(imgchar);
			rep.setFitWidth(120);
			rep.setFitHeight(120);

			rep.setLayoutX(use.get(index).getKey());
			rep.setLayoutY(use.get(index).getValue() + 30);

			pane.getChildren().addAll(first, rep);

			index++;

		}

		ArrayList<Pair<Integer, Integer>> enemyPos = new ArrayList<>();
		enemyPos.add(new Pair<>(600, 250));

		enemyPos.add(new Pair<>(650, 500));

		enemyPos.add(new Pair<>(560, 350));

		int countS = 0;
		for (Enemy enemy : enemies) {
			Text second = new Text("Enemy HP: " + enemy.getHp());
			second.setFont(Font.font(20));
			second.setLayoutX(enemyPos.get(countS).getKey());
			second.setLayoutY(enemyPos.get(countS).getValue());

			Image imgchar = new Image("file:res/up_2.png");
			ImageView rep = new ImageView(imgchar);

			rep.setFitWidth(88);
			rep.setFitHeight(88);
			rep.setLayoutX(enemyPos.get(countS).getKey());
			rep.setLayoutY(enemyPos.get(countS).getValue() + 20);

			final int c = countS;
			rep.setOnMouseClicked(event -> {
				choice = c;
				System.out.println("Choose"+(choice+1));
				
			});

			pane.getChildren().addAll(second, rep);

			countS++;
		}
	}

	public void checkForDeadCharacters(ArrayList<GameCharacter> a, ArrayList<Enemy> b) {
		a.removeIf(character -> character.getHp() <= 0);
		b.removeIf(enemy -> enemy.getHp() <= 0);
	}

}