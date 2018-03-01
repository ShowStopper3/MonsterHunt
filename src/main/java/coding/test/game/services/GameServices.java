package coding.test.game.services;

import java.util.ArrayList;

import coding.test.game.characters.Player;
import coding.test.game.commands.CommandParser;
import coding.test.game.monsters.Monster;
import coding.test.game.monsters.MonsterFactory;
import coding.test.game.repository.LocationRepository;

/**
 * 
 * This class contains the main loop that takes the input and does the according
 * actions. 
 * 
 *  @author ssachdev
 *
 */
public class GameServices {
	public ArrayList<Monster> monsterList = new ArrayList<Monster>();
	public MonsterFactory monsterFactory = new MonsterFactory();
	public CommandParser parser;
	public Monster monster;
	Player player = null;

	public GameServices(Player player, String playerType) throws PlayerDeathException {
		this.parser = new CommandParser(player);
		this.player = player;
		switch (playerType) {
		case "new":
			newGameStart(player);
			break;
		case "old":
			QueueServices.offer("Welcome back, " + player.getName() + "!");
			QueueServices.offer("");
			player.getLocation().print();
			gamePrompt(player);
			break;
		default:
			QueueServices.offer("Invalid player type");
			break;
		}
	}

	/**
	 * Starts a new game. It prints the introduction text first and asks for the
	 * name of the player's character. After that, it goes to
	 * the normal game prompt.
	 */
	public void newGameStart(Player player) throws PlayerDeathException {
		QueueServices.offer(player.getIntro());
		String userInput = QueueServices.take();
		player.setName(userInput);
		LocationRepository locationRepo = RepositoryServices.getLocationRepository(player.getName());
		this.player.setLocation(locationRepo.getInitialLocation());
		player.save();
		QueueServices.offer("Welcome to New Delhi, " + player.getName() + ".");
		player.getLocation().print();
		gamePrompt(player);
	}

	/**
	 * This is the main loop for the player-game interaction. It gets input from the
	 * command line and checks if it is a recognised command.
	 *
	 * This keeps looping as long as the player didn't type an exit command.
	 */
	public void gamePrompt(Player player) throws PlayerDeathException {
		boolean continuePrompt = true;
		try {
			while (continuePrompt) {
				QueueServices.offer("\n Enter Command:");
				String command = QueueServices.take().toLowerCase();
				continuePrompt = parser.parse(player, command);
			}
		} catch (PlayerDeathException e) {
			if (e.getLocalisedMessage().equals("replay")) {
				return;
			} else {
				throw e;
			}
		}
	}
}
