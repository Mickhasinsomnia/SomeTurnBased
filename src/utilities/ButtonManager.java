package utilities;

import java.util.ArrayList;
import java.util.Collections;

import application.Main;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ButtonManager {
	public static Button newgameButton(Stage primary) {
		Button newGame = new Button("New Game");
		newGame.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		newGame.setOnAction(e -> {
			SaveManager.saveUnlock(1);
			Main.unlock = SaveManager.loadUnlock();
			Main.finished = new ArrayList<>(Collections.nCopies(11, false));
			UtilScene.showManage(primary);
		});
		ButtonManager.styleButton(newGame);
		return newGame;
	}

	public static Button loadgameButton(Stage primary) {
		Button startButton = new Button("Load Game");
		startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		startButton.setOnAction(e -> UtilScene.showManage(primary));
		ButtonManager.styleButton(startButton);
		return startButton;
	}

	public static Button exitButton(Stage primary) {
		Button exitButton = new Button("Exit");
		exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		exitButton.setOnAction(e -> Platform.exit());
		return exitButton;
	}

	public static void styleButton(Button button) {

		String defaultStyle = "-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white;";
		String hoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.3); -fx-border-color: white; -fx-text-fill: black;";
		String pressedStyle = "-fx-background-color: rgba(255, 255, 255, 0.6); -fx-border-color: white; -fx-text-fill: black;";

		button.setStyle(defaultStyle);
		button.setUserData(defaultStyle);

		DropShadow textStroke = new DropShadow();
		textStroke.setColor(Color.BLACK);
		textStroke.setRadius(2.0);
		textStroke.setSpread(1.0);
		button.setEffect(textStroke);
		styleOnMouse(button, textStroke, hoverStyle, pressedStyle);

	}
	private static void styleOnMouse(Button button, DropShadow textStroke, String hoverStyle, String pressedStyle) {
		button.setOnMouseEntered(e -> {
		    button.setScaleX(1.1);
		    button.setScaleY(1.1);
		});
		button.setOnMouseExited(e -> {
		    button.setScaleX(1.0);
		    button.setScaleY(1.0);
		});

		button.setOnMousePressed(e -> {
		    button.setScaleX(1.2);
		    button.setScaleY(1.2);
		});

		button.setOnMouseReleased(e -> {
		    button.setScaleX(1.1);
		    button.setScaleY(1.1);
		});
	}
}
