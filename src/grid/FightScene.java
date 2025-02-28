package grid;

import character.*;
import character.Enemy;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import application.Main;

public class FightScene extends Pane {

	private ArrayList<GameCharacter> players;
	private ArrayList<Enemy> enemies;
	private Pane pane;
	private boolean pressed;
	private int choice;
	private int type;

	public FightScene(ArrayList<GameCharacter> players, ArrayList<Enemy> enemies, Stage primary, String bg) {
		this.players = players;
		this.enemies = enemies;

		pane = new Pane();
		pane.setMinSize(700, 700);
		this.getChildren().add(pane);
		pressed = false;

		drawScene(bg);

		setButton();
		Thread combatThread = new Thread(() -> {
		    try {
		        while (!players.isEmpty() && !enemies.isEmpty()) {
		            List<GameCharacter> all = new ArrayList<>(players);
		            all.addAll(enemies);
		            Collections.sort(all);

		            Circle onHead = new Circle(20);
		            
		            for (GameCharacter current:all) {
		               
		            
		                if (current instanceof Enemy) {
		                	
		                    try {
		                        Thread.sleep(1500);
		                    } catch (InterruptedException e) {
		                        return;
		                    }
		                    Enemy enemy = (Enemy) current;
		                    
		                    ArrayList<GameCharacter>alivePlayer=new ArrayList<>();
		                    for(GameCharacter alive:players ) {
		                    	alivePlayer.add(alive);
		                    }
		                    
		                    
		                    if (!alivePlayer.isEmpty()) {
		                        enemy.chooseTarget(alivePlayer);
		                    }
		                    else {
		                    	Platform.runLater(() -> primary.getScene().setRoot(Main.menu));
			                    return;
		                    }
		                } else {
		                    choice = -1;
		                    
		                
		                    Platform.runLater(() -> {
		                        onHead.setLayoutX(current.getPosX() - 20);
		                        onHead.setLayoutY(current.getPosY() + 60);
		                        this.getChildren().add(onHead);
		                    });

		                    while (!pressed) {
		                        try {
		                            Thread.sleep(1);
		                        } catch (InterruptedException e) {
		                            return;
		                        }
		                    }

		                    while (choice == -1) {
		                        try {
		                            Thread.sleep(1);
		                        } catch (InterruptedException e) {
		                            return;
		                        }
		                    }
		                    Enemy selectedEnemy = enemies.get(choice);

		                    if (!enemies.contains(selectedEnemy)) {
		                        continue;
		                    }

		                    playerAction(type, current, selectedEnemy);

		                    Platform.runLater(() -> this.getChildren().remove(onHead));
		                }
		                checkForDeadCharacters(players, enemies);
		                
		                if (players.isEmpty() || enemies.isEmpty()) {
		                    Platform.runLater(() -> primary.getScene().setRoot(Main.menu));
		                    return;
		                }
		                Platform.runLater(() -> drawScene(bg));
		                pressed = false;
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		});

		primary.setOnCloseRequest(event -> {
		    if (combatThread != null && combatThread.isAlive()) {
		        combatThread.interrupt();
		    }
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

		this.getChildren().addAll(attackButton, magicButton);

		attackButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 1;
		});

		magicButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 2;
		});
	}

	private void drawScene(String bg) {
		Image img = new Image(ClassLoader.getSystemResource(bg).toString());
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
		use.add(new Pair<>(100, 300));
		use.add(new Pair<>(200, 550));
		use.add(new Pair<>(200, 420));
		use.add(new Pair<>(360, 300));
		return use;
	}

	private ArrayList<Pair<Integer, Integer>> setupEnemy() {
		ArrayList<Pair<Integer, Integer>> enemyPos = new ArrayList<>();
		enemyPos.add(new Pair<>(750, 530));
		enemyPos.add(new Pair<>(500, 540));
		enemyPos.add(new Pair<>(600, 350));
		enemyPos.add(new Pair<>(700, 400));
		return enemyPos;
	}

	private void drawPlayer() {
		ArrayList<Pair<Integer, Integer>> pos = setupPlayer();
		int index = 0;
		for (GameCharacter player : players) {

			HBox f1 = new HBox();
			Text first = new Text(player.getClass().getSimpleName() + " " + " HP: " + player.getHp() + " " + " MP: "
					+ player.getMana());
			first.setFont(Font.font(20));

			f1.getChildren().add(first);

			f1.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

			setPos(pos, index, f1, player);

			Image imgchar = new Image(player.getSelf());
			ImageView rep = new ImageView(imgchar);

			rep.setFitWidth(120);
			rep.setFitHeight(120);

			setImagePos(pos, index, rep, player);

			player.setPos(rep.getLayoutX(), rep.getLayoutY());

			player.setImg(rep);

			pane.getChildren().addAll(f1, rep);

			index++;
		}
	}

	public void drawEnemy() {
		ArrayList<Pair<Integer, Integer>> enemyPos = setupEnemy();
		int countS = 0;
		for (Enemy enemy : enemies) {

			HBox f2 = new HBox();
			Text second = new Text("Enemy HP: " + enemy.getHp());
			second.setFont(Font.font(20));

			f2.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

			f2.getChildren().add(second);

			setPos(enemyPos, countS, f2, enemy);

			Image imgchar = new Image(ClassLoader.getSystemResource("up_2.png").toString());
			ImageView rep = new ImageView(imgchar);

			rep.setFitWidth(88);
			rep.setFitHeight(88);

			setImagePos(enemyPos, countS, rep, enemy);

			enemy.setImg(rep);

			final int c = countS;
			rep.setOnMouseClicked(event -> {
				if (pressed)
					choice = c;
			});

			enemy.setPos(rep.getLayoutX(), rep.getLayoutY());
			pane.getChildren().addAll(f2, rep);
			countS++;
		}
	}

	public static void moveToAttack(double deltaX, double deltaY, ImageView img) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.75), img);
		transition.setByX(deltaX);
		transition.setByY(deltaY);
		transition.play();
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void magicMove(ImageView view) {
		TranslateTransition shakeTransition = new TranslateTransition(Duration.seconds(0.25), view);
		shakeTransition.setByX(10);
		shakeTransition.setAutoReverse(true);
		shakeTransition.play();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setPos(ArrayList<Pair<Integer, Integer>> Pos, int count, HBox second, GameCharacter current) {
		if (current.stillDefault()) {
			second.setLayoutX(Pos.get(count).getKey());
			second.setLayoutY(Pos.get(count).getValue());
			current.setOriginalX(Pos.get(count).getKey());
			current.setOriginalY(Pos.get(count).getValue());
		} else {
			second.setLayoutX(current.getOriginalX());
			second.setLayoutY(current.getOriginalY());
		}
	}

	private void setImagePos(ArrayList<Pair<Integer, Integer>> Pos, int count, ImageView rep, GameCharacter enemy) {
		if (enemy.stillDefault()) {
			rep.setLayoutX(Pos.get(count).getKey());
			rep.setLayoutY(Pos.get(count).getValue() + 20);
		} else {
			rep.setLayoutX(enemy.getOriginalX());
			rep.setLayoutY(enemy.getOriginalY() + 20);
		}
	}

	public static void playerAction(int damageAction, GameCharacter current, GameCharacter selectedEnemy) {
		if (damageAction == 1) {
			double deltaX = selectedEnemy.getPosX() - current.getPosX();
			double deltaY = selectedEnemy.getPosY() - current.getPosY();
			moveToAttack(deltaX, deltaY, current.getImg());
			magicMove(selectedEnemy.getImg());
			current.attack(selectedEnemy);
		} else if (damageAction == 2) {
			magicMove(selectedEnemy.getImg());
			current.magic(selectedEnemy);
		}
	}
}