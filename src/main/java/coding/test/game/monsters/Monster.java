package coding.test.game.monsters;

import java.util.List;
import java.util.Arrays;
import java.util.Random;

import coding.test.game.characters.BaseCharcter;
import coding.test.game.items.Item;
import coding.test.game.repository.ItemRepository;
import coding.test.game.services.RepositoryServices;

/**
 *
 * This class just holds a type of monster that is further outlined in its
 * respective file. For now it just holds the monster's name.
 *
 * @author ssachdev
 *
 */
public abstract class Monster extends BaseCharcter {
	public String monsterType;
	private int xpGain;
	private ItemRepository itemRepo = RepositoryServices.getItemRepository();

	public int getXPGain() {
		return xpGain;
	}

	public void setXPGain(int xpGain) {
		this.xpGain = xpGain;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Monster) {
			Monster m = (Monster) obj;
			return m.monsterType.equals(this.monsterType);
		}
		return false;
	}

	public void addRandomItems(int playerLevel, String... children) {
		List<String> itemList = Arrays.asList(children);
		Random rand = new Random();

		int numItems = 1;
		int i = 0;
		while (i != numItems) {
			for (String itemName : itemList) {
				if (i == numItems) {
					break;
				}

				int j = rand.nextInt(5) + 1;
				if (j == 1) {
					Item item = itemRepo.getItem(itemName);
					addItemToStorage(item);
					i++;
				}
			}
		}
	}
}
