package coding.test.game.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

import coding.test.game.characters.Player;
import coding.test.game.services.PlayerDeathException;
import coding.test.game.services.QueueServices;

/**
 * CommandParser parses the game commands
 *
 * It parses all the commands automatically. To add a new command, you just need
 * to make addition in the CommandCollection.
 *
 * @author ssachdev
 *
 */
public class CommandParser {
	Player player;
	private TreeMap<String, Method> commandMap;

	public CommandParser(Player player) {
		this.player = player;
		commandMap = new TreeMap<String, Method>();
		initCommandMap();
	}


	private void initCommandMap() {
		Method[] methods = CommandCollection.class.getMethods();

		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			Command annotation = method.getAnnotation(Command.class);
			this.commandMap.put(annotation.command(), method);
			for (String alias : annotation.aliases().split(",")) {
				if (alias.length() == 0) {
					break;
				}
				this.commandMap.put(alias, method);
			}
		}
	}

	public boolean parse(Player player, String userCommand) throws PlayerDeathException {
		CommandCollection com = CommandCollection.getInstance();
		com.initPlayer(player);

		if (userCommand.equals("exit")) {
			return false;
		}

		String command = removeNaturalText(userCommand);
		for (String key : commandMap.descendingKeySet()) {
			if (command.startsWith(key)) {
				Method method = commandMap.get(key);
				if (method.getParameterTypes().length == 0) {
					if (command.equals(key)) {
						try {

							method.invoke(com);
						} catch (IllegalAccessException | InvocationTargetException e) {
							if (e.getCause() instanceof PlayerDeathException) {
								throw (PlayerDeathException) e.getCause();
							} else {
								e.getCause().printStackTrace();
							}
						}
					} else {
						QueueServices.offer("I don't know what'" + userCommand + "' means.");
						return true;
					}
				} else if (method.getParameterTypes()[0] == String.class) {
					String arg = command.substring(key.length()).trim();
					try {

						method.invoke(com, arg);

					} catch (IllegalAccessException | InvocationTargetException e) {
						if (e.getCause() instanceof PlayerDeathException) {
							throw (PlayerDeathException) e.getCause();
						} else {
							e.getCause().printStackTrace();
						}
					}
				}
				return true;
			}
		}
		QueueServices.offer("I don't know what'" + userCommand + "' means.");
		return true;
	}

	private String removeNaturalText(String command) {
		command = command.replaceAll(" to ", " ");
		command = command.replaceAll(" a ", " ");
		return command;
	}
}
