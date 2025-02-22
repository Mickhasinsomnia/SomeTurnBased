package grid;

import character.*;
import character.Enemy;
import javafx.animation.TranslateTransition;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

import application.Main;

public class FightScene extends Pane {

	private ArrayList<GameCharacter> players;
	private ArrayList<Enemy> enemies;
	private Pane pane;
	private boolean pressed;
	private int choice;
	private int type;
	private Stage primary;
	private final Object dummy = new Object();
//	private MediaPlayer p;

	public FightScene(ArrayList<GameCharacter> players, ArrayList<Enemy> enemies, Stage primary, String bg) {
		this.players = players;
		this.enemies = enemies;
		this.primary = primary;
		pane = new Pane();
		pane.setMinSize(700, 700);
		this.getChildren().add(pane);
		pressed = false;

		drawScene(bg);

		setButton();

		Thread combatThread = new Thread(() -> {
//			Media sound = new Media(ClassLoader.getSystemResource("sound.mp3").toString());
//			p = new MediaPlayer(sound);
//			p.play();
			while (players.size() > 0 && enemies.size() > 0) {
				ArrayList<GameCharacter> all = new ArrayList<>();
				all.addAll(players);
				all.addAll(enemies);
				Collections.sort(all);
				Circle onHead = new Circle(20);

				for (GameCharacter current : all) {
					if (current.getHp() <= 0)
						continue;
					if (current instanceof Enemy) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Enemy enemy = (Enemy) current;

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
//							p.stop();
							Platform.runLater(() -> {
								primary.getScene().setRoot(Main.menu);
							});
						}
						Platform.runLater(() -> {
							onHead.setLayoutX(current.getPosX() - 20);
							onHead.setLayoutY(current.getPosY() + 60);
							this.getChildren().add(onHead);
						});
						synchronized (dummy) { 
						    while (!pressed) {
						        try {
						            dummy.wait(100);
						        } catch (InterruptedException e) {
						            e.printStackTrace();
						        }
						    }

						    while (choice == -1) {
						        try {
						            dummy.wait(100); 
						        } catch (InterruptedException e) {
						            e.printStackTrace();
						        }
						    }
						}

						Enemy selectedEnemy = enemies.get(choice);
						int damageAction = type;
						
						if (damageAction == 1) {
						    double deltaX = selectedEnemy.getPosX() - current.getPosX();
						    double deltaY = selectedEnemy.getPosY()-current.getPosY();
						    moveToAttack(deltaX, deltaY,current.getImg());
						    current.attack(selectedEnemy);
						}
						
						else if (damageAction == 2) {
							magicMove(selectedEnemy.getImg());
						    current.magic(selectedEnemy);
						}
						
						Platform.runLater(() -> {
							this.getChildren().remove(onHead);
						});
					}
					checkForDeadCharacters(players, enemies);
					Platform.runLater(() -> drawScene(bg));
					pressed = false;
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Platform.runLater(() -> {
				primary.getScene().setRoot(Main.menu);
			});
		});
		combatThread.start();
	}

	private void setButton() {
		Button attackButton = new Button("Attack");
		Button magicButton = new Button("Magic");

		Button returnMenu = new Button("return to menu");

		attackButton.setLayoutX(700);
		attackButton.setLayoutY(650);
		magicButton.setLayoutX(800);
		magicButton.setLayoutY(650);
		returnMenu.setLayoutX(500);
		returnMenu.setLayoutY(650);

		magicButton.setMinSize(60, 40);
		attackButton.setMinSize(60, 40);
		returnMenu.setMinSize(60, 40);

		this.getChildren().addAll(attackButton, magicButton, returnMenu);

		attackButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 1;
		});

		magicButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 2;
		});

		returnMenu.setOnMouseClicked(event -> {
//			p.stop();
			Platform.runLater(() -> {
				primary.getScene().setRoot(Main.menu);
			});
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
			rep.setLayoutY(pos.get(index).getValue() + 20);

			player.setPos(rep.getLayoutX(), rep.getLayoutY());
			
			player.setImg(rep);
			
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
			
			enemy.setImg(rep);

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
	
	private void moveToAttack(double deltaX,double deltaY,ImageView img) {
	    TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), img);
	    transition.setByX(deltaX); 
	    transition.setByY(deltaY);  
	    transition.play();
	    try {
	        Thread.sleep(1500);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	
	private void magicMove(ImageView view) {
		TranslateTransition shakeTransition = new TranslateTransition(Duration.seconds(0.5), view);
	    shakeTransition.setByX(10); 
	    shakeTransition.setAutoReverse(true); 
	    shakeTransition.play();
	    try {
	    	Thread.sleep(1500);
	    }
	    catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
}