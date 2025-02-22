package character;

import javafx.scene.image.ImageView;

public abstract class GameCharacter implements Comparable<GameCharacter> {
	protected int hp;
	protected int attack;
	protected int magic;
	protected int mana;
	protected int speed;
	protected String self;
	protected double Xpos;
	protected double Ypos;
	protected double originalX;
	protected double originalY;
	protected ImageView img;

	public ImageView getImg() {
		return img;
	}

	public void setImg(ImageView img) {
		this.img = img;
	}

	public String getSelf() {
		return self;
	}

	public GameCharacter() {
		super();
	}

	public abstract void attack(GameCharacter target);

	public abstract void magic(GameCharacter target);

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
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

	@Override
	public int compareTo(GameCharacter other) {
		return Integer.compare(other.speed, this.speed);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.getAttack();
	}

	public void setPos(double x, double y) {
		this.Xpos = x;
		this.Ypos = y;
	}

	public double getPosX() {
		return Xpos;
	}

	public double getPosY() {
		return Ypos;
	}
}
