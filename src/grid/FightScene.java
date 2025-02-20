package grid;

import character.*;
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
import javafx.scene.shape.Circle;
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

		setButton();

		Thread combatThread = new Thread(() -> {
			while (players.size() > 0 && enemies.size() > 0) {
				ArrayList<GameCharacter> all = new ArrayList<>();
				all.addAll(players);
				all.addAll(enemies);
				Collections.sort(all);
				Circle onHead = new Circle(20);
				Circle onHead2 = new Circle(20);
				for (GameCharacter current : all) {
					if (current.getHp() <= 0)
						continue;
					if (current instanceof Enemy) {
						try {
							Thread.sleep(2500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Enemy enemy = (Enemy) current;
						onHead2.setLayoutX(enemy.getPosX());
						onHead2.setLayoutY(current.getPosY());

						ArrayList<GameCharacter> alivePlayers = new ArrayList<>();
						for (GameCharacter player : players) {
							if (player.getHp() > 0) {
								alivePlayers.add(player);
							}
						}

						if (!alivePlayers.isEmpty()) {
							enemy.chooseTarget(alivePlayers);
						}
					} else {
						choice = -1;
						if (enemies.size() <= 0) {
							Platform.exit();
						}
						Platform.runLater(() -> {
							onHead.setLayoutX(current.getPosX() - 20);
							onHead.setLayoutY(current.getPosY() + 60);
							this.getChildren().add(onHead);
						});
						while (!pressed) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						while (choice == -1) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Enemy selectedEnemy = enemies.get(choice);
						int damageAction = type;
						if (damageAction == 1) {
							current.attack(selectedEnemy);
						} else if (damageAction == 2) {
							current.magic(selectedEnemy);
						}
						Platform.runLater(() -> {
							this.getChildren().remove(onHead);
						});
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
			Platform.exit();
		});
		combatThread.start();
	}

	private void setButton() {
		Button attackButton = new Button("Attack");
		Button magicButton = new Button("Magic");

		attackButton.setLayoutX(700);
		attackButton.setLayoutY(650);
		magicButton.setLayoutX(800);
		magicButton.setLayoutY(650);

		magicButton.setMinSize(60, 40);
		attackButton.setMinSize(60, 40);

		this.getChildren().addAll(attackButton,magicButton);
		
		attackButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 1;
		});

		magicButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 2;
		});
	}

	private void drawScene() {
		Image img = new Image(ClassLoader.getSystemResource("finalfan.png").toString());
		BackgroundImage bgImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		this.setBackground(new Background(bgImg));

		pane.getChildren().clear();

		drawPlayer();
		drawEnemy();

	}

	public void checkForDeadCharacters(ArrayList<GameCharacter> a, ArrayList<Enemy> b) {
		a.removeIf(character -> character.getHp() <= 0);
		b.removeIf(enemy -> enemy.getHp() <= 0);
	}

	private ArrayList<Pair<Integer, Integer>> setupPlayer() {
		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(100, 250));
		use.add(new Pair<>(200, 550));
		use.add(new Pair<>(250, 375));
		use.add(new Pair<>(360, 190));
		return use;
	}

	private ArrayList<Pair<Integer, Integer>> setupEnemy() {
		ArrayList<Pair<Integer, Integer>> enemyPos = new ArrayList<>();
		enemyPos.add(new Pair<>(750, 250));

		enemyPos.add(new Pair<>(500, 500));

		enemyPos.add(new Pair<>(600, 300));

		enemyPos.add(new Pair<>(700, 400));
		return enemyPos;
	}

	private void drawPlayer() {
		ArrayList<Pair<Integer, Integer>> pos = setupPlayer();
		int index = 0;
		for (GameCharacter player : players) {
			Text first = new Text(player.getClass().getSimpleName() + " " + " HP: " + player.getHp() + " " + " MP: "
					+ player.getMana());
			first.setFont(Font.font(20));
			first.setLayoutX(pos.get(index).getKey());
			first.setLayoutY(pos.get(index).getValue());

			Image imgchar = new Image(player.getSelf());

			ImageView rep = new ImageView(imgchar);
			rep.setFitWidth(120);
			rep.setFitHeight(120);

			rep.setLayoutX(pos.get(index).getKey());
			rep.setLayoutY(pos.get(index).getValue() + 30);

			player.setPos(rep.getLayoutX(), rep.getLayoutY());

			pane.getChildren().addAll(first, rep);

			index++;
		}
	}

	public void drawEnemy() {
		ArrayList<Pair<Integer, Integer>> enemyPos = setupEnemy();

		int countS = 0;
		for (Enemy enemy : enemies) {
			Text second = new Text("Enemy HP: " + enemy.getHp());
			second.setFont(Font.font(20));
			second.setLayoutX(enemyPos.get(countS).getKey());
			second.setLayoutY(enemyPos.get(countS).getValue());

			Image imgchar = new Image(ClassLoader.getSystemResource("up_2.png").toString());
			ImageView rep = new ImageView(imgchar);

			rep.setFitWidth(88);
			rep.setFitHeight(88);
			rep.setLayoutX(enemyPos.get(countS).getKey());
			rep.setLayoutY(enemyPos.get(countS).getValue() + 20);

			final int c = countS;
			rep.setOnMouseClicked(event -> {
				if (pressed) {
					choice = c;
				}
			});
			enemy.setPos(rep.getLayoutX(), rep.getLayoutY());
			pane.getChildren().addAll(second, rep);
			countS++;
		}
	}
}