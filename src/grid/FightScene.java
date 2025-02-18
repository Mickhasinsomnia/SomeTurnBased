package grid;

import character.GameCharacter;
import combat.Combat;
import character.Enemy;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;

public class FightScene extends VBox {

	private ArrayList<GameCharacter> players;
	private ArrayList<Enemy> enemies;
	private Pane canvas;
	private boolean pressed;

	public FightScene(ArrayList<GameCharacter> players, ArrayList<Enemy> enemies) {
		this.players = players;
		this.enemies = enemies;
		canvas = new Pane();
		canvas.setMinSize(700, 700);
		this.getChildren().add(canvas);
		pressed = false;

		drawScene();

		Button attackButton = new Button("Attack");
		Button magicButton = new Button("Magic");

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

						System.out.println(current.getClass().getSimpleName() + "'s turn. Choose an enemy to attack:");
						ArrayList<Enemy> chooseEnemy = Combat.getEnemies(enemies);
						for (int i = 0; i < chooseEnemy.size(); i++) {
							System.out.println(i + 1 + ". " + enemies.get(i).getClass().getSimpleName() + " HP: "
									+ enemies.get(i).getHp());
						}
						attackButton.setOnMouseClicked(event -> {
							pressed = true;

						});

						magicButton.setOnMouseClicked(event -> {
							pressed = true;
						});
						int choice = 1;
						while (!pressed) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						System.out.println();
						if (choice > 0 && choice <= enemies.size()) {
							Enemy selectedEnemy = enemies.get(choice - 1);
							System.out.println("1: attack");
							System.out.println("2: magic");
							int damageAction = choice;
							if (damageAction == 1) {
								current.attack(selectedEnemy);
							} else if (damageAction == 2) {
								current.magic(selectedEnemy);
							}
						}
					}
					Combat.checkForDeadCharacters(players, enemies);
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
		this.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));

		canvas.getChildren().clear();
		int xPos = 50;
		for (GameCharacter player : players) {
			Text first = new Text(player.getClass().getSimpleName() + " HP: " + player.getHp());
			first.setFont(Font.font(15));
			first.setLayoutX(xPos);
			first.setLayoutY(50);
			Circle rep = new Circle(20);
			rep.setLayoutX(xPos + 10);
			rep.setLayoutY(80);
			rep.setOnMouseClicked(event->{
				System.out.println("enemy");
			});
			canvas.getChildren().addAll(first, rep);
			xPos += 150;
		}
		int yPos = 150;
		for (Enemy enemy : enemies) {
			Text second = new Text("Enemy HP: " + enemy.getHp());
			second.setFont(Font.font(15));
			second.setLayoutX(50);
			second.setLayoutY(yPos);
			Circle rep = new Circle(20);
			rep.setLayoutX(50);
			rep.setLayoutY(yPos+60);
			yPos += 150;
			rep.setOnMouseClicked(event->{
				System.out.println("enemy");
			});

			canvas.getChildren().addAll(second, rep);
		}
	}

}
