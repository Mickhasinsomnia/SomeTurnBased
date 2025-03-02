package application;

import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import panels.UtilScene;
import utilities.MenuButtonManager;
import utilities.SaveManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Main extends Application {

	public static int unlock;
	public static ArrayList<Boolean> finished = new ArrayList<>(Collections.nCopies(11, false));

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

	public static void showTitleScreen(Stage primary) {

		Text titleText = new Text("My Awesome Game");
		titleText.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 64));
		titleText.setFill(Color.WHITE);
		titleText.setStroke(Color.BLACK);
		titleText.setStrokeWidth(3);

		Glow glow = new Glow(0.5);
		titleText.setEffect(glow);

		Button newGame = MenuButtonManager.newgameButton(primary);
		Button startButton = MenuButtonManager.loadgameButton(primary);
		Button exitButton = MenuButtonManager.exitButton(primary);

		VBox titleLayout = new VBox(20, titleText, newGame, startButton, exitButton);
		titleLayout.setAlignment(Pos.CENTER);

		Image gifImage = new Image(ClassLoader.getSystemResource("BG.gif").toString());
		ImageView gifView = new ImageView(gifImage);
		gifView.setFitWidth(900);
		gifView.setFitHeight(700);

		MenuButtonManager.styleButton(startButton);
		MenuButtonManager.styleButton(exitButton);

		StackPane root = new StackPane(gifView, titleLayout);
		Scene titleScene = new Scene(root, 900, 700);
		primary.setScene(titleScene);

	}
}
