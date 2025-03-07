package character;

public class Priest extends GameCharacter {

	public Priest() {
		setHp(80);
		setMagic(30);
		setSpeed(1);
		setMana(120);
		setManaCost(20);
		setMaxHp(80);
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("priest.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "heal.wav";
	}

}
