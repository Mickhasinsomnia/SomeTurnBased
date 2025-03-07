package character;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import panels.FightScene;

public class Xande extends Enemy implements RangeCharacter {

	public Xande() {
		setHp(300);
		setAttack(50);
		setMagic(35);
		setMana(300);
		setSpeed(50);
		setMaxHp(300);
		setManaCost(20);
	}

	@Override
	public String getWeapon() {
		return ClassLoader.getSystemResource("fire.png").toString();
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("boss.png").toString();
	}

	@Override
	public void attack(GameCharacter target) {
		super.attack(target);
		setHp(Math.min(getMaxHp(), getHp() + 40));
	}
	
	@Override
	public void magic(ArrayList<GameCharacter> all) {
		super.magic(all);
		setHp(Math.min(getMaxHp(), getHp() + 40));
	}

	
	@Override
	public String getSoundEffect() {
		return "fire.wav";
	}

	@Override
	public void chooseTarget(ArrayList<GameCharacter> target, Pane pane) {
		int min = 0;
		for (int i = 1; i < target.size(); i++) {
			if (target.get(i).getHp() < target.get(min).getHp())
				min = i;
		}
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

}
