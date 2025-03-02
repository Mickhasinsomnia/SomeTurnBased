package panels;

import character.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import utilities.SaveManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import application.Main;

public class FightScene extends Pane {

	private ArrayList<GameCharacter> players;
	private ArrayList<GameCharacter> enemies;
	private Pane pane;
	private boolean pressed;
	private int choice;
	private int type;
	private VBox playerStatusPanel;
	private VBox enemyStatusPanel;

	private Button attackButton = new Button("Attack");
	private Button magicButton = new Button("Magic");

	private String defaultButtonStyle = "-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: white; "
			+ "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10px;";
	private String selectButtonStyle = "-fx-background-color: rgba(255, 255, 255, 0.7); -fx-border-color: black; "
			+ "-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px;";

	public FightScene(ArrayList<GameCharacter> players, ArrayList<GameCharacter> enemies, Stage primary, String bg,
			String song, int level) {
		this.players = players;
		this.enemies = enemies;

		pane = new Pane();
		pane.setMinSize(700, 700);
		this.getChildren().add(pane);
		pressed = false;

		drawScene(bg);

		setupStatusPanels();

		updateStatusPanels();

		startCombat(primary, bg, song, level);
	}

	private void startCombat(Stage primary, String bg, String song, int level) {
		Thread combatThread = new Thread(() -> {
			Clip clip = null;
			try {
				AudioInputStream audio = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("sound.wav"));
				clip = AudioSystem.getClip();
				clip.open(audio);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				setButton(primary, clip, Thread.currentThread());

				while (!players.isEmpty() && !enemies.isEmpty() && !Thread.currentThread().isInterrupted()) {
					List<GameCharacter> all = new ArrayList<>(players);
					all.addAll(enemies);
					Collections.sort(all);

					Polygon onHead = pointerPlayer();

					for (GameCharacter current : all) {
						if (current.getHp() <= 0)
							continue;

						if (current instanceof Enemy) {
							enemyTurn(current, level, primary);
						} else {
							choice = -1;
							if (!(current instanceof Priest)) {
								Platform.runLater(() -> setOnHead(onHead, current));

								while (!pressed || choice == -1) {
									try {
										Thread.sleep(100);
										if (Thread.currentThread().isInterrupted())
											return;
									} catch (InterruptedException e) {
										Thread.currentThread().interrupt();
										return;
									}
								}

								Enemy selectedEnemy = (Enemy) enemies.get(choice);
								if (!enemies.contains(selectedEnemy))
									continue;

								playerAction(type, current, selectedEnemy, this);
								Platform.runLater(() -> this.getChildren().remove(onHead));

							} else {
								Thread.sleep(1500);
								autoHeal(current, players);
							}
						}

						checkForDeadCharacters(players, enemies);
						if (players.isEmpty() || enemies.isEmpty()) {
							setGameEnd(level, primary, clip, Thread.currentThread());
							return;
						}

						Platform.runLater(() -> {
							drawScene(bg);
							updateStatusPanels();
							pressed = false;
							attackButton.setStyle(defaultButtonStyle);
							magicButton.setStyle(defaultButtonStyle);
						});
					}
				}
				setGameEnd(level, primary, clip, Thread.currentThread());

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (clip != null) {
					clip.stop();
					clip.close();
				}
			}
		});

