package character;

import java.util.ArrayList;

import grid.FightScene;

public class Enemy extends GameCharacter {

	protected int manacost;

	public Enemy(int hp, int attack, int magic, int mana, int speed) {
		setHp(hp);
		setAttack(attack);
		setMagic(magic);
		setMana(mana);
		setSpeed(speed);
	}

	public Enemy() {
		setHp(200);
		setAttack(20);
		setMagic(30);
		setMana(100);
		setSpeed(1);
		setManacost(15);
	}

	public int getManacost() {
		return manacost;
	}

	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	
	public void chooseTarget(ArrayList<GameCharacter> target) {
		int min = 0;
		for (int i = 1; i < target.size(); i++) {
			if (target.get(i).getHp() < target.get(min).getHp())
				min = i;
		}
		GameCharacter choosenTarget = target.get(min);
		if (this.takeAction(choosenTarget).equals("attack")) {
			FightScene.playerAction(1, this, choosenTarget);
		}
		else if(this.takeAction(choosenTarget).equals("magic")) {
			FightScene.playerAction(2, this, choosenTarget);
		}
	}

	@Override
	public void magic(GameCharacter target) {
		target.setHp(target.getHp() - getMagic());
		setMana(getMana() - getManacost());
	}

	public String takeAction(GameCharacter target) {
		double actionChoice = Math.random();
		if (actionChoice < 0.5) {
			return "attack";
		} else {
			return "magic";
		}
	}

	@Override
	public void attack(GameCharacter target) {
		target.setHp(target.getHp() - getAttack());
	}
}
