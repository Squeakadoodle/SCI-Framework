package scilib.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import scilib.checkers.Filter;
import scilib.utilities.TeamDataComparator;

/**
 * Organizer which manages TeamData objects.
 * You can add and remove TeamData objects to this object, and set filters and sorters to organize them.
 * TeamData objects which are being filtered and sorted are part of the TeamData Pool.
 * TeamData objects from the pool which have passed through the Sorter and Filter are part of the active TeamData List.
 * @author Squeakadoodle
 *
 */
public class Group {
	
	private ArrayList<TeamData> originalList;
	private ArrayList<TeamData> currentList;
	
	public String name;
	public Sorter sorter;
	public Filter filter;
	
	/**
	 * Construct a Group object with a given name and no TeamData objects.
	 * @param name Name by which to reference the Group.
	 */
	public Group(String name) {
		this(name, new ArrayList<TeamData>());
	}
	
	/**
	 * Construct a Group object with a given name and an initial ArrayList of TeamData objects.
	 * @param name String name by which to reference the Group.
	 * @param list ArrayList of TeamData objects the Group starts with.
	 */
	public Group(String name, ArrayList<TeamData> list) {
		this.originalList = new ArrayList<TeamData>(list);
		this.currentList = new ArrayList<TeamData>();
		
		this.name = name;
		this.sorter = new Sorter();
		this.filter = new Filter();
	}
	
	/**
	 * Get the TeamData objects which are a part of the active TeamData List.
	 * @return Shallow copy of the ArrayList of TeamData objects which are part of the active TeamData List.
	 */
	public ArrayList<TeamData> getTeamList() {
		update();
		return new ArrayList<TeamData>(currentList);
	}
	
	/**
	 * Get the pool of TeamData objects which are being sorted and filtered.
	 * @return Shallow copy of the ArrayList of TeamData objects which are being sorted and filtered.
	 */
	public ArrayList<TeamData> getTeamPool() {
		return new ArrayList<TeamData>(originalList);
	}
	/**
	 * Test if a TeamData object is in active TeamData List.
	 * @param td TeamData object to be tested.
	 * @return Boolean value representing whether the TeamData object is in the active TeamData List.
	 */
	public boolean teamListContains(TeamData td) {
		update();
		return currentList.contains(td);
	}
	
	/**
	 * Test if a TeamData object is in the TeamData Pool.
	 * @param td TeamData object to be tested.
	 * @return Boolean value representing whether the TeamData object is in the TeamData Pool.
	 */
	public boolean teamPoolContains(TeamData td) {
		return originalList.contains(td);
	}
	
	/**
	 * Add a Collection of TeamData objects to the TeamData Pool.
	 * @param teamDataCollection Collection of TeamData objects to add.
	 * @return Boolean value representing whether the TeamData Pool was modified.
	 */
	public boolean addAll(Collection<TeamData> teamDataCollection) {
		boolean success = false;
		for( TeamData td : teamDataCollection ) {
			success |= add(td);
		}
		return success;
	}
	
	/**
	 * Add a TeamData object to the TeamData Pool.
	 * @param td TeamData object to add.
	 * @return Boolean value representing whether the TeamData Pool was modified.
	 */
	public boolean add(TeamData td) {
		if( !originalList.contains(td) ) {
			originalList.add(td);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove a TeamData object from the TeamData Pool
	 * @param td TeamData object to remove.
	 * @return Boolean value representing whether the TeamData Pool was modified.
	 */
	public boolean remove(TeamData td) {
		if( originalList.contains(td) ) {
			originalList.remove(td);
			return true;
		}
		return false;
	}
	
	/**
	 * Set the TeamData Pool to a given ArrayList of TeamData objects.
	 * @param teamDataList ArrayList of TeamData objects to be set to.
	 */
	public void set(ArrayList<TeamData> teamDataList) {
		originalList = new ArrayList<TeamData>(teamDataList);
	}
	
	/**
	 * Reset the Sorter and Filter of this Group, reverting the active TeamData List to the current TeamData Pool.
	 */
	public void reset() {
		sorter = new Sorter();
		filter.reset();
	}
	
	/**
	 * Remove all TeamData objects from the TeamData Pool.
	 */
	public void clear() {
		originalList = new ArrayList<TeamData>();
	}
	
	private void update() {
		ArrayList<TeamData> newList = new ArrayList<TeamData>();
		for( TeamData td : originalList ) {
			if(filter.check(td)) {
				newList.add(td);
			}
		}
		TeamData[] newArray = newList.toArray(new TeamData[newList.size()]);
		Arrays.sort(newArray, new TeamDataComparator(sorter));
		currentList = new ArrayList<TeamData>(Arrays.asList(newArray));
	}
}
