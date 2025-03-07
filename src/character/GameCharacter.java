package character;

import java.util.ArrayList;

import javafx.scene.image.ImageView;
import javafx.util.Pair;

public abstract class GameCharacter implements Comparable<GameCharacter> {
	protected int hp;
	protected int attack;
	protected int magic;
	protected int mana;
	protected int speed;
	protected int maxHp;
	protected int manaCost;
	protected double xPos;
	protected double yPos;
	protected double originalX = -1;
	protected double originalY = -1;
	protected ImageView img;

	public ImageView getImg() {
		return img;
	}

	public void setImg(ImageView img) {
		this.img = img;
	}

	public abstract String getImagePath();

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public void attack(GameCharacter target) {
		target.setHp(target.getHp() - getAttack());
	}

	public void magic(ArrayList<GameCharacter> all) {
		if (getMana() <= 0)
			return;
		for (GameCharacter target : all)
			target.setHp(target.getHp() - getMagic());
		this.setMana(getMana() - getManaCost());
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = Math.max(0, hp);
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		if (attack < 0)
			attack = 0;
		this.attack = attack;
	}

	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		if (magic < 0)
			magic = 0;
		this.magic = magic;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		if (mana < 0)
			mana = 0;
		this.mana = mana;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		if (speed < 0)
			speed = 0;
		this.speed = speed;
	}

	public int getManaCost() {
		return manaCost;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

	@Override
	public int compareTo(GameCharacter other) {
		return Integer.compare(other.speed, this.speed);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public void setPos(double x, double y) {
		this.xPos = x;
		this.yPos = y;
	}

	public Pair<Double, Double> getPos() {
		return new Pair<Double, Double>(xPos, yPos);
	}

	public boolean stillDefault() {
		return (originalX == -1 && originalY == -1);
	}

	public Pair<Double, Double> getOriginalPos() {
		return new Pair<Double, Double>(originalX, originalY);
	}

	public void setOriginalPos(double X, double Y) {
		this.originalX = X;
		this.originalY = Y;
	}

	@Override
	public boolean equals(Object other) {
		return other != null && getClass() == other.getClass();
	}

	public abstract String getSoundEffect();

}
