package character;

import javafx.scene.image.ImageView;
import javafx.util.Pair;

public abstract class GameCharacter implements Comparable<GameCharacter> {
	protected int hp;
	protected int attack;
	protected int magic;
	protected int mana;
	protected int speed;
	protected int maxhp;
	protected String self;
	protected double xPos;
	protected double yPos;
	protected double originalX=-1;
	protected double originalY=-1;
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

	public int getMaxhp() {
		return maxhp;
	}

	public void setMaxhp(int maxhp) {
		this.maxhp = maxhp;
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
		this.xPos = x;
		this.yPos = y;
	}

	public Pair<Double,Double> getPos(){
		return new Pair<Double,Double>(xPos,yPos);
	}
	
	
	
	public boolean stillDefault() {
		return (originalX==-1 && originalY==-1);
	}

	public double getOriginalX() {
		return originalX;
	}

	public void setOriginalPos(double originalX, double originalY) {
		this.originalX = originalX;
		this.originalY = originalY;
	}

	public double getOriginalY() {
		return originalY;
	}


}
