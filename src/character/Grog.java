package character;

public class Grog extends Enemy {

	public Grog() {
		setHp(250);
		setAttack(40);
		setMagic(0);
		setMana(0);
		setSpeed(50);
		setMaxHp(200);
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("goblin_boss.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "swoosh.wav";
	}

}
