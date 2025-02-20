package character;


public class Swordman extends GameCharacter {

	public Swordman() {
		this.setHp(200);
		this.setAttack(40);
		setMagic(0);
		setMana(0);
		setSpeed(10);
		self=ClassLoader.getSystemResource("swordman.png").toString();
	}
	@Override
	public void attack(GameCharacter target) {
		target.setHp(target.getHp()-getAttack());
	}

	@Override
	public void magic(GameCharacter target) {
		
	}

}
