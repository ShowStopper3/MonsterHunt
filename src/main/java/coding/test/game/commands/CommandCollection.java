package coding.test.game.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coding.test.game.characters.NonPlayingCharacter;
import coding.test.game.characters.Player;
import coding.test.game.conversation.ConversationServices;
import coding.test.game.monsters.Monster;
import coding.test.game.monsters.MonsterFactory;
import coding.test.game.navigation.Direction;
import coding.test.game.navigation.ILocation;
import coding.test.game.navigation.LocationType;
import coding.test.game.repository.ItemRepository;
import coding.test.game.services.PlayerDeathException;
import coding.test.game.services.QueueServices;
import coding.test.game.services.RepositoryServices;

/**
 * CommandCollection contains the declaration of the methods mapped to game
 * commands
 *
 * The declared command methods are accessed only by reflection. To declare a
 * new command, add an appropriate method to this class and Annotate it with
 * Command(command, aliases, description)
 * 
 * 
 * @author ssachdev
 *
 */
public enum CommandCollection {
	INSTANCE;

	private Logger logger = LoggerFactory.getLogger(CommandCollection.class);

	public Player player;

	private final static Map<String, String> DIRECTION_LINKS = new HashMap<String, String>();
	static {
		DIRECTION_LINKS.put("n", "north");
		DIRECTION_LINKS.put("s", "south");
		DIRECTION_LINKS.put("e", "east");
		DIRECTION_LINKS.put("w", "west");
		DIRECTION_LINKS.put("u", "up");
		DIRECTION_LINKS.put("d", "down");
	}

	public static CommandCollection getInstance() {
		return INSTANCE;
	}

	public void initPlayer(Player player) {
		this.player = player;
	}


	@Command(command = "help", aliases = "h", description = "Prints help")
	public void command_help() {
		Method[] methods = CommandCollection.class.getMethods();
		int commandWidth = 0;
		int descriptionWidth = 0;
		QueueServices.offer("");
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			Command annotation = method.getAnnotation(Command.class);
			String command = annotation.command() + "( " + annotation.aliases() + "):";
			String description = annotation.description();
			if (command.length() > commandWidth) {
				commandWidth = command.length();
			}
			if (description.length() > descriptionWidth) {
				descriptionWidth = description.length();
			}
		}
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			Command annotation = method.getAnnotation(Command.class);
			String command = (annotation.aliases().length() == 0) ? annotation.command()
					: annotation.command() + " (" + annotation.aliases() + "):";
			String message = String.format("%-" + commandWidth + "s %-" + descriptionWidth + "s", command,
					annotation.description());

