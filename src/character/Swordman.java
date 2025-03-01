package character;


public class Swordman extends GameCharacter {

	public Swordman() {
		this.setHp(160);
		this.setAttack(20);
		setMagic(0);
		setMana(0);
		setSpeed(10);
		imagePath=ClassLoader.getSystemResource("swordman.png").toString();
		maxhp=hp;
	}
	@Override
	public void attack(GameCharacter target) {
		target.setHp(target.getHp()-getAttack());
	}

	@Override
	public void magic(GameCharacter target) {
		
	}

}
