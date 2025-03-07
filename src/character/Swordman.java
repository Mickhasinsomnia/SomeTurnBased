package character;



public class Swordman extends GameCharacter {

	public Swordman() {
		setHp(160);
		setAttack(20);
		setMagic(0);
		setMana(0);
		setSpeed(10);
		setMaxHp(160);
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("swordman.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "sword.wav";
	}

}
