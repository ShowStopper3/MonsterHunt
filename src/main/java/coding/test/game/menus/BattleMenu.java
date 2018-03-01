package coding.test.game.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import coding.test.game.characters.BaseCharcter;
import coding.test.game.characters.NonPlayingCharacter;
import coding.test.game.characters.Player;
import coding.test.game.items.Item;
import coding.test.game.items.ItemStack;
import coding.test.game.monsters.Monster;
import coding.test.game.services.CharcterServices;
import coding.test.game.services.PlayerDeathException;
import coding.test.game.services.QueueServices;
import coding.test.game.services.RepositoryServices;

public class BattleMenu extends BaseMenu {

	private Monster monsterOpponent;
	private NonPlayingCharacter npcOpponent;
	private Player player;
	private Random random;
	private int armour;
	private double damage;
	private int escapeSuccessfulAttempts = 0;

	public BattleMenu(Monster monsterOpponent, Player player) throws PlayerDeathException {
		this.random = new Random();
		this.monsterOpponent = monsterOpponent;
		this.player = player;
		this.menuItems.add(new MenuPojo("Attack", "Attack " + monsterOpponent.getName() + "."));
		this.menuItems.add(new MenuPojo("Defend", "Defend against " + monsterOpponent.getName() + "'s attack."));
		this.menuItems.add(new MenuPojo("Escape", "Try and escape from " + monsterOpponent.getName()));
		this.menuItems.add(new MenuPojo("Equip", "Equip an item"));
		this.menuItems.add(new MenuPojo("Unequip", "Unequip an item"));
		this.menuItems.add(new MenuPojo("View", "View details about your character"));
		this.armour = player.getArmour();
		this.damage = player.getDamage();
		while (monsterOpponent.getHealth() > 0 && player.getHealth() > 0 && (escapeSuccessfulAttempts <= 0)) {
			QueueServices.offer("\nWhat is your choice?");
			MenuPojo selectedItem = showMenu(this.menuItems);
			testSelected(selectedItem);
		}
		if (player.getHealth() == 0) {
			QueueServices.offer("You died... Start again? (y/n)");
			String reply = QueueServices.take().toLowerCase();
			while (!reply.startsWith("y") && !reply.startsWith("n")) {
				QueueServices.offer("You died... Start again? (y/n)");
				reply = QueueServices.take().toLowerCase();
			}
			if (reply.startsWith("y")) {
				throw new PlayerDeathException("restart");
			} else if (reply.startsWith("n")) {
				throw new PlayerDeathException("close");
			}
		} else if (monsterOpponent.getHealth() == 0) {
			int xp = monsterOpponent.getXPGain();
			this.player.setXP(this.player.getXP() + xp);
			int oldLevel = this.player.getLevel();
			int newLevel = (int) (0.075 * Math.sqrt(this.player.getXP()) + 1);
			this.player.setLevel(newLevel);

			// Iterates over monster's items and if there are any, drops them.
			// There are two loops due to a ConcurrentModification Exception that occurs
			// if you try to remove the item while looping through the monster's items.
			List<ItemStack> itemStacks = monsterOpponent.getStorage().getItemStack();
			List<String> itemIds = new ArrayList<>();
			for (ItemStack itemStack : itemStacks) {
				String itemId = itemStack.getItem().getId();
				itemIds.add(itemId);
			}
			for (String itemId : itemIds) {
				Item item = RepositoryServices.getItemRepository().getItem(itemId);
				monsterOpponent.removeItemFromStorage(item);
				this.player.getLocation().addItem(item);
				QueueServices.offer("Your opponent dropped a " + item.getName());
			}

			this.player.getLocation().removeMonster(monsterOpponent);
			this.player.setGold(this.player.getGold() + monsterOpponent.getGold());
			QueueServices.offer("You killed a " + monsterOpponent.getName() + "\nYou have gained " + xp + " XP and "
					+ monsterOpponent.getGold() + " gold");
			if (oldLevel < newLevel) {
				QueueServices.offer("You've are now level " + newLevel + "!");
			}
			CharcterServices cc = new CharcterServices();
			cc.checkForCharcterChange(this.player, "kill", monsterOpponent.getName());
		}
	}

