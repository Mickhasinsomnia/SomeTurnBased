package panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.Main;
import character.Enemy;
import character.GameCharacter;
import character.Necromancer;
import character.Priest;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import utilities.SaveManager;

/**
 * Owns the actual game rules: whose turn it is, what attack/magic/heal does,
 * who wins. Talks to CombatRenderer for anything visual and
 * PlayerInputHandler for anything requiring player input, but doesn't know
 * how either of those is implemented.
 */
public class CombatEngine {

	public interface TurnListener {
		/** Called after each completed turn, on the FX thread is not guaranteed - wrap in Platform.runLater if needed. */
		void onTurnCompleted();

		void onGameEnded(boolean playersWon);
	}

	private final ArrayList<GameCharacter> players;
	private final ArrayList<GameCharacter> enemies;
	private final CombatRenderer renderer;
	private final PlayerInputHandler input;
	private final TurnListener listener;
	private final int level;
	private final Stage primary;

	private volatile Thread combatThread;

	public CombatEngine(ArrayList<GameCharacter> players, ArrayList<GameCharacter> enemies, CombatRenderer renderer,
	                    PlayerInputHandler input, TurnListener listener, int level, Stage primary) {
		this.players = players;
		this.enemies = enemies;
		this.renderer = renderer;
		this.input = input;
		this.listener = listener;
		this.level = level;
		this.primary = primary;
	}

	public void start() {
		combatThread = new Thread(this::runLoop);
		combatThread.start();
	}

	public void stop() {
		if (combatThread != null && combatThread.isAlive()) {
			combatThread.interrupt();
		}
	}

	private void runLoop() {
		try {
			while (!players.isEmpty() && !enemies.isEmpty() && !Thread.currentThread().isInterrupted()) {
				List<GameCharacter> turnOrder = new ArrayList<>(players);
				turnOrder.addAll(enemies);
				Collections.sort(turnOrder);

				Polygon turnPointer = renderer.createTurnPointer();

				for (GameCharacter current : turnOrder) {
					if (current.getHp() <= 0) {
						continue;
					}

					if (current instanceof Enemy) {
						runEnemyTurn(current);
					} else if (current instanceof Priest) {
						sleep(1500);
						autoHeal(current, players);
					} else {
						runPlayerTurn(current, turnPointer);
					}

					checkForDeadCharacters();
					if (players.isEmpty() || enemies.isEmpty()) {
						endGame();
						return;
					}

					listener.onTurnCompleted();
				}
			}
			endGame();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			// Preserve original behavior of not crashing the game on unexpected
			// errors mid-combat. Worth logging if you revisit this.
		}
	}

	private void runPlayerTurn(GameCharacter current, Polygon turnPointer) throws InterruptedException {
		renderer.showTurnPointer(turnPointer, current);

		int[] action = input.awaitPlayerAction(); // blocks until player picks action + target, no polling
		int actionType = action[0];
		int enemyIndex = action[1];

		if (enemyIndex < 0 || enemyIndex >= enemies.size()) {
			renderer.hideTurnPointer(turnPointer);
			return;
		}
		Enemy target = (Enemy) enemies.get(enemyIndex);

		executeAction(actionType, current, target, enemies);
		renderer.hideTurnPointer(turnPointer);
	}

	private void runEnemyTurn(GameCharacter current) {
		sleep(750);

		if (current instanceof Necromancer) {
			autoHeal(current, enemies);
			return;
		}

		Enemy enemy = (Enemy) current;
		ArrayList<GameCharacter> alivePlayers = new ArrayList<>();
		for (GameCharacter p : players) {
			if (p.getHp() > 0) {
				alivePlayers.add(p);
			}
		}

		if (alivePlayers.isEmpty()) {
			if (players.size() > enemies.size()) {
				updateLevel();
			}
			Platform.runLater(() -> UtilScene.showManage(primary));
			return;
		}

		GameCharacter chosenTarget = enemy.selectTarget(alivePlayers);
		String action = enemy.takeAction();

		if (action.equals("magic") && enemy.getMana() > 0) {
			executeAction(PlayerInputHandler.ACTION_MAGIC, enemy, chosenTarget, alivePlayers);
		} else {
			executeAction(PlayerInputHandler.ACTION_ATTACK, enemy, chosenTarget, alivePlayers);
		}
	}

	/** Shared by player and enemy turns - same rules apply to both. */
	private void executeAction(int actionType, GameCharacter current, GameCharacter target,
	                           ArrayList<GameCharacter> allTargets) {
		if (current.getMana() <= 0) {
			actionType = PlayerInputHandler.ACTION_ATTACK;
		}

		if (actionType == PlayerInputHandler.ACTION_ATTACK) {
			UtilScene.playAudio(current.getSoundEffect());
			if (current instanceof character.RangeCharacter) {
				renderer.playRangedAttack(current, target);
			} else {
				renderer.playMeleeAttack(current, target);
			}
			current.attack(target);
		} else {
			if (current.getMana() <= 0) {
				return;
			}
			renderer.playAreaEffect(allTargets, Color.RED, "magic2.wav");
			current.magic(allTargets);
		}
	}

	private void autoHeal(GameCharacter current, ArrayList<GameCharacter> allies) {
		if (current.getMana() <= 0) {
			return;
		}
		renderer.playAreaEffect(allies, Color.GREEN, "heal.wav");
		for (GameCharacter ally : allies) {
			ally.setHp(Math.min(ally.getHp() + current.getMagic(), ally.getMaxHp()));
		}
		current.setMana(current.getMana() - current.getManaCost());
	}

	private void checkForDeadCharacters() {
		players.removeIf(c -> c.getHp() <= 0);
		enemies.removeIf(c -> c.getHp() <= 0);
	}

	private void updateLevel() {
		if (!Main.finished.get(level) && level < 5) {
			SaveManager.saveUnlock(SaveManager.loadUnlock() + 1);
			Main.unlock += 1;
			Main.finished.set(level, true);
		}
	}

	private void endGame() {
		if (players.size() > enemies.size()) {
			updateLevel();
		}
		listener.onGameEnded(!players.isEmpty());
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}