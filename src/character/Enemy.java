package character;

public class Enemy extends GameCharacter {

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
    }

    @Override
    public void attack(GameCharacter target) {
        System.out.println(getClass().getSimpleName() + " attacks " + target.getClass().getSimpleName() + " for " + getAttack() + " damage.");
        target.setHp(target.getHp() - getAttack());  
    }

    @Override
    public void magic(GameCharacter target) {
        System.out.println(getClass().getSimpleName() + " casts a spell on " + target.getClass().getSimpleName() + " for " + getMagic() + " magic damage.");
        target.setHp(target.getHp() - getMagic()); 
        setMana(target.getMana() - getMagic());  
    }

    public void takeAction(GameCharacter target) {
        double actionChoice = Math.random();

        if (actionChoice < 0.5) {
            attack(target); 
        } else {
            magic(target); 
        }
    }
}
