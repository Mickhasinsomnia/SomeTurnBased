package character;

public class EnemyArcher extends Enemy implements RangeCharacter  {

	public EnemyArcher() {
		setSpeed(13);
		setAttack(25);
		setHp(80);
		setMana(0);
	}
	@Override
	public String getWeapon() {
		return ClassLoader.getSystemResource("enemyArrow.png").toString();
	}

}
