package panels;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import character.Enemy;
import character.GameCharacter;
import character.Grog;
import character.RangeCharacter;
import character.Xande;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * Pure rendering: draws sprites, plays animations, shows hit/heal effects.
 * Knows nothing about turn order or who's allowed to attack whom - it just
 * draws what it's told and reports back mouse interactions via callbacks.
 */
public class CombatRenderer {

	private final Pane root;
	private final Pane spritePane;

	public CombatRenderer(Pane root, Pane spritePane) {
		this.root = root;
		this.spritePane = spritePane;
	}

	public void drawScene(String bg, ArrayList<GameCharacter> players, ArrayList<GameCharacter> enemies,
			Consumer<GameCharacter> onPlayerHover, Consumer<GameCharacter> onEnemyHover, IntConsumer onEnemyClicked) {

		Image img = new Image(ClassLoader.getSystemResource(bg).toString());
		BackgroundImage bgImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		root.setBackground(new Background(bgImg));

		spritePane.getChildren().clear();
		drawPlayers(players, onPlayerHover);
		drawEnemies(enemies, onEnemyHover, onEnemyClicked);
	}

	private ArrayList<Pair<Integer, Integer>> playerPositions() {
		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(100, 300));
		use.add(new Pair<>(200, 550));
		use.add(new Pair<>(50, 420));
		use.add(new Pair<>(320, 300));
		return use;
	}

	private ArrayList<Pair<Integer, Integer>> enemyPositions() {
		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(600, 310));
		use.add(new Pair<>(500, 500));
		use.add(new Pair<>(750, 380));
		use.add(new Pair<>(659, 450));
		return use;
	}

	private void drawPlayers(ArrayList<GameCharacter> players, Consumer<GameCharacter> onHover) {
		ArrayList<Pair<Integer, Integer>> pos = playerPositions();
		int index = 0;
		for (GameCharacter player : players) {
			Image imgchar = new Image(player.getImagePath());
			ImageView rep = new ImageView(imgchar);
			rep.setFitWidth(120);
			rep.setFitHeight(120);

			setImagePos(pos, index, rep, player);

			rep.setOnMouseEntered(e -> onHover.accept(player));

			player.setPos(rep.getLayoutX(), rep.getLayoutY());
			player.setImg(rep);
			spritePane.getChildren().add(rep);
			index++;
		}
	}

	private void drawEnemies(ArrayList<GameCharacter> enemies, Consumer<GameCharacter> onHover,
			IntConsumer onEnemyClicked) {
		ArrayList<Pair<Integer, Integer>> pos = enemyPositions();
		int index = 0;
		for (GameCharacter enemy : enemies) {
			Image imgchar = new Image(enemy.getImagePath());
			ImageView rep = new ImageView(imgchar);
			rep.setFitWidth(180);
			rep.setFitHeight(180);

			if (enemy instanceof Grog || enemy instanceof Xande) {
				rep.setFitWidth(250);
				rep.setFitHeight(250);
			}

			rep.setOnMouseEntered(e -> onHover.accept(enemy));

			setImagePos(pos, index, rep, enemy);
			enemy.setImg(rep);

			final int c = index;
			rep.setOnMouseClicked(event -> onEnemyClicked.accept(c));

			enemy.setPos(rep.getLayoutX(), rep.getLayoutY());
			spritePane.getChildren().add(rep);
			index++;
		}
	}

	private void setImagePos(ArrayList<Pair<Integer, Integer>> pos, int count, ImageView rep, GameCharacter c) {
		if (c.stillDefault()) {
			rep.setLayoutX(pos.get(count).getKey());
			rep.setLayoutY(pos.get(count).getValue() + 20);
			c.setOriginalPos(pos.get(count).getKey(), pos.get(count).getValue());
		} else {
			rep.setLayoutX(c.getOriginalPos().getKey());
			rep.setLayoutY(c.getOriginalPos().getValue() + 20);
		}
	}

	public Polygon createTurnPointer() {
		Polygon onHead = new Polygon(-20, -6, 6, -6, 6, -20, 20, 0, 6, 20, 6, 6, -20, 6);
		onHead.setFill(Color.ORANGE);
		onHead.setStroke(Color.BLACK);
		onHead.setStrokeWidth(2);
		return onHead;
	}

	public void showTurnPointer(Polygon onHead, GameCharacter current) {
		onHead.setLayoutX(current.getPos().getKey() - 20);
		onHead.setLayoutY(current.getPos().getValue() + 60);
		Platform.runLater(() -> root.getChildren().add(onHead));
	}

	public void hideTurnPointer(Polygon onHead) {
		Platform.runLater(() -> root.getChildren().remove(onHead));
	}

	public void playMeleeAttack(GameCharacter current, GameCharacter target) {
		double deltaX = target.getPos().getKey() - current.getPos().getKey();
		double deltaY = target.getPos().getValue() - current.getPos().getValue();
		moveToAttack(deltaX, deltaY, current.getImg());
		setShake(target.getImg());
	}

	public void playRangedAttack(GameCharacter current, GameCharacter target) {
		double deltaX = target.getPos().getKey() - current.getPos().getKey();
		double deltaY = target.getPos().getValue() - current.getPos().getValue();
		projectileShoot(current, deltaX, deltaY);
		setShake(target.getImg());
	}

	public void playAreaEffect(ArrayList<GameCharacter> targets, Color color, String soundEffect) {
		for (GameCharacter t : targets) {
			Platform.runLater(() -> {
				showGroundEffect(t, color);
				UtilScene.playAudio(soundEffect);
			});
			setShake(t.getImg());
		}
	}

	private void moveToAttack(double deltaX, double deltaY, ImageView img) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.75), img);
		transition.setByX(deltaX);
		transition.setByY(deltaY);
		transition.play();
		sleep(750);
	}

	private void setShake(ImageView img) {
		TranslateTransition shakeTransition = new TranslateTransition(Duration.seconds(0.25), img);
		shakeTransition.setByX(10);
		shakeTransition.setAutoReverse(true);
		shakeTransition.play();
		sleep(250);
	}

	private void projectileShoot(GameCharacter current, double deltaX, double deltaY) {
		if (!(current instanceof RangeCharacter)) {
			return;
		}
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
		Platform.runLater(() -> spritePane.getChildren().add(arrow));
		moveToAttack(deltaX, deltaY - 50, arrow);
		Platform.runLater(() -> spritePane.getChildren().remove(arrow));
	}

	private void showGroundEffect(GameCharacter target, Color color) {
		Circle effect = new Circle();
		effect.setRadius(10);
		effect.setFill(new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, color),
				new Stop(1, Color.TRANSPARENT)));
		effect.setLayoutX(target.getPos().getKey() + target.getImg().getFitWidth() / 2);
		effect.setLayoutY(target.getPos().getValue() + target.getImg().getFitHeight());

		spritePane.getChildren().add(effect);

		ScaleTransition expand = new ScaleTransition(Duration.millis(300), effect);
		expand.setToX(5);
		expand.setToY(5);

		FadeTransition fade = new FadeTransition(Duration.millis(300), effect);
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		fade.setOnFinished(e -> spritePane.getChildren().remove(effect));

		new ParallelTransition(expand, fade).play();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
