package character;

import java.util.ArrayList;

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

	/** Boss behavior: always focus whoever currently has the least HP. */
	@Override
	public GameCharacter selectTarget(ArrayList<GameCharacter> aliveTargets) {
		int min = 0;
		for (int i = 1; i < aliveTargets.size(); i++) {
			if (aliveTargets.get(i).getHp() < aliveTargets.get(min).getHp()) {
				min = i;
			}
		}
		return aliveTargets.get(min);
	}

}