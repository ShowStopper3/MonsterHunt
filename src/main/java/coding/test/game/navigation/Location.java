package coding.test.game.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coding.test.game.characters.NonPlayingCharacter;
import coding.test.game.items.Item;
import coding.test.game.items.Storage;
import coding.test.game.monsters.Monster;
import coding.test.game.repository.ItemRepository;
import coding.test.game.repository.LocationRepository;
import coding.test.game.repository.NpcRepository;
import coding.test.game.repository.RepositoryException;
import coding.test.game.services.QueueServices;
import coding.test.game.services.RepositoryServices;

/**
 *
 * The location class mostly deals with getting and setting variables. It also
 * contains the method to print a location's details.
 *
 * @author ssachdev
 *
 */
public class Location implements ILocation {
	// @Resource
	protected static ItemRepository itemRepo = RepositoryServices.getItemRepository();
	protected static NpcRepository npcRepo = RepositoryServices.getNpcRepository();

	private Coordinate coordinate;
	private String title;
	private String description;
	private LocationType locationType;
	private int dangerRating;
	private Storage storage = new Storage();
	private List<NonPlayingCharacter> npcs = new ArrayList<>();
	private List<Monster> monsters = new ArrayList<>();

	public Location() {

	}

	public Location(Coordinate coordinate, String title, String description, LocationType locationType) {
		this.coordinate = coordinate;
		this.title = title;
		this.description = description;
		this.locationType = locationType;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public int getDangerRating() {
		return dangerRating;
	}

	public void setDangerRating(int dangerRating) {
		this.dangerRating = dangerRating;
	}

	// It checks each direction for an exit and adds it to the exits hashmap if it
	// exists.
	public Map<Direction, ILocation> getExits() {
		Map<Direction, ILocation> exits = new HashMap<Direction, ILocation>();
		ILocation borderingLocation;
		LocationRepository locationRepo = RepositoryServices.getLocationRepository();
		for (Direction direction : Direction.values()) {
			try {
				borderingLocation = locationRepo.getLocation(getCoordinate().getBorderingCoordinate(direction));
				if (borderingLocation.getCoordinate().getZ() == getCoordinate().getZ()) {
					exits.put(direction, borderingLocation);
				} else if (getLocationType().equals(LocationType.STAIRS)) {
					exits.put(direction, borderingLocation);
				}
			} catch (RepositoryException ex) {
				// Location does not exist so do nothing
			}
		}
		return exits;
	}

	public Storage getStorage() {
		return storage;
	}

	public List<Item> getItems() {
		return storage.getItems();
	}

	public void addNpcs(List<String> npcIds) {
		for (String npcId : npcIds) {
			addNpc(npcId);
		}
	}

	public void addNpc(String npcId) {
		npcs.add(npcRepo.getNpc(npcId));
	}

	public void removeNpc(NonPlayingCharacter npc) {
		for (int i = 0; i < npcs.size(); i++) {
			if (npcs.get(i).equals(npc)) {
				npcs.remove(i);
			}
		}
	}

	public List<NonPlayingCharacter> getNpcs() {
		return Collections.unmodifiableList(npcs);
	}

	public void addMonster(Monster monster) {
		if (monster != null) {
			monsters.add(monster);
		}
	}

	public void removeMonster(Monster monster) {
		for (int i = 0; i < monsters.size(); i++) {
			if (monsters.get(i).equals(monster)) {
				monsters.remove(i);
			}
		}
	}

	public List<Monster> getMonsters() {
		return monsters;
	}

	public Item removeItem(Item item) {
		return storage.remove(item);
	}

	public void addItem(Item item) {
		storage.add(item);
	}

	public void print() {
		QueueServices.offer("\n" + getTitle() + ":");
		QueueServices.offer("    " + getDescription());
		List<Item> items = getItems();
		if (!items.isEmpty()) {
			QueueServices.offer("Items:");
			for (Item item : items) {
				QueueServices.offer("    " + item.getName());
			}
		}
		List<NonPlayingCharacter> npcs = getNpcs();
		if (!npcs.isEmpty()) {
			QueueServices.offer("NPCs:");
			for (NonPlayingCharacter npc : npcs) {
				QueueServices.offer("   " + npc.getName());
			}
		}
		QueueServices.offer("");
		for (Map.Entry<Direction, ILocation> direction : getExits().entrySet()) {
			QueueServices.offer(direction.getKey().getDescription() + ": ");
			QueueServices.offer("    " + direction.getValue().getDescription());
		}
	}
}
