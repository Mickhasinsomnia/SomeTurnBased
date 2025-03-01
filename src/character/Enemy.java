package character;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import panels.FightScene;

public class Enemy extends GameCharacter {

	protected int manacost;

	public Enemy(int hp, int attack, int magic, int mana, int speed) {
		setHp(hp);
		setAttack(attack);
		setMagic(magic);
		setMana(mana);
		setSpeed(speed);
		maxhp = hp;
		this.self = ClassLoader.getSystemResource("up_2.png").toString();
	}

	public Enemy() {
		setHp(200);
		setAttack(20);
		setMagic(30);
		setMana(100);
		setSpeed(1);
		setManacost(15);
		maxhp = hp;
		this.self = ClassLoader.getSystemResource("up_2.png").toString();
	}

	public int getManacost() {
		return manacost;
	}

	public void setManacost(int manacost) {
		this.manacost = manacost;
	}

	public void chooseTarget(ArrayList<GameCharacter> target, Pane pane) {
//		int min = 0;
//		for (int i = 1; i < target.size(); i++) {
//			if (target.get(i).getHp() < target.get(min).getHp())
//				min = i;
//		}
		int min = (int) (Math.random() * target.size());
		GameCharacter choosenTarget = target.get(min);
		String action = takeAction();
		if (action.equals("attack")) {
			FightScene.playerAction(1, this, choosenTarget, pane);
		} else if (action.equals("magic")) {
			if (this.getMana() > 0) {
				FightScene.playerAction(2, this, choosenTarget, pane);
			} else {
				FightScene.playerAction(1, this, choosenTarget, pane);
			}
		}
	}

	@Override
	public void magic(GameCharacter target) {
		target.setHp(target.getHp() - getMagic());
		setMana(getMana() - getManacost());
	}

	public String takeAction() {
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
