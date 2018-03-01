package coding.test.game.services;

import coding.test.game.repository.ItemRepository;
import coding.test.game.repository.LocationRepository;
import coding.test.game.repository.NpcRepository;

public final class RepositoryServices {

	public static ItemRepository getItemRepository() {
		return ItemRepository.createRepo();
	}

	public static LocationRepository getLocationRepository() {
		return LocationRepository.createRepo("");
	}

	public static LocationRepository getLocationRepository(String profile) {
		return LocationRepository.createRepo(profile);
	}

	public static NpcRepository getNpcRepository() {
		return NpcRepository.createRepo();
	}
}
