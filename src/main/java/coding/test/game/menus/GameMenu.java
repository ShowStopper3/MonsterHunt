package coding.test.game.menus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import coding.test.game.characters.Player;
import coding.test.game.services.GameServices;
import coding.test.game.services.PlayerDeathException;
import coding.test.game.services.QueueServices;

/**
 * The first menu displayed on user screen
 * 
 * This menu lets the player choose whether to load an exiting game, start a new
 * one, or exit to the terminal.
 * 
 * @author ssachdev
 *
 */
public class GameMenu extends BaseMenu {

	public GameMenu() {
		start();
	}

	public void start() {
		this.menuItems.add(new MenuPojo("Start", "Starts a new Game", "new"));
		this.menuItems.add(new MenuPojo("Load", "Loads an existing Game"));
		this.menuItems.add(new MenuPojo("Delete", "Deletes an existing Game"));
		this.menuItems.add(new MenuPojo("Exit", null, "quit"));

		while (true) {
			try {
				MenuPojo selectedItem = showMenu(this.menuItems);
				boolean exit = getSelectedOption(selectedItem);
				if (!exit) {
					break;
				}
			} catch (PlayerDeathException e) {
				if (e.getLocalisedMessage().equals("close")) {
					break;
				}
			}
		}
		QueueServices.offer("EXIT");

	}

	private static boolean getSelectedOption(MenuPojo m) throws PlayerDeathException {
		String key = m.getKey();
		switch (key) {
		case "start":
			try {
				Path orig = Paths.get("json/original_data/locations.json");
				Path dest = Paths.get("json/locations.json");
				Files.copy(orig, dest, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				QueueServices.offer("Unable to load new locations file.");
				ex.printStackTrace();
			}
			new NewPlayerMenu();
			break;
		case "exit":
			QueueServices.offer("Goodbye!");
			return false;
		case "load":
			listProfiles();
			QueueServices.offer("\nWhat is the name of the avatar you want to load? Type 'back' to go back");
			Player player = null;
			boolean exit = false;
			while (player == null) {
				key = QueueServices.take();
				if (Player.profileExists(key)) {
					player = Player.load(key);
				} else if (key.equals("exit") || key.equals("back")) {
					exit = true;
					break;
				} else {
					QueueServices.offer("That user doesn't exist. Try again.");
				}
			}
			if (exit) {
				return true;
			}
			new GameServices(player, "old");
			break;
		case "delete":
			listProfiles();
			QueueServices.offer("\nWhich profile do you want to delete? Type 'back' to go back");
			exit = false;
			while (!exit) {
				key = QueueServices.take();
				if (Player.profileExists(key)) {
					String profileName = key;
					QueueServices.offer("Are you sure you want to delete " + profileName + "? y/n");
					key = QueueServices.take();
					if (key.equals("y")) {
						File profile = new File("json/profiles/" + profileName);
						deleteDirectory(profile);
						QueueServices.offer("Profile Deleted");
						return true;
					} else {
						listProfiles();
						QueueServices.offer("\nWhich profile do you want to delete?");
					}
				} else if (key.equals("exit") || key.equals("back")) {
					exit = true;
					break;
				} else {
					QueueServices.offer("That user doesn't exist. Try again.");
				}
			}
			break;
		}
		return true;
	}

	private static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	private static void listProfiles() {
		QueueServices.offer("Profiles:");
		File file = new File("json/profiles");
		String[] profiles = file.list();
		int i = 1;
		for (String name : profiles) {
			if (new File("json/profiles/" + name).isDirectory()) {
				QueueServices.offer("  " + i + ". " + name);
			}
			i += 1;
		}
	}
}
