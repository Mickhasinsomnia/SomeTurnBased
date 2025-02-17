package character;

public class EnemyHealer extends Enemy {

	public EnemyHealer(int hp, int attack, int magic, int mana, int speed) {
		super(hp, attack, magic, mana, speed);
		
	}
	
	@Override
    public void magic(GameCharacter target) {
        System.out.println(getClass().getSimpleName() + " casts a spell on " + target.getClass().getSimpleName() + " for " + getMagic() + " magic damage.");
        target.setMana(target.getMana() - getMagic());  
    }

}
