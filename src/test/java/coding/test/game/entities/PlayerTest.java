package coding.test.game.entities;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.Test;

import coding.test.game.characters.Player;

public class PlayerTest {

    @Before
    public void copyFiles() {
        File source = new File("json/original_data/locations.json");
        File dest = new File("json/locations.json");
        try {
            Files.copy(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void newRecruit() {
        Player player = Player.getInstance("recruit");
        int expected = 1;
        int actual = player.getArmour();
        assertEquals("Failure - new recruit not properly created", expected, actual);
    }
    
    @Test
    public void newSewerRat() {
        Player player = Player.getInstance("sewerrat");
        int expected = 0;
        int actual = player.getArmour();
        assertEquals("Failure - new recruit not properly created", expected, actual);
    }

    @Test
    public void oldPlayer() {
        Player player = Player.load("test");
        String expected = "test";
        String actual = player.getName();
        assertEquals("Failure - old player not properly loaded", expected, actual);
    }
}
