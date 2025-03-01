package application;

import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import utilities.ButtonManager;
import utilities.SaveManager;
import utilities.UtilScene;
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
		titleText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
		titleText.setFill(Color.BLACK);

		DropShadow titleStroke = new DropShadow();
		titleStroke.setColor(Color.GRAY);
		titleStroke.setRadius(3.0);
		titleStroke.setSpread(1.2);
		titleText.setEffect(titleStroke);

		Glow glow = new Glow(0.5);
		titleText.setEffect(glow);

		Button newGame = ButtonManager.newgameButton(primary);
		Button startButton = ButtonManager.loadgameButton(primary);
		Button exitButton = ButtonManager.exitButton(primary);

		VBox titleLayout = new VBox(20, titleText, newGame, startButton, exitButton);
		titleLayout.setAlignment(javafx.geometry.Pos.CENTER);

		Image gifImage = new Image(ClassLoader.getSystemResource("bg.gif").toString());
		ImageView gifView = new ImageView(gifImage);
		gifView.setFitWidth(900);
		gifView.setFitHeight(700);

		ButtonManager.styleButton(startButton);
		ButtonManager.styleButton(exitButton);

		StackPane root = new StackPane(gifView, titleLayout);
		Scene titleScene = new Scene(root, 900, 700);
		primary.setScene(titleScene);

	}
}

	