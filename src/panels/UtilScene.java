package panels;

import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import player.PlayerTeam;
import utilities.MenuButtonManager;

public class UtilScene {

	public static void showManage(Stage primary) {
		PlayerTeam.player().clear();
		Main.clip.stop();
		Main.clip.close();

		Button selectButton = new Button("Select");
		selectButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		MenuButtonManager.styleButton(selectButton);
		selectButton.setDisable(true);

		selectButton.setOnAction(e -> {
			if (PlayerTeam.player().size() > 0) {
				showLevelSelect(primary);
			}
		});

		selectButton.setLayoutX(670);
		selectButton.setLayoutY(600);

		Button back = backButton(primary);
		Button remove = removeButton();

		Pane manageLayout = new Pane();

		setBackground(manageLayout);

		Button[] buttons = characterButton();
		Label[] labels = characterLabel();
		ArrayList<Pair<Integer, Integer>> positions = generatePlayerIconPosition();
		ArrayList<ImageView> playerIcons = new ArrayList<>();

		Runnable updateUI = () -> {
			updateStatus(labels, manageLayout, playerIcons, positions);
			selectButton.setDisable(PlayerTeam.player().isEmpty());
		};

		addCharacterButton(buttons, updateUI);

		remove.setOnAction(e -> {
			PlayerTeam.player().clear();
			updateUI.run();
		});

		manageLayout.getChildren().addAll(selectButton, remove, back);
		manageLayout.getChildren().addAll(buttons);
		manageLayout.getChildren().addAll(labels);

		Platform.runLater(() -> primary.getScene().setRoot(manageLayout));
	}

	private static ArrayList<Pair<Integer, Integer>> generatePlayerIconPosition() {
		ArrayList<Pair<Integer, Integer>> use = new ArrayList<>();
		use.add(new Pair<>(100, 300));
		use.add(new Pair<>(200, 480));
		use.add(new Pair<>(50, 420));
		use.add(new Pair<>(320, 300));
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
		MenuButtonManager.styleButton(Back);
		Back.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		Back.setOnAction(e -> {
			showManage(primary);
			PlayerTeam.player().clear();
		});

		flowPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 15px; -fx-padding: 20px;");

		flowPane.getChildren().addAll(buttonList);
		flowPane.getChildren().add(Back);

		VBox root = new VBox(flowPane);
		root.setPadding(new Insets(20, 20, 20, 50));

		root.setStyle("-fx-background-color: #e1f5fe;");

		primary.getScene().setFill(Color.web("#e1f5fe"));

		Platform.runLater(() -> {
			primary.getScene().setRoot(root);
		});
	}

	private static Button selectLevel(int level, Stage primary) {
		Button startButton = new Button("Level " + level);
		startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		MenuButtonManager.styleButton(startButton);
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

	private static Button[] characterButton() {
		Button[] buttons = { new Button("Swordman"), new Button("Wizard"), new Button("Archer"), new Button("Priest") };
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setLayoutY(100 + i * 50);
			buttons[i].setLayoutX(50);
			buttons[i].setFont(Font.font("Arial", FontWeight.BOLD, 18));
			MenuButtonManager.styleButton(buttons[i]);
		}
		return buttons;
	}

	private static Label[] characterLabel() {
		Label[] labels = { new Label("Total Players: 0"), new Label("Swordman: 0"), new Label("Wizard: 0"),
				new Label("Archer: 0"), new Label("Priest: 0") };

		for (int i = 0; i < labels.length; i++) {
			labels[i].setLayoutX(720);
			labels[i].setLayoutY(100 + i * 30);
			labels[i].setFont(Font.font("Arial", FontWeight.BOLD, 18));
			styleLabelWithStroke(labels[i]);
		}
		return labels;
	}

