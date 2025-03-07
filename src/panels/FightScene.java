package panels;

import character.*;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
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
	private GameCharacter mouseSelectPlayerCharacter;
	private GameCharacter mouseSelectEnemyCharacter;

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

		mouseSelectPlayerCharacter = players.getFirst();
		mouseSelectEnemyCharacter = enemies.getFirst();
		drawScene(bg);

		setupStatusPanels();

		updateStatusPanels();

		startCombat(primary, bg, song, level);
	}

	private void startCombat(Stage primary, String bg, String song, int level) {
		Thread combatThread = new Thread(() -> {
			Clip clip = null;
			try {
				clip = UtilScene.playAudio(song);
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
							mouseSelectEnemyCharacter = current;
						} else {
							choice = -1;
							if (!(current instanceof Priest)) {
								Platform.runLater(() -> setOnHead(onHead, current));
								mouseSelectPlayerCharacter = current;
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

								executeTurnAction(type, current, selectedEnemy, this, enemies);
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

		attackButton.setLayoutX(15);
		attackButton.setLayoutY(80);
		magicButton.setLayoutX(143);
		magicButton.setLayoutY(80);

		attackButton.setMinSize(120, 75);
		magicButton.setMinSize(120, 75);

		attackButton.setStyle(defaultButtonStyle);
		magicButton.setStyle(defaultButtonStyle);

		Platform.runLater(() -> {
			this.getChildren().addAll(attackButton, magicButton);
		});

		attackButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 1;
			UtilScene.playAudio("click.wav");
			attackButton.setStyle(selectButtonStyle);
			magicButton.setStyle(defaultButtonStyle);
		});

		magicButton.setOnMouseClicked(event -> {
			pressed = true;
			type = 2;
			UtilScene.playAudio("click.wav");
			attackButton.setStyle(defaultButtonStyle);
			magicButton.setStyle(selectButtonStyle);
		});

	}

	private void setExit(Stage primary, Thread combatThread) {
		primary.setOnCloseRequest(event -> {
			if (combatThread != null && combatThread.isAlive()) {
				combatThread.interrupt();
			}
			Platform.exit();
			System.exit(0);
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
		use.add(new Pair<>(320, 300));
		return use;
	}

	private ArrayList<Pair<Integer, Integer>> setupEnemy() {
		ArrayList<Pair<Integer, Integer>> enemyPos = new ArrayList<>();
		enemyPos.add(new Pair<>(600, 310));
		enemyPos.add(new Pair<>(500, 500));
		enemyPos.add(new Pair<>(750, 380));
		enemyPos.add(new Pair<>(659, 450));
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

			rep.setOnMouseEntered(e -> {
				mouseSelectPlayerCharacter = player;
				updateStatusPanels();
			});

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

			rep.setFitWidth(180);
			rep.setFitHeight(180);

			if (enemy instanceof Grog || enemy instanceof Xande) {
				rep.setFitWidth(250);
				rep.setFitHeight(250);
			}

			rep.setOnMouseEntered(e -> {
				mouseSelectEnemyCharacter = enemy;
				updateStatusPanels();
			});

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

	private static void moveToAttack(double deltaX, double deltaY, ImageView img) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.75), img);
		transition.setByX(deltaX);
		transition.setByY(deltaY);
		transition.play();
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {

		}
	}

	private static void setShake(ImageView img) {
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
			rep.setLayoutX(enemy.getOriginalPos().getKey());
			rep.setLayoutY(enemy.getOriginalPos().getValue() + 20);
		}
	}

	private void enemyTurn(GameCharacter current, int level, Stage primary) {
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {

		}

		if (!(current instanceof Necromancer)) {

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

	public static void executeTurnAction(int damageAction, GameCharacter current, GameCharacter selectedEnemy,
			Pane pane, ArrayList<GameCharacter> target) {

		if (current.getMana() <= 0)
			damageAction = 1;

		if (damageAction == 1) {
			double deltaX = selectedEnemy.getPos().getKey() - current.getPos().getKey();
			double deltaY = selectedEnemy.getPos().getValue() - current.getPos().getValue();

			UtilScene.playAudio(current.getSoundEffect());

			if (!(current instanceof RangeCharacter)) {
				moveToAttack(deltaX, deltaY, current.getImg());
			} else {
				projectileShoot(current, deltaX, deltaY, pane);
			}
			setShake(selectedEnemy.getImg());
			current.attack(selectedEnemy);
		} else if (damageAction == 2) {
			if (current.getMana() <= 0)
				return;
			for (GameCharacter all : target) {
				Platform.runLater(() -> {
					showGroundEffect(pane, all, Color.RED);
					UtilScene.playAudio("magic2.wav");

				});
				setShake(all.getImg());
			}
			current.magic(target);
		}
	}

	private void autoHeal(GameCharacter current, ArrayList<GameCharacter> allies) {
		if (current.getMana() <= 0)
			return;

		for (int i = 0; i < allies.size(); i++) {
			{
				setShake(allies.get(i).getImg());

				int temp = i;
				Platform.runLater(() -> {
					showGroundEffect(pane, allies.get(temp), Color.GREEN);
					UtilScene.playAudio("heal.wav");
				});
				setShake(allies.get(i).getImg());

				allies.get(i).setHp(Math.min(allies.get(i).getHp() + current.getMagic(), allies.get(i).getMaxHp()));
			}
		}
		current.setMana(current.getMana() - current.getManaCost());
	}

	private void updateStatusPanels() {
		playerStatusPanel.getChildren().clear();
		enemyStatusPanel.getChildren().clear();

		playerStatusPanel.getChildren().add(createTitleLabel("Player Status", Color.DARKBLUE));
		enemyStatusPanel.getChildren().add(createTitleLabel("Enemy Status", Color.DARKRED));

		Label[] playerInfo = createInfoLabels(mouseSelectPlayerCharacter, Color.DARKBLUE);
		Label[] enemyInfo = createInfoLabels(mouseSelectEnemyCharacter, Color.DARKRED);

		playerStatusPanel.getChildren().addAll(playerInfo);
		enemyStatusPanel.getChildren().addAll(enemyInfo);
	}

	private Label createTitleLabel(String title, Color color) {
		Label titleLabel = new Label(title);
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		titleLabel.setTextFill(color);
		return titleLabel;
	}

	private Label[] createInfoLabels(GameCharacter character, Color color) {
		Font statLabelFont = Font.font("Verdana", FontWeight.NORMAL, 14);
		Label[] infoLabels = { new Label(), new Label(), new Label(), new Label(), new Label(), new Label() };

		String[] statValues = { character.getClass().getSimpleName(), "HP: " + character.getHp(),
				"MP: " + character.getMana(), "ATK: " + character.getAttack(), "MAGIC: " + character.getMagic(),
				"SPEED: " + character.getSpeed() };

		for (int i = 0; i < infoLabels.length; i++) {
			infoLabels[i].setFont(statLabelFont);
			infoLabels[i].setTextFill(color);
			infoLabels[i].setText(statValues[i]);
		}

		return infoLabels;
	}

	private void setupStatusPanels() {
		playerStatusPanel = new VBox(10);
		playerStatusPanel.setPadding(new Insets(10));
		playerStatusPanel.setLayoutX(550);
		playerStatusPanel.setLayoutY(10);
		playerStatusPanel
				.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY)));
		playerStatusPanel.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

		enemyStatusPanel = new VBox(10);
		enemyStatusPanel.setPadding(new Insets(10));
		enemyStatusPanel.setLayoutX(700);
		enemyStatusPanel.setLayoutY(10);
		enemyStatusPanel
				.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY)));
		enemyStatusPanel.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

		this.getChildren().addAll(playerStatusPanel, enemyStatusPanel);
	}

	private void updateLevel(int level) {
		if (!Main.finished.get(level) && level < 5) {
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
		arrow.setFitHeight(100);
		arrow.setFitWidth(100);
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
		Image BG = new Image(ClassLoader.getSystemResource("waiting2.jpeg").toString());

		ImageView BGView = new ImageView(BG);
		BGView.setFitWidth(900);
		BGView.setFitHeight(700);
		BGView.setPreserveRatio(false);

		ImageView winner = new ImageView(new Image(ClassLoader.getSystemResource("winner.png").toString()));
		ImageView overr = new ImageView(new Image(ClassLoader.getSystemResource("over.png").toString()));

		for (ImageView img : new ImageView[] { winner, overr }) {
			img.setFitWidth(600);
			img.setFitHeight(300);
			img.setPreserveRatio(false);
			img.setLayoutX(150);
			img.setLayoutY(120);
		}
		resultPane.getChildren().add(BGView);
		resultPane.getChildren().add(players.isEmpty() ? overr : winner);

		Clip fanfare = null;
		try {
			fanfare = UtilScene.playAudio(players.isEmpty() ? "gameover2.wav" : "victoryFanfare.wav");
		} catch (Exception e) {
			fanfare = null;
		}

		final Clip finalFanfare = fanfare;

		Button menuButton = new Button("Back to Menu");
		menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		menuButton.setLayoutX(355);
		menuButton.setLayoutY(400);
		menuButton.setOnAction(e -> {
			Platform.runLater(() -> {
				if (finalFanfare != null) {
					finalFanfare.stop();
				}
				UtilScene.showManage(primary);
			});
		});
		Platform.runLater(() -> {
			resultPane.getChildren().addAll(menuButton);
			primary.getScene().setRoot(resultPane);
		});

	}

	private static void showGroundEffect(Pane pane, GameCharacter target, Color color) {
		Circle effect = new Circle();
		effect.setRadius(10);
		effect.setFill(new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, color),
				new Stop(1, Color.TRANSPARENT)));

		effect.setLayoutX(target.getPos().getKey() + target.getImg().getFitWidth() / 2);
		effect.setLayoutY(target.getPos().getValue() + target.getImg().getFitHeight());

		pane.getChildren().add(effect);

		ScaleTransition expand = new ScaleTransition(Duration.millis(300), effect);
		expand.setToX(5);
		expand.setToY(5);

		FadeTransition fade = new FadeTransition(Duration.millis(300), effect);
		fade.setFromValue(1.0);
		fade.setToValue(0.0);

		fade.setOnFinished(e -> pane.getChildren().remove(effect));

		ParallelTransition animation = new ParallelTransition(expand, fade);
		animation.play();
	}

}