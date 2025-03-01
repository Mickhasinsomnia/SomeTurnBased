package character;

public class Priest extends GameCharacter {

	public Priest() {
		this.setHp(50);
		this.setAttack(65);
		setMagic(50);
		setSpeed(12);
		self=ClassLoader.getSystemResource("priest.png").toString();
		maxhp=hp;
	}
	
	@Override
	public void attack(GameCharacter target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void magic(GameCharacter target) {
		// TODO Auto-generated method stub
		
	}

}
