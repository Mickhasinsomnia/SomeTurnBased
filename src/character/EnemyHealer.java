package character;

public class EnemyHealer extends Enemy {

	public EnemyHealer() {
		setHp(80);
		setMagic(40);
		setAttack(20);
		setSpeed(5);
		maxhp=hp;
		imagePath=ClassLoader.getSystemResource("evilHealer.png").toString();
	}
	
	@Override
    public void magic(GameCharacter target) {
        
    }

}
