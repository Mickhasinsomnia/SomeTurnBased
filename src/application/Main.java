package application;

import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.Clip;
import javafx.application.Application;
import javafx.stage.Stage;
import panels.UtilScene;
import utilities.MenuButtonManager;
import utilities.SaveManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Main extends Application {

	public static int unlock;
	public static ArrayList<Boolean> finished = new ArrayList<>(Collections.nCopies(6, false));
	public static Clip clip;

	@Override
	public void start(Stage primaryStage) {
		unlock = SaveManager.loadUnlock();
		for (int i = 1; i <= unlock - 1; i++)
			finished.set(i, true);

		showTitleScreen(primaryStage);

		primaryStage.setTitle("Game Title");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private static void playBackgroundMusic() {
		if (clip != null && clip.isRunning()) {
			return;
		}
		clip = UtilScene.playAudio("menu.wav");
	}

	public static void showTitleScreen(Stage primaryStage) {
		ImageView titleImage = new ImageView(new Image(ClassLoader.getSystemResource("title7.png").toString()));
		titleImage.setFitWidth(700);
		titleImage.setPreserveRatio(true);

		Button newGame = MenuButtonManager.newgameButton(primaryStage);
		Button startButton = MenuButtonManager.loadgameButton(primaryStage);
		Button exitButton = MenuButtonManager.exitButton(primaryStage);

		VBox titleLayout = new VBox(20, titleImage, newGame, startButton, exitButton);
		titleLayout.setAlignment(Pos.CENTER);

		Image gifImage = new Image(ClassLoader.getSystemResource("BG.gif").toString());
		ImageView gifView = new ImageView(gifImage);
		gifView.setFitWidth(900);
		gifView.setFitHeight(700);

		MenuButtonManager.styleButton(startButton);
		MenuButtonManager.styleButton(exitButton);

		StackPane root = new StackPane(gifView, titleLayout);
		Scene titleScene = new Scene(root, 900, 700);
		primaryStage.setScene(titleScene);
		playBackgroundMusic();
	}

}
