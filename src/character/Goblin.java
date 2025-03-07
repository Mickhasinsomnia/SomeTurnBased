package character;


public  class Goblin extends Enemy {

	public Goblin() {
		setHp(120);
		setAttack(20);
		setMaxHp(120);
		setSpeed(10);
	}


	@Override
	public String getImagePath() {
		return ClassLoader.getSystemResource("goblin_normal.png").toString();
	}

	@Override
	public String getSoundEffect() {
		return "punch.wav";
	}



	








}
