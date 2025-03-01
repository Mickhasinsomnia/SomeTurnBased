package character;

public class Archer extends GameCharacter implements RangeCharacter{

	public Archer() {
		this.setHp(80);
		this.setAttack(50);
		setSpeed(12);
		self=ClassLoader.getSystemResource("archer.png").toString();
		maxhp=hp;
	}
	
	@Override
	public void attack(GameCharacter target) {
		target.setHp(target.getHp()-getAttack());
	}

	@Override
	public void magic(GameCharacter target) {
		
	}

	@Override
	public String getWeapon() {
		return ClassLoader.getSystemResource("arrow.png").toString();
	}

}
