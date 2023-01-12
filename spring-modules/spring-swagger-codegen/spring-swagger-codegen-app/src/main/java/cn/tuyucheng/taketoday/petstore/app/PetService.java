package cn.tuyucheng.taketoday.petstore.app;


import cn.tuyucheng.taketoday.petstore.client.model.Pet;

import java.util.List;

public interface PetService {

	/**
	 * Find available pets
	 *
	 * @return List of available pets
	 */
	List<Pet> findAvailablePets();

	/**
	 * Find Pending pets
	 *
	 * @return List of pending pets
	 */
	List<Pet> findPendingPets();

	/**
	 * Find sold pets
	 *
	 * @return List of sold pets
	 */
	List<Pet> findSoldPets();
}