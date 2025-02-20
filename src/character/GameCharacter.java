package character;

import javafx.scene.image.ImageView;

public abstract class GameCharacter implements Comparable<GameCharacter> {
    protected int hp;
    protected int attack;
    protected int magic;
    protected int mana;
    protected int speed;
    protected ImageView self=new ImageView();
    protected double Xpos;
    protected double Ypos;
    protected double originalX;
    protected double originalY;
   
    
    public ImageView getSelf() {
		return self;
	}

	public void setSelf(ImageView self) {
		this.self = self;
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
    	if(attack<0)
    		attack=0;
        this.attack = attack;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
    	if(magic<0)
    		magic=0;
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
    	if(speed<0)
    		speed=0;
        this.speed = speed;
    }

    @Override
    public int compareTo(GameCharacter other) {
        return Integer.compare(this.speed, other.speed);
    }
    
    @Override
    public String toString() {
    	return this.getClass().getSimpleName()+" "+this.getAttack();
    }
    
    public void setPos(double x,double y) {
    	this.Xpos=x;
    	this.Ypos=y;
    }
    
    public void setoriPos(double x,double y) {
    	this.originalX=x;
    	this.originalY=y;
    }
    
    public double getoriPosX() {
    	return originalX;
    
    }
    
    public double getoriPosY() {
    	return originalX;
    
    }
    
    public double getPosX(){
    	return Xpos;
    }
    
    public double getPosY(){
    	return Ypos;
    }
}
