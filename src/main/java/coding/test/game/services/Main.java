package coding.test.game.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coding.test.game.menus.GameMenu;


public class Main {
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("Starting Monster Search ...");
		new GameMenu();
	}

	
}
