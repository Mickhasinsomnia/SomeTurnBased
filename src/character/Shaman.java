package character;

public class Shaman extends Enemy implements RangeCharacter {

	public Shaman() {
		setHp(100);
		setAttack(20);
		setMagic(20);
		setMana(120);
		setSpeed(12);
		setManaCost(20);
		setMaxHp(100);
	}

	@Override
	public String getWeapon() {
		return "light2.png";
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("shaman.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "magic.wav";
	}

}