		setExit(primary, combatThread);
		combatThread.start();
	}

	private void setButton(Stage primary, Clip clip, Thread combatThread) {
		Button back = new Button("Retreat");

		attackButton.setLayoutX(15);
		attackButton.setLayoutY(80);
		magicButton.setLayoutX(143);
		magicButton.setLayoutY(80);
		back.setLayoutX(15);
		back.setLayoutY(163);

		attackButton.setMinSize(120, 75);
		magicButton.setMinSize(120, 75);
		back.setMinSize(248, 40);

		attackButton.setStyle(defaultButtonStyle);
		magicButton.setStyle(defaultButtonStyle);
		back.setStyle(defaultButtonStyle);

		Platform.runLater(() -> {
			this.getChildren().addAll(attackButton, magicButton, back);
		});

		back.setOnMouseClicked(event -> {
			if (combatThread != null && combatThread.isAlive()) {
				combatThread.interrupt();
				try {
					combatThread.join(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			clip.stop();
			Platform.runLater(() -> UtilScene.showLevelSelect(primary));
		});

		attackButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 1;
			attackButton.setStyle(selectButtonStyle);
			magicButton.setStyle(defaultButtonStyle);
		});

		magicButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 2;
			attackButton.setStyle(defaultButtonStyle);
			magicButton.setStyle(selectButtonStyle);
		});

	}

	private void setExit(Stage primary, Thread combatThread) {
		primary.setOnCloseRequest(event -> {
			if (combatThread != null && combatThread.isAlive()) {
				combatThread.interrupt();
			}
			return;
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

	private void checkForDeadCharacters(ArrayList<GameCharacter> teamA, ArrayList<GameCharacter> teamB) {
		teamA.removeIf(character -> character.getHp() <= 0);
		teamB.removeIf(enemy -> enemy.getHp() <= 0);
	}

	private ArrayList<Pair<Integer, Integer>> setupPlayer() {
		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(100, 300));
		use.add(new Pair<>(200, 550));
		use.add(new Pair<>(50, 420));
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

			Image imgchar = new Image(player.getImagePath());
			ImageView rep = new ImageView(imgchar);

			rep.setFitWidth(120);
			rep.setFitHeight(120);

			setImagePos(pos, index, rep, player);

			player.setPos(rep.getLayoutX(), rep.getLayoutY());

			player.setImg(rep);

			pane.getChildren().addAll(rep);

			index++;
		}
	}

	private void setOnHead(Polygon onHead, GameCharacter current) {
		onHead.setLayoutX(current.getPos().getKey() - 20);
		onHead.setLayoutY(current.getPos().getValue() + 60);
		this.getChildren().add(onHead);
	}

	private void drawEnemy() {
		ArrayList<Pair<Integer, Integer>> enemyPos = setupEnemy();
		int countS = 0;
		for (GameCharacter enemy : enemies) {

			Image imgchar = new Image(enemy.getImagePath());
			ImageView rep = new ImageView(imgchar);

			rep.setFitWidth(120);
			rep.setFitHeight(120);

			setImagePos(enemyPos, countS, rep, enemy);

			enemy.setImg(rep);

			final int c = countS;
			rep.setOnMouseClicked(event -> {
				if (pressed) {
					choice = c;
					attackButton.setStyle(defaultButtonStyle);
					magicButton.setStyle(defaultButtonStyle);
				}
			});

			enemy.setPos(rep.getLayoutX(), rep.getLayoutY());
			pane.getChildren().addAll(rep);
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

		}
	}

	public static void setShake(ImageView img) {
		TranslateTransition shakeTransition = new TranslateTransition(Duration.seconds(0.25), img);
		shakeTransition.setByX(10);
		shakeTransition.setAutoReverse(true);
		shakeTransition.play();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {

		}
	}

	private void setImagePos(ArrayList<Pair<Integer, Integer>> Pos, int count, ImageView rep, GameCharacter enemy) {
		if (enemy.stillDefault()) {
			rep.setLayoutX(Pos.get(count).getKey());
			rep.setLayoutY(Pos.get(count).getValue() + 20);
			enemy.setOriginalPos(Pos.get(count).getKey(), Pos.get(count).getValue());
		} else {
			rep.setLayoutX(enemy.getOriginalX());
			rep.setLayoutY(enemy.getOriginalY() + 20);
		}
	}

	private void enemyTurn(GameCharacter current, int level, Stage primary) {
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {

		}

		if (!(current instanceof EnemyHealer)) {

			Enemy enemy = (Enemy) current;

			ArrayList<GameCharacter> alivePlayer = new ArrayList<>();
			for (GameCharacter alive : players) {
				if (alive.getHp() > 0)
					alivePlayer.add(alive);
			}

			if (!alivePlayer.isEmpty()) {

				enemy.chooseTarget(alivePlayer, this);
			} else {

				if (players.size() > enemies.size()) {
					updateLevel(level);
				}
				UtilScene.showManage(primary);
			}
		} else {
			autoHeal(current, enemies);
		}
	}

	public static void playerAction(int damageAction, GameCharacter current, GameCharacter selectedEnemy, Pane pane) {

		if (damageAction == 1) {
			double deltaX = selectedEnemy.getPos().getKey() - current.getPos().getKey();
			double deltaY = selectedEnemy.getPos().getValue() - current.getPos().getValue();
			if (!(current instanceof RangeCharacter)) {
				moveToAttack(deltaX, deltaY, current.getImg());
			} else {
				projectileShoot(current, deltaX, deltaY, pane);
			}
			setShake(selectedEnemy.getImg());
			current.attack(selectedEnemy);
		} else if (damageAction == 2) {
			setShake(selectedEnemy.getImg());
			current.magic(selectedEnemy);
		}
	}

	private void autoHeal(GameCharacter current, ArrayList<GameCharacter> allies) {
		int min = 0;
		for (int i = 1; i < allies.size(); i++) {
			if (allies.get(i).getHp() < allies.get(min).getHp() && allies.get(i).getHp() > 0) {
				min = i;
			}
		}
		setShake(current.getImg());
		setShake(allies.get(min).getImg());
		allies.get(min).setHp(Math.min(allies.get(min).getHp() + current.getMagic(), allies.get(min).getMaxhp()));
	}

	private void updateStatusPanels() {
		playerStatusPanel.getChildren().clear();
		enemyStatusPanel.getChildren().clear();

		Label playerTitle = new Label("Player Status");
		playerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		playerTitle.setTextFill(Color.DARKBLUE);

		Label enemyTitle = new Label("Enemy Status");
		enemyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		enemyTitle.setTextFill(Color.DARKRED);

		playerStatusPanel.getChildren().add(playerTitle);
		enemyStatusPanel.getChildren().add(enemyTitle);

		for (GameCharacter player : players) {
			Label playerInfo = new Label(
					player.getClass().getSimpleName() + " | HP: " + player.getHp() + " | MP: " + player.getMana());
			playerInfo.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
			playerInfo.setTextFill(Color.DARKBLUE);
			playerStatusPanel.getChildren().add(playerInfo);
		}

		for (GameCharacter enemy : enemies) {
			Label enemyInfo = new Label(
					enemy.getClass().getSimpleName() + " | HP: " + enemy.getHp() + " | MP: " + enemy.getMana());
			enemyInfo.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
			enemyInfo.setTextFill(Color.DARKRED);
			enemyStatusPanel.getChildren().add(enemyInfo);
		}
	}

	private void setupStatusPanels() {
		playerStatusPanel = new VBox(10);
		playerStatusPanel.setPadding(new Insets(10));
		playerStatusPanel.setLayoutX(650);
		playerStatusPanel.setLayoutY(10);
		playerStatusPanel
				.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY)));
		playerStatusPanel.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

		enemyStatusPanel = new VBox(10);
		enemyStatusPanel.setPadding(new Insets(10));
		enemyStatusPanel.setLayoutX(650);
		enemyStatusPanel.setLayoutY(200);
		enemyStatusPanel
				.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY)));
		enemyStatusPanel.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

		this.getChildren().addAll(playerStatusPanel, enemyStatusPanel);
	}

	private void updateLevel(int level) {
		if (!Main.finished.get(level)) {
			SaveManager.saveUnlock(SaveManager.loadUnlock() + 1);
			Main.unlock += 1;
			Main.finished.set(level, true);
		} else {
			return;
		}
	}

	private static void projectileShoot(GameCharacter current, double deltaX, double deltaY, Pane pane) {
		RangeCharacter archer = (RangeCharacter) current;
		ImageView arrow = new ImageView(new Image(archer.getWeapon()));
		arrow.setFitHeight(80);
		arrow.setFitWidth(80);
		if (!(current instanceof Enemy)) {
			arrow.setLayoutX(current.getPos().getKey() + 60);
			arrow.setLayoutY(current.getPos().getValue() + 40);
		} else {
			arrow.setLayoutX(current.getPos().getKey() - 60);
			arrow.setLayoutY(current.getPos().getValue() + 40);
		}
		Platform.runLater(() -> {
			pane.getChildren().add(arrow);
		});
		moveToAttack(deltaX, deltaY - 50, arrow);
		Platform.runLater(() -> {
			pane.getChildren().remove(arrow);
		});
	}

	private Polygon pointerPlayer() {
		Polygon onHead = new Polygon(-20, -6, 6, -6, 6, -20, 20, 0, 6, 20, 6, 6, -20, 6);
		onHead.setFill(Color.ORANGE);
		onHead.setStroke(Color.BLACK);
		onHead.setStrokeWidth(2);
		return onHead;
	}

	private void setGameEnd(int level, Stage primary, Clip clip, Thread combatThread) {

		if (combatThread != null && combatThread.isAlive()) {
			combatThread.interrupt();
		}

		clip.stop();
		if (players.size() > enemies.size()) {
			updateLevel(level);
		}

		Pane resultPane = new Pane();
		resultPane.setMinSize(900, 700);
		resultPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

		Label resultLabel = new Label(players.isEmpty() ? "You Lost!" : "You Won!");
		resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
		resultLabel.setTextFill(Color.WHITE);
		resultLabel.setLayoutX(340);
		resultLabel.setLayoutY(250);
		
		Clip fanfare=null;

		if (!players.isEmpty()) {
			try {
				AudioInputStream audio = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("victoryFanfare.wav"));
				fanfare = AudioSystem.getClip();
				fanfare.open(audio);
				fanfare.start();
			} catch (Exception e) {
				System.out.println("No song");
				fanfare=null;
			}

		}

		final Clip finalFanfare = fanfare;
		
		Button menuButton = new Button("Back to Menu");
		menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		menuButton.setLayoutX(355);
		menuButton.setLayoutY(400);
		menuButton.setOnAction(e -> {
			Platform.runLater(() -> {
				finalFanfare.stop();
				UtilScene.showManage(primary);
			});
		});

		Platform.runLater(() -> {
			resultPane.getChildren().addAll(resultLabel, menuButton);
			primary.getScene().setRoot(resultPane);
		});

	}

}