			QueueServices.offer(message);

		}
	}

	@Command(command = "save", aliases = "s", description = "Save the game")
	public void command_save() {
		logger.info("Command 'save' is running");
		player.save();
	}

	@Command(command = "monster", aliases = "m", description = "Monsters around you")
	public void command_m() {
		List<Monster> monsterList = player.getLocation().getMonsters();
		if (monsterList.size() > 0) {
			QueueServices.offer("Monsters around you:");
			QueueServices.offer("----------------------------");
			for (Monster monster : monsterList) {
				QueueServices.offer(monster.monsterType);
			}
			QueueServices.offer("----------------------------");
		} else {
			QueueServices.offer("There are no monsters around you'n");
		}
	}

	@Command(command = "go", aliases = "g", description = "Goto a direction")
	public void command_g(String arg) throws PlayerDeathException {
		ILocation location = player.getLocation();

		try {
			arg = DIRECTION_LINKS.get(arg);
			Direction direction = Direction.valueOf(arg.toUpperCase());
			Map<Direction, ILocation> exits = location.getExits();
			if (exits.containsKey(direction)) {
				ILocation newLocation = exits.get(Direction.valueOf(arg.toUpperCase()));
				if (!newLocation.getLocationType().equals(LocationType.WALL)) {
					player.setLocation(newLocation);
					player.getLocation().print();
					Random random = new Random();
					if (player.getLocation().getMonsters().size() == 0) {
						MonsterFactory monsterFactory = new MonsterFactory();
						int upperBound = random.nextInt(player.getLocation().getDangerRating() + 1);
						for (int i = 0; i < upperBound; i++) {
							Monster monster = monsterFactory.generateMonster(player);
							player.getLocation().addMonster(monster);
						}
					}
					if (player.getLocation().getItems().size() == 0) {
						int chance = random.nextInt(100);
						if (chance < 60) {
							addItemToLocation();
						}
					}
					if (random.nextDouble() < 0.5) {
						List<Monster> monsters = player.getLocation().getMonsters();
						if (monsters.size() > 0) {
							int posMonster = random.nextInt(monsters.size());
							String monster = monsters.get(posMonster).monsterType;
							QueueServices.offer("A " + monster + " is attacking you!");
							player.attack(monster);
						}
					}
				} else {
					QueueServices.offer("You cannot walk through walls.");
				}
			} else {
				QueueServices.offer("The is no exit that way.");
			}
		} catch (IllegalArgumentException ex) {
			QueueServices.offer("That direction doesn't exist");
		} catch (NullPointerException ex) {
			QueueServices.offer("That direction doesn't exist");
		}
	}

	@Command(command = "inspect", aliases = "i", description = "Inspect an item")
	public void command_i(String arg) {
		player.inspectItem(arg.trim());
	}

	@Command(command = "equip", aliases = "e", description = "Equip an item")
	public void command_e(String arg) {
		player.equipItem(arg.trim());
	}

	@Command(command = "unequip", aliases = "ue", description = "Unequip an item")
	public void command_ue(String arg) {
		player.dequipItem(arg.trim());
	}

	@Command(command = "view", aliases = "v", description = "View details for 'stats' or 'equipped' ")
	public void command_v(String arg) {
		arg = arg.trim();
		switch (arg) {
		case "s":
		case "stats":
			player.getStats();
			break;
		case "e":
		case "equipped":
			player.printEquipment();
			break;
		
		default:
			QueueServices.offer("That is not a valid display");
			break;
		}
	}

	@Command(command = "pick", aliases = "p", description = "Pick up an item")
	public void command_p(String arg) {
		player.pickUpItem(arg.trim());
	}

	@Command(command = "drop", aliases = "d", description = "Drop an item")
	public void command_d(String arg) {
		player.dropItem(arg.trim());
	}

	@Command(command = "attack", aliases = "a", description = "Attacks an entity")
	public void command_a(String arg) throws PlayerDeathException {
		player.attack(arg.trim());
	}

	@Command(command = "lookaround", aliases = "la", description = "Displays the description of the room you are in.")
	public void command_la() {
		player.getLocation().print();
	}

	@Command(command = "talk", aliases = "t", description = "Talks to a character.")
	public void command_talk(String arg) throws PlayerDeathException {
		ConversationServices cm = new ConversationServices();
		List<NonPlayingCharacter> npcs = player.getLocation().getNpcs();
		NonPlayingCharacter npc = null;
		for (NonPlayingCharacter i : npcs) {
			if (i.getName().equalsIgnoreCase(arg)) {
				npc = i;
			}
		}
		if (npc != null) {
			cm.startConversation(npc, player);
		} else {
			QueueServices.offer("Unable to talk to " + arg);
		}
	}

	private void addItemToLocation() {
		ItemRepository itemRepo = RepositoryServices.getItemRepository();
		if (player.getHealth() < player.getHealthMax() / 3) {
			player.getLocation().addItem(itemRepo.getRandomFood(player.getLevel()));
		} else {
			Random rand = new Random();
			int startIndex = rand.nextInt(3);
			switch (startIndex) {
			case 0:
				player.getLocation().addItem(itemRepo.getRandomWeapon(player.getLevel()));
				break;
			case 1:
				player.getLocation().addItem(itemRepo.getRandomFood(player.getLevel()));
				break;
			case 2:
				player.getLocation().addItem(itemRepo.getRandomArmour(player.getLevel()));
				break;
			case 3:
				player.getLocation().addItem(itemRepo.getRandomPotion(player.getLevel()));
				break;
			}
		}
	}
}
