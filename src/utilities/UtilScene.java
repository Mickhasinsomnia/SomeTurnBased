package utilities;

import java.util.ArrayList;
import java.util.Collections;

import application.Main;
import character.Archer;
import character.GameCharacter;
import character.Priest;
import character.Swordman;
import character.Wizard;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import panels.LevelSelect;
import player.PlayerTeam;

public class UtilScene {

	public static void showManage(Stage primary) {
		PlayerTeam.player().clear();

		
		Image bgImage = new Image(ClassLoader.getSystemResource("finalfan2.png").toString());
		ImageView bgImageView = new ImageView(bgImage);
		bgImageView.setFitWidth(900);
		bgImageView.setFitHeight(700);
		bgImageView.setPreserveRatio(false);

		Text title = new Text("Manage Your Team");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
		title.setFill(Color.WHITE);
		title.setLayoutX(300);
		title.setLayoutY(50);
		title.setStroke(Color.BLACK);
		title.setStrokeWidth(2);

		Button back = new Button("Back To Main");
		back.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		ButtonManager.styleButton(back);
		back.setLayoutX(26);
		back.setLayoutY(600);
		back.setMinSize(120, 40);
		back.setOnAction(e -> Main.showTitleScreen(primary));

		Button selectButton = new Button("Select");
		selectButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		ButtonManager.styleButton(selectButton);
		selectButton.setOnAction(e -> showLevelSelect(primary));
		selectButton.setLayoutX(670);
		selectButton.setLayoutY(600);

		Button remove = new Button("Clear");
		remove.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		ButtonManager.styleButton(remove);
		remove.setLayoutX(500);
		remove.setLayoutY(600);

		Pane manageLayout = new Pane();

		manageLayout.getChildren().add(bgImageView);

		manageLayout.getChildren().addAll(title);

		Button[] buttons = { new Button("Swordman"), new Button("Wizard"), new Button("Archer"), new Button("Priest") };
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setLayoutY(100 + i * 50);
			buttons[i].setLayoutX(50);
			buttons[i].setFont(Font.font("Arial", FontWeight.BOLD, 18));
			ButtonManager.styleButton(buttons[i]);
		}

		Label[] labels = { new Label("Total Players: 0"), new Label("Swordman: 0"), new Label("Wizard: 0"),
				new Label("Archer: 0"), new Label("Priest: 0") };

		for (int i = 0; i < labels.length; i++) {
			labels[i].setLayoutX(650);
			labels[i].setLayoutY(100 + i * 30);
			labels[i].setFont(Font.font("Arial", FontWeight.BOLD, 18));
			styleLabelWithStroke(labels[i]);
		}

		ArrayList<Pair<Integer, Integer>> positions = generatePlayerIconPosition();

		ArrayList<ImageView> playerIcons = new ArrayList<>();

		Thread updateCount = new Thread(() -> {
			int total = PlayerTeam.player().size();
			long swordmanCount = PlayerTeam.player().stream().filter(p -> p instanceof Swordman).count();
			long wizardCount = PlayerTeam.player().stream().filter(p -> p instanceof Wizard).count();
			long archerCount = PlayerTeam.player().stream().filter(p -> p instanceof Archer).count();
			long priestCount = PlayerTeam.player().stream().filter(p -> p instanceof Priest).count();

			Platform.runLater(() -> {
				labels[0].setText("Total Players: " + total);
				labels[1].setText("Swordman: " + swordmanCount);
				labels[2].setText("Wizard: " + wizardCount);
				labels[3].setText("Archer: " + archerCount);
				labels[4].setText("Priest: " + priestCount);

				manageLayout.getChildren().removeAll(playerIcons);
				playerIcons.clear();

				for (int i = 0; i < PlayerTeam.player().size(); i++) {
					GameCharacter p = PlayerTeam.player().get(i);
					Image selfMade = new Image(p.getSelf());

					ImageView avatar = new ImageView(selfMade);
					avatar.setFitHeight(120);
					avatar.setFitWidth(120);
					avatar.setLayoutX(positions.get(i).getKey());
					avatar.setLayoutY(positions.get(i).getValue());
					playerIcons.add(avatar);
				}
				manageLayout.getChildren().addAll(playerIcons);
			});
		});

		buttons[0].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Swordman());
				updateCount.run();
			}
		});
		buttons[1].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Wizard());
				updateCount.run();
			}
		});
		buttons[2].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Archer());
				updateCount.run();
			}
		});
		buttons[3].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Priest());
				updateCount.run();
			}
		});
		remove.setOnAction(e -> {
			PlayerTeam.player().clear();
			updateCount.run();
		});

		manageLayout.getChildren().addAll(selectButton, remove);
		manageLayout.getChildren().addAll(buttons);
		manageLayout.getChildren().addAll(labels);
		manageLayout.getChildren().addAll(back);

		Platform.runLater(() -> {
			primary.getScene().setRoot(manageLayout);
		});
		
		
	}

	private static ArrayList<Pair<Integer, Integer>> generatePlayerIconPosition() {
		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(100, 300));
		use.add(new Pair<>(200, 550));
		use.add(new Pair<>(50, 420));
		use.add(new Pair<>(360, 300));
		return use;
	}

	public static void showLevelSelect(Stage primary) {
		ArrayList<Button> buttonList = new ArrayList<>();

		int unlockedLevels = Main.unlock;

		for (int i = 1; i <= unlockedLevels; i++) {
			buttonList.add(selectLevel(i, primary));
		}

		FlowPane flowPane = new FlowPane();
		flowPane.setHgap(20);
		flowPane.setVgap(20);
		flowPane.setAlignment(Pos.TOP_LEFT);

		Button Back = new Button("Back");
		ButtonManager.styleButton(Back);
		Back.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		Back.setOnAction(e -> {
			showManage(primary);
			PlayerTeam.player().clear();
		});

		flowPane.getChildren().addAll(buttonList);
		flowPane.getChildren().add(Back);

		VBox root = new VBox(flowPane);
		root.setPadding(new Insets(20, 20, 20, 50));

		Platform.runLater(() -> {
			primary.getScene().setRoot(root);
		});
	}

	private static Button selectLevel(int level, Stage primary) {
		Button startButton = new Button("Level " + level);
		startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		ButtonManager.styleButton(startButton);
		startButton.setOnMouseClicked(e -> LevelSelect.Level(level, primary));
		return startButton;
	}

	private static void styleLabelWithStroke(Label label) {
		label.setStyle("-fx-text-fill: white;");
		DropShadow textStroke = new DropShadow();
		textStroke.setColor(Color.BLACK);
		textStroke.setRadius(2.0);
		textStroke.setSpread(1.0);
		label.setEffect(textStroke);
	}

}
