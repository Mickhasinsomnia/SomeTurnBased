package character;

public class Archer extends GameCharacter implements RangeCharacter {

	public Archer() {
		setHp(80);
		setAttack(30);
		setSpeed(14);
		setMaxHp(80);
	}

	@Override
	public String getWeapon() {
		return ClassLoader.getSystemResource("arrow.png").toString();
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("archer.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "arrow.wav";
	}

}
