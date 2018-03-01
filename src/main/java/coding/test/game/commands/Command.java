package coding.test.game.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Command annotates a command method in the CommandCollection
 * 
 * @author ssachdev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String command();

	String aliases();

	String description();

}
