package panels;

import java.util.ArrayList;

import character.GameCharacter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javax.sound.sampled.Clip;

public class FightScene extends Pane implements CombatEngine.TurnListener {

	private final ArrayList<GameCharacter> players;
	private final ArrayList<GameCharacter> enemies;
	private final Pane spritePane = new Pane();
	private final String bg;
	private final Stage primary;
	private final int level;

	private final CombatRenderer renderer;
	private final PlayerInputHandler input = new PlayerInputHandler();
	private final StatusPanel playerStatus = new StatusPanel("Player Status", Color.DARKBLUE, 550, 10);
	private final StatusPanel enemyStatus = new StatusPanel("Enemy Status", Color.DARKRED, 700, 10);
	private final CombatEngine engine;

	private GameCharacter mouseSelectPlayerCharacter;
	private GameCharacter mouseSelectEnemyCharacter;
	private Clip musicClip;

	public FightScene(ArrayList<GameCharacter> players, ArrayList<GameCharacter> enemies, Stage primary, String bg,
	                  String song, int level) {
		this.players = players;
		this.enemies = enemies;
		this.primary = primary;
		this.bg = bg;
		this.level = level;

		spritePane.setMinSize(700, 700);
		this.getChildren().add(spritePane);
		this.renderer = new CombatRenderer(this, spritePane);

		mouseSelectPlayerCharacter = players.getFirst();
		mouseSelectEnemyCharacter = enemies.getFirst();

		this.getChildren().addAll(playerStatus.getNode(), enemyStatus.getNode());
		this.getChildren().addAll(input.getAttackButton(), input.getMagicButton());

		this.engine = new CombatEngine(players, enemies, renderer, input, this, level, primary);

		redraw();
		updateStatusPanels();
		setExit();
		playMusic(song);
		engine.start();
	}

	private void redraw() {
		renderer.drawScene(bg, players, enemies, player -> {
			mouseSelectPlayerCharacter = player;
			updateStatusPanels();
		}, enemy -> {
			mouseSelectEnemyCharacter = enemy;
			updateStatusPanels();
		}, input::onEnemySelected);
	}

	private void updateStatusPanels() {
		playerStatus.update(mouseSelectPlayerCharacter);
		enemyStatus.update(mouseSelectEnemyCharacter);
	}

	private void playMusic(String song) {
		musicClip = UtilScene.playAudio(song);
		if (musicClip != null) {
			musicClip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	private void setExit() {
		primary.setOnCloseRequest(event -> {
			engine.stop();
			Platform.exit();
			System.exit(0);
		});
	}

	@Override
	public void onTurnCompleted() {
		Platform.runLater(() -> {
			redraw();
			updateStatusPanels();
			input.resetButtonStyles();
		});
	}

	@Override
	public void onGameEnded(boolean playersWon) {
		if (musicClip != null) {
			musicClip.stop();
		}
		Platform.runLater(() -> showResultScreen(playersWon));
	}

	private void showResultScreen(boolean playersWon) {
		Pane resultPane = new Pane();
		resultPane.setMinSize(900, 700);
		resultPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

		ImageView bgView = new ImageView(new Image(ClassLoader.getSystemResource("waiting2.jpeg").toString()));
		bgView.setFitWidth(900);
		bgView.setFitHeight(700);
		bgView.setPreserveRatio(false);

		String resultImage = playersWon ? "winner.png" : "over.png";
		ImageView resultView = new ImageView(new Image(ClassLoader.getSystemResource(resultImage).toString()));
		resultView.setFitWidth(600);
		resultView.setFitHeight(300);
		resultView.setPreserveRatio(false);
		resultView.setLayoutX(150);
		resultView.setLayoutY(120);

		resultPane.getChildren().addAll(bgView, resultView);

		Clip fanfare = UtilScene.playAudio(playersWon ? "victoryFanfare.wav" : "gameover2.wav");

		Button menuButton = new Button("Back to Menu");
		menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		menuButton.setLayoutX(355);
		menuButton.setLayoutY(400);
		menuButton.setOnAction(e -> {
			if (fanfare != null) {
				fanfare.stop();
			}
			UtilScene.showManage(primary);
		});

		resultPane.getChildren().add(menuButton);
		primary.getScene().setRoot(resultPane);
	}
}
