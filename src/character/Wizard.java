package character;

public class Wizard extends GameCharacter implements RangeCharacter {

	public Wizard() {
		setHp(120);
		setAttack(25);
		setMagic(20);
		setMana(120);
		setSpeed(12);
		setManaCost(20);
		setMaxHp(120);
	}

	@Override
	public String getWeapon() {

		return ClassLoader.getSystemResource("light1.png").toString();
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("wizard.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "magic.wav";
	}

}