	private void testSelected(MenuPojo m) {
		switch (m.getKey()) {
		case "attack": {
			mutateStats(1, 0.5);
			if (npcOpponent == null) {
				attack(player, monsterOpponent);
				attack(monsterOpponent, player);
			} else {
				attack(player, npcOpponent);
				attack(npcOpponent, player);
			}
			resetStats();
			break;
		}
		case "defend": {
			mutateStats(0.5, 1);
			if (npcOpponent == null) {
				QueueServices.offer("\nYou get ready to defend against the " + monsterOpponent.getName() + ".");
				attack(player, monsterOpponent);
				attack(monsterOpponent, player);
			} else {
				QueueServices.offer("\nYou get ready to defend against the " + npcOpponent.getName() + ".");
				attack(player, npcOpponent);
				attack(npcOpponent, player);
			}
			resetStats();
			break;
		}
		case "escape": {
			if (npcOpponent == null) {
				escapeSuccessfulAttempts = escapeAttempt(player, monsterOpponent, escapeSuccessfulAttempts);
			} else {
				escapeSuccessfulAttempts = escapeAttempt(player, npcOpponent, escapeSuccessfulAttempts);
			}
			break;
		}
		case "equip": {
			equip();
			break;
		}
		case "unequip": {
			unequip();
			break;
		}
		case "view": {
			viewStats();
			break;
		}
		default: {
			break;
		}
		}
	}

	private int escapeAttempt(Player player, BaseCharcter attacker, int escapeAttempts) {
		if (escapeAttempts == -10) {
			escapeAttempts = 0;
		}
		double playerEscapeLevel = player.getIntelligence() + player.getStealth() + player.getDexterity();
		double attackerEscapeLevel = attacker.getIntelligence() + attacker.getStealth() + attacker.getDexterity()
				+ (attacker.getDamage() / playerEscapeLevel);
		double escapeLevel = playerEscapeLevel / attackerEscapeLevel;

		Random rand = new Random();
		int rawLuck = rand.nextInt(player.getLuck() * 2) + 1;
		int lowerBound = 60 - rawLuck;
		int upperBound = 80 - rawLuck;
		double minEscapeLevel = (rand.nextInt((upperBound - lowerBound) + 1) + lowerBound) / 100.0;

		if (escapeLevel > minEscapeLevel && (escapeAttempts == 0)) {
			QueueServices.offer("You have managed to escape the: " + attacker.getName());
			return 1;
		} else if (escapeAttempts < 0) {
			QueueServices.offer("You have tried to escape too many times!");
			return escapeAttempts - 1;

		} else {
			QueueServices.offer("You failed to escape the: " + attacker.getName());
			return escapeAttempts - 1;
		}
	}

	private void attack(BaseCharcter attacker, BaseCharcter defender) {
		if (attacker.getHealth() == 0) {
			return;
		}
		double damage = attacker.getDamage();
		double critCalc = random.nextDouble();
		if (critCalc < attacker.getCritChance()) {
			damage += damage;
			QueueServices.offer("Crit hit! Damage has been doubled!");
		}
		int healthReduction = (int) ((((3 * attacker.getLevel() / 50 + 2) * damage * damage / (defender.getArmour() + 1)
				/ 100) + 2) * (random.nextDouble() + 1));
		defender.setHealth((defender.getHealth() - healthReduction));
		if (defender.getHealth() < 0) {
			defender.setHealth(0);
		}
		QueueServices.offer(healthReduction + " damage dealt!");
		if (attacker instanceof Player) {
			QueueServices.offer("The " + defender.getName() + "'s health is " + defender.getHealth());
		} else {
			QueueServices.offer("Your health is " + defender.getHealth());
		}
	}

	private void mutateStats(double damageMult, double armourMult) {
		armour = player.getArmour();
		damage = player.getDamage();
		player.setArmour((int) (armour * armourMult));
		player.setDamage(damage * damageMult);
	}

	private void resetStats() {
		player.setArmour(armour);
		player.setDamage(damage);
	}

	private void equip() {
		player.printStorage();
		QueueServices.offer("What item do you want to use?");
		String itemName = QueueServices.take();
		if (!itemName.equalsIgnoreCase("back")) {
			player.equipItem(itemName);
		}
	}

	private void unequip() {
		player.printEquipment();
		QueueServices.offer("What item do you want to unequip?");
		String itemName = QueueServices.take();
		if (!itemName.equalsIgnoreCase("back")) {
			player.dequipItem(itemName);
		}
	}

	private void viewStats() {
		QueueServices.offer("\nWhat is your command? ex. View stats(vs), View Backpack(vb), View Equipment(ve) ");
		String input = QueueServices.take();
		switch (input) {
		case "vs":
		case "viewstats":
			player.getStats();
			break;
		case "ve":
		case "viewequipped":
			player.printEquipment();
			break;
		case "vb":
		case "viewbackpack":
			player.printStorage();
			break;
		case "back":
		case "exit":
			break;
		default:
			viewStats();
			break;
		}
	}
}
