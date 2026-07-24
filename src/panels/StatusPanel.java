package panels;

import character.GameCharacter;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * One "Player Status" or "Enemy Status" box. FightScene used to build two of
 * these inline with duplicated styling code - now it's just two instances.
 */
public class StatusPanel {

	private final VBox box = new VBox(10);
	private final String title;
	private final Color color;

	public StatusPanel(String title, Color color, double x, double y) {
		this.title = title;
		this.color = color;

		box.setPadding(new Insets(10));
		box.setLayoutX(x);
		box.setLayoutY(y);
		box.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY)));
		box.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
	}

	public VBox getNode() {
		return box;
	}

	public void update(GameCharacter character) {
		box.getChildren().clear();
		box.getChildren().add(titleLabel());

		String[] statValues = { character.getClass().getSimpleName(), "HP: " + character.getHp(),
				"MP: " + character.getMana(), "ATK: " + character.getAttack(), "MAGIC: " + character.getMagic(),
				"SPEED: " + character.getSpeed() };

		Font statFont = Font.font("Verdana", FontWeight.NORMAL, 14);
		for (String value : statValues) {
			Label label = new Label(value);
			label.setFont(statFont);
			label.setTextFill(color);
			box.getChildren().add(label);
		}
	}

	private Label titleLabel() {
		Label label = new Label(title);
		label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		label.setTextFill(color);
		return label;
	}
}
