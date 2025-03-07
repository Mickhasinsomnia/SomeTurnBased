package character;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import panels.FightScene;

public abstract class Enemy extends GameCharacter {

	public Enemy() {
		
	}

	public void chooseTarget(ArrayList<GameCharacter> target, Pane pane) {
		int min = (int) (Math.random() * target.size());
		GameCharacter choosenTarget = target.get(min);
		String action = takeAction();
		if (action.equals("attack")) {
			FightScene.executeTurnAction(1, this, choosenTarget, pane, target);
		} else if (action.equals("magic")) {
			if (this.getMana() > 0) {
				FightScene.executeTurnAction(2, this, choosenTarget, pane, target);
			} else {
				FightScene.executeTurnAction(1, this, choosenTarget, pane, target);
			}
		}
	}

	public String takeAction() {
		double actionChoice = Math.random();
		if (actionChoice < 0.5) {
			return "attack";
		} else {
			return "magic";
		}
	}

}
