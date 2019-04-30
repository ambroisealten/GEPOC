/**
 *
 */
package fr.alten.ambroiseJEE.model.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.google.gson.JsonElement;

import fr.alten.ambroiseJEE.model.beans.Data;

/**
 * @author Andy Chabalier
 *
 */
public interface DataRepository extends CrudRepository<Data, Integer> {

	/**
	 * return the list of all data with a number less than l
	 *
	 * @param l the top number to fetch
	 * @return the list of data with a number less or equal to l
	 * @author Andy Chabalier
	 */
	List<Data> findByNumberLessThanEqual(long l);

	/**
	 * @param nbToFetch
	 * @return
	 * @author Andy Chabalier
	 */
	JsonElement findFirst10ByName();

}