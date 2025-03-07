package character;

public class Necromancer extends Enemy {

	public Necromancer() {
		setHp(80);
		setMagic(40);
		setAttack(20);
		setSpeed(5);
		setMaxHp(80);
		setMana(120);
		setManaCost(20);
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("necromancer.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "heal.wav";
	}

}
