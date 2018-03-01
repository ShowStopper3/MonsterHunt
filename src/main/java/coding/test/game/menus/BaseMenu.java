package coding.test.game.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coding.test.game.services.QueueServices;

/**
 *
 * All menus in extend this class Add MenuItems to menuItems, call showMenu for
 * menu
 * 
 * @author ssachdev
 *
 */
public class BaseMenu {
	protected List<MenuPojo> menuItems = new ArrayList<>();
	protected Map<String, MenuPojo> commandMap = new HashMap<String, MenuPojo>();

	public MenuPojo showMenu(List<MenuPojo> m) {
		int i = 1;
		for (MenuPojo menuItem : m) {
			commandMap.put(String.valueOf(i), menuItem);
			commandMap.put(menuItem.getKey(), menuItem);
			for (String command : menuItem.getAltCommands()) {
				commandMap.put(command.toLowerCase(), menuItem);
			}
			i++;
		}
		MenuPojo selectedItem = selectMenu(m);
		return selectedItem;
	}

	// calls for user input from command line
	protected MenuPojo selectMenu(List<MenuPojo> m) {
		this.printMenuItems(m);
		String command = QueueServices.take();
		if (commandMap.containsKey(command.toLowerCase())) {
			return commandMap.get(command.toLowerCase());
		} else {
			QueueServices.offer("I don't know what '" + command + "' means.");
			return this.showMenu(m);
		}
	}

	private void printMenuItems(List<MenuPojo> m) {
		int i = 1;
		for (MenuPojo menuItem : m) {
			if (menuItem.getDescription() != null) {
				QueueServices.offer("[" + i + "] " + menuItem.getCommand() + " - " + menuItem.getDescription());
			} else {
				QueueServices.offer("[" + i + "] " + menuItem.getCommand());
			}
			i++;
		}
	}
}
