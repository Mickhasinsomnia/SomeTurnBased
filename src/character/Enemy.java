package character;

import java.util.ArrayList;

public abstract class Enemy extends GameCharacter {

	public Enemy() {
	}

	public GameCharacter selectTarget(ArrayList<GameCharacter> aliveTargets) {
		int index = (int) (Math.random() * aliveTargets.size());
		return aliveTargets.get(index);
	}

	public String takeAction() {
		return Math.random() < 0.5 ? "attack" : "magic";
	}

}