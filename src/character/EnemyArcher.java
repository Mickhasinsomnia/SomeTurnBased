package character;

public class EnemyArcher extends Enemy implements RangeCharacter  {

	public EnemyArcher() {
		setAttack(50);
		setHp(80);
		setMana(0);
	}
	@Override
	public String getWeapon() {
		return ClassLoader.getSystemResource("enemyArrow.png").toString();
	}

}
