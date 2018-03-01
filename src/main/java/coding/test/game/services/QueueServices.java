package coding.test.game.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueServices {
	private static Logger logger = LoggerFactory.getLogger(QueueServices.class);
	public static BlockingQueue<String> queue = new LinkedBlockingQueue<>();
	public static BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
	public static DataOutputStream out;
	public static DataInputStream in;
	public static Socket server;

	public static void startMessenger() {
		logger.debug("startMessenger(  )");
	}

	public static BlockingQueue<String> getQueue() {
		return queue;
	}

	public static void offer(String message) {
		logger.debug("offer( " + message + " )");

		System.out.println(message);
	}


	public static String getInput(String message) {
		logger.debug("getInput( " + message + " )");
		String input = "";
		try {
			out.writeUTF(message + "END");
			input = in.readUTF();
		} catch (SocketException se) {
			logger.debug("Inside getInput( " + message + " )", se);
			input = "error";
		} catch (IOException ioe) {
			logger.debug("Inside getInput( " + message + " )", ioe);
			input = "error";
		}
		return input;
	}

	public static String take() {
		String message = null;

		Scanner input = null;
		try {
			input = new Scanner(System.in);
			message = input.nextLine();
		} catch (NoSuchElementException nsee) {
			nsee.printStackTrace();
		} catch (IllegalStateException ise) {
			ise.printStackTrace();
		}

		return message;
	}
}