	private static void updateStatus(Label[] labels, Pane manageLayout, ArrayList<ImageView> playerIcons,
			ArrayList<Pair<Integer, Integer>> positions) {
		int total = PlayerTeam.player().size();
		long swordmanCount = PlayerTeam.player().stream().filter(p -> p instanceof Swordman).count();
		long wizardCount = PlayerTeam.player().stream().filter(p -> p instanceof Wizard).count();
		long archerCount = PlayerTeam.player().stream().filter(p -> p instanceof Archer).count();
		long priestCount = PlayerTeam.player().stream().filter(p -> p instanceof Priest).count();

		VBox characterInfo = new VBox();
		characterInfo.setBackground(
				new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.7), new CornerRadii(10), Insets.EMPTY)));
		characterInfo.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));
		characterInfo.setLayoutX(500);
		characterInfo.setLayoutY(300);
		characterInfo.setSpacing(5);
		characterInfo.setPadding(new Insets(10));
		characterInfo.setPrefSize(160, 120);

		Font statLabelFont = Font.font("Verdana", FontWeight.NORMAL, 14);
		Color statColor = Color.BLUE;
		String[] labelsText = { "Name", "HP", "MP", "ATK", "MAGIC", "SPEED" };

		Label[] infoLabels = new Label[labelsText.length];
		for (int i = 0; i < labelsText.length; i++) {
			infoLabels[i] = new Label();
			infoLabels[i].setFont(statLabelFont);
			infoLabels[i].setTextFill(statColor);
			characterInfo.getChildren().add(infoLabels[i]);
		}

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
				ImageView avatar = new ImageView(new Image(p.getImagePath()));
				avatar.setFitHeight(120);
				avatar.setFitWidth(120);
				avatar.setLayoutX(positions.get(i).getKey());
				avatar.setLayoutY(positions.get(i).getValue());

				avatar.setOnMouseEntered(e -> {
					infoLabels[0].setText(p.getClass().getSimpleName());
					infoLabels[1].setText("HP: " + p.getHp());
					infoLabels[2].setText("MP: " + p.getMana());
					infoLabels[3].setText("ATK: " + p.getAttack());
					infoLabels[4].setText("MAGIC: " + p.getMagic());
					infoLabels[5].setText("SPEED: " + p.getSpeed());
					characterInfo.setVisible(true);
				});

				avatar.setOnMouseExited(e -> characterInfo.setVisible(false));

				playerIcons.add(avatar);
			}

			manageLayout.getChildren().addAll(playerIcons);
			manageLayout.getChildren().add(characterInfo);
			characterInfo.setVisible(false);
		});
	}

	public static Clip playAudio(String audioPath) {
		Clip clip = null;
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(audioPath));
			clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clip;
	}

	private static Button backButton(Stage primary) {
		Button back = new Button("Back to menu");
		back.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		MenuButtonManager.styleButton(back);
		back.setOnAction(e -> Main.showTitleScreen(primary));
		back.setLayoutX(50);
		back.setLayoutY(600);
		return back;
	}

	private static Button removeButton() {
		Button remove = new Button("Clear");
		remove.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		MenuButtonManager.styleButton(remove);
		remove.setLayoutX(500);
		remove.setLayoutY(600);
		return remove;
	}

	private static void setBackground(Pane manageLayout) {
		Image bgImage = new Image(ClassLoader.getSystemResource("waiting2.jpeg").toString());
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
		manageLayout.getChildren().add(bgImageView);
		manageLayout.getChildren().addAll(title);
	}

	private static void addCharacterButton(Button[] buttons, Runnable updateUI) {
		buttons[0].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Swordman());
				updateUI.run();
			}
		});

		buttons[1].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Wizard());
				updateUI.run();
			}
		});

		buttons[2].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Archer());
				updateUI.run();
			}
		});

		buttons[3].setOnAction(e -> {
			if (PlayerTeam.player().size() < 4) {
				PlayerTeam.player().add(new Priest());
				updateUI.run();
			}
		});
	}

}
