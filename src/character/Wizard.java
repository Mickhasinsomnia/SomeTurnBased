package character;

public class Wizard extends GameCharacter implements RangeCharacter{
	protected int manaCost;
	
	public Wizard() {
		this.setHp(120);
		this.setAttack(10);
		setMagic(50);
		setMana(200);
		setSpeed(12);
		setManaCost(15);
		self=ClassLoader.getSystemResource("wizard.png").toString();
		maxhp=hp;
	}

	public int getManaCost() {
		return manaCost;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

	@Override
	public void attack(GameCharacter target) {
		target.setHp(target.getHp()-getAttack());
	}

	@Override
	public void magic(GameCharacter target) {
		if(mana<=0)
			return;
			
		target.setHp(target.getHp()-getMagic());
		this.setMana(getMana()-getManaCost());
		
	}

	@Override
	public String getWeapon() {
		
		return ClassLoader.getSystemResource("arrow.png").toString();
	}

}
