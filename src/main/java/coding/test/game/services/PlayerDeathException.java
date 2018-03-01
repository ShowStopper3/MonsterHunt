package coding.test.game.services;

public class PlayerDeathException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;

    public PlayerDeathException(String message) {
        super(message);
        this.message = message;
    }

    public String getLocalisedMessage() {
        return message;
    }
}
