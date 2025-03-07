package character;

public class GoblinRanger extends Enemy implements RangeCharacter {

	public GoblinRanger() {
		setSpeed(13);
		setAttack(25);
		setHp(80);
		setMana(0);
		setMaxHp(80);
	}

	@Override
	public String getWeapon() {
		return ClassLoader.getSystemResource("enemyArrow.png").toString();
	}

	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("goblin_archer.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "arrow.wav";
	}
	
	


}
