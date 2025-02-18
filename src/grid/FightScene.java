package grid;

import character.GameCharacter;
import combat.Combat;
import character.Enemy;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class FightScene extends VBox {

	private ArrayList<GameCharacter> players;
	private ArrayList<Enemy> enemies;
	private Canvas canvas;
	private boolean pressed;

	public FightScene(ArrayList<GameCharacter> players, ArrayList<Enemy> enemies) {
		this.players = players;
		this.enemies = enemies;
		canvas = new Canvas(700, 700);
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
					Platform.runLater(()->
					drawScene());
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
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.WHITE);
		int xPos = 50;
		gc.setFont(Font.font(15));
		for (GameCharacter player : players) {
			gc.fillText("Swordman HP: " + player.getHp(), xPos, 50);
			xPos += 150;
		}

		gc.setFill(Color.WHITE);
		int yPos = 150;
		for (Enemy enemy : enemies) {
			gc.fillText("Enemy HP: " + enemy.getHp(), 50, yPos);
			yPos += 150;
		}
	}

}
