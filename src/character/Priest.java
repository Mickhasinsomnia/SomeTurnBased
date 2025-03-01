package character;

public class Priest extends GameCharacter {

	public Priest() {
		this.setHp(50);
		setMagic(20);
		setSpeed(12);
		imagePath=ClassLoader.getSystemResource("priest.png").toString();
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
