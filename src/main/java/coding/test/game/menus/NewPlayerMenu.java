package coding.test.game.menus;

import coding.test.game.characters.Player;
import coding.test.game.services.GameServices;
import coding.test.game.services.PlayerDeathException;
import coding.test.game.services.QueueServices;

/**
 * Called when creating a new Player
 * 
 * @author ssachdev
 *
 */
public class NewPlayerMenu extends BaseMenu {

	public NewPlayerMenu() throws PlayerDeathException {
		this.menuItems.add(new MenuPojo("Recruit", "A soldier to guard the city of New Delhi"));
		this.menuItems.add(new MenuPojo("SewerRat", "A member of the underground of New Delhi"));

		while (true) {
			QueueServices.offer("Choose a Non Playing Charcter to get started with:");
			MenuPojo selectedItem = showMenu(this.menuItems);
			if (getPlayerType(selectedItem)) {
				break;
			}
		}
	}

	private static boolean getPlayerType(MenuPojo m) throws PlayerDeathException {
		String key = m.getKey();
		if (key.equals("recruit")) {
			Player player = Player.getInstance("recruit");
			new GameServices(player, "new");
			return true;
		} else if (key.equals("sewerrat")) {
		   QueueServices.offer("Sorry, Work in progress for sewerrat, you can play with recruit only :");
			return false;
		} else {
			return false;
		}
	}
}
