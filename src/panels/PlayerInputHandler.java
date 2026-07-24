package panels;

import java.util.concurrent.SynchronousQueue;

import javafx.scene.control.Button;

public class PlayerInputHandler {

	public static final int ACTION_ATTACK = 1;
	public static final int ACTION_MAGIC = 2;

	private final SynchronousQueue<int[]> inputQueue = new SynchronousQueue<>();

	private final Button attackButton = new Button("Attack");
	private final Button magicButton = new Button("Magic");

	private final String defaultButtonStyle = "-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: white; "
			+ "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10px;";
	private final String selectButtonStyle = "-fx-background-color: rgba(255, 255, 255, 0.7); -fx-border-color: black; "
			+ "-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10px;";

	// -1 means "no action type chosen yet"
	private volatile int pendingActionType = -1;

	public PlayerInputHandler() {
		attackButton.setLayoutX(15);
		attackButton.setLayoutY(80);
		magicButton.setLayoutX(143);
		magicButton.setLayoutY(80);
		attackButton.setMinSize(120, 75);
		magicButton.setMinSize(120, 75);
		resetButtonStyles();

		attackButton.setOnMouseClicked(event -> {
			pendingActionType = ACTION_ATTACK;
			UtilScene.playAudio("click.wav");
			attackButton.setStyle(selectButtonStyle);
			magicButton.setStyle(defaultButtonStyle);
		});

		magicButton.setOnMouseClicked(event -> {
			pendingActionType = ACTION_MAGIC;
			UtilScene.playAudio("click.wav");
			attackButton.setStyle(defaultButtonStyle);
			magicButton.setStyle(selectButtonStyle);
		});
	}

	public Button getAttackButton() {
		return attackButton;
	}

	public Button getMagicButton() {
		return magicButton;
	}

	public void resetButtonStyles() {
		attackButton.setStyle(defaultButtonStyle);
		magicButton.setStyle(defaultButtonStyle);
	}

	/**
	 * Call this from the enemy sprite's mouse-click handler. Only completes an
	 * action if the player already picked attack/magic; otherwise it's ignored
	 * (mirrors the old "if (pressed)" guard).
	 */
	public void onEnemySelected(int enemyIndex) {
		if (pendingActionType == -1) {
			return;
		}
		int type = pendingActionType;
		pendingActionType = -1;
		resetButtonStyles();
		// offer() is non-blocking; fine here since the combat thread is normally
		// already waiting in take(). If it isn't (rare race on scene rebuild),
		// the click is simply dropped, same as the old code effectively did.
		inputQueue.offer(new int[] { type, enemyIndex });
	}

	/**
	 * Blocks the calling thread (the combat thread) until a full action has
	 * been chosen. Returns {actionType, enemyIndex}.
	 */
	public int[] awaitPlayerAction() throws InterruptedException {
		return inputQueue.take();
	}
}
