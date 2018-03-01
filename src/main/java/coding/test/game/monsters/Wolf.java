package coding.test.game.monsters;

/**
 * 
 * A monster without low damage, but high health and armour.
 * 
 * @author ssachdev
 *
 */
public class Wolf extends Monster {
	public Wolf(int playerLevel) {
		this.monsterType = "Wolf";
		this.setHealthMax(35 + playerLevel * 3);
		this.setHealth(35 + playerLevel * 3);
		this.setArmour(0);
		this.setDamage(15 + playerLevel * 2.5);
		this.setCritChance(0.04);
		this.setIntelligence(2);
		this.setStealth(4);
		this.setDexterity(2);
		this.setXPGain(25 + playerLevel * 3);
		this.setGold(playerLevel * 2);
		addRandomItems(playerLevel, "fram1");
	}
}
