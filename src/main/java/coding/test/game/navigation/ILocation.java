package coding.test.game.navigation;

import java.util.List;
import java.util.Map;

import coding.test.game.characters.NonPlayingCharacter;
import coding.test.game.items.Item;
import coding.test.game.items.Storage;
import coding.test.game.monsters.Monster;

/**
 * This interface maps all the properties and methods that pertain to a specific
 * location.
 * 
 * @author ssachdev
 *
 */
public interface ILocation {
	Coordinate getCoordinate();

	String getTitle();

	String getDescription();

	LocationType getLocationType();

	List<Item> getItems();

	Storage getStorage();

	void addItem(Item item);

	Item removeItem(Item item);

	List<NonPlayingCharacter> getNpcs();

	List<Monster> getMonsters();

	void addMonster(Monster monster);

	void removeMonster(Monster monster);

	void addNpcs(List<String> npcIds);

	void addNpc(String npcID);

	void removeNpc(NonPlayingCharacter npc);

	int getDangerRating();

	void setDangerRating(int dangerRating);

	Map<Direction, ILocation> getExits();

	void print();
}
