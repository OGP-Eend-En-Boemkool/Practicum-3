package filesystem;

import java.util.*;
import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;


/**
 * An abstract class of items.
 *
 * @invar	Each item must have a properly spelled name.
 * 			| isValidName(getName())
 * @invar   Each item must have a valid creation time.
 *          | isValidCreationTime(getCreationTime())
 * @invar   Each item must have a valid modification time.
 *          | canHaveAsModificationTime(getModificationTime())
 * @invar   Each item must have a valid parent directory.
 *          | hasProperParentDirectory()
 * 
 * @author 	Lotte en Linde    
 * @version	1.0
 * 
 */

public abstract class Item {
	

    /**********************************************************
     * Constructors
     **********************************************************/
    
	protected Item(String name) {
		setName(name);
	}
	
	protected Item(Directory parent, String name) throws IllegalArgumentException, DiskItemNotWritableException {
		if (parent == null) 
			throw new IllegalArgumentException();
		if (parent.isWritable() && isValidName(name) && parent.containsDiskItemWithName(name))
			throw new IllegalArgumentException();
		if (parent.isWritable() && !isValidName(name) && parent.containsDiskItemWithName(getDefaultName()))
			throw new IllegalArgumentException();
		if (!parent.isWritable()) 
			throw new DiskItemNotWritableException(parent);

		setName(name);
		setParentDirectory(parent);
		try {
			parent.addAsItem(this);
		} catch (DiskItemNotWritableException e) {
			//cannot occur
			assert false;
		} catch (IllegalArgumentException e) {
			//cannot occur
			assert false;
		}
	}

	/**********************************************************
	 * delete/termination
	 **********************************************************/

	/**
	 * Variable registering whether or not this item has been terminated.
	 */
	private boolean isTerminated = false;


	/**
	 * Check whether this item is already terminated.
	 */
	@Raw @Basic
	public boolean isTerminated() {
		return isTerminated;
	}


	/**
	 * Check whether this item can be terminated.
	 * 
	 * @return	True if the item is not yet terminated, is writable and it is either a root or
	 * 			its parent directory is writable
	 * 			| if (isTerminated() || !isWritable() || (!isRoot() && !getParentDirectory().isWritable()))
	 * 			| then result == false
	 * @note	This specification must be left open s.t. the subclasses can change it
	 */
	public boolean canBeTerminated(){
		return !isTerminated() && isWritable() && (isRoot() || getParentDirectory().isWritable());
	}

	/**
	 * Terminate this item.
	 * 
	 * @post 	This item is terminated.
	 *       	| new.isTerminated()
	 * @effect 	If this item is not terminated and it is not a root, it is made a root
	 * 			| if (!isTerminated() && !isRoot())  
	 * 			| then makeRoot()
	 * @throws 	IllegalStateException
	 * 		   	This item is not yet terminated and it can not be terminated.
	 * 		   	| !isTerminated() && !canBeTerminated()
	 */
	public void terminate() throws IllegalStateException {
		if(!isTerminated()){
			if (!canBeTerminated()) {
				throw new IllegalStateException("This item cannot be terminated");
			}
			if(!isRoot()){
				try{
					makeRoot();
				}catch(DiskItemNotWritableException e){
					//should not happen since this item and its parent are writable
					assert false;
				}
			}
			this.isTerminated = true;
		}
	}	

	/**********************************************************
	 * name - total programming
	 **********************************************************/

	/**
	 * Variable referencing the name of this item.
	 */
	private String name = null;

	/**
	 * Return the name of this item.
	 */
	@Raw @Basic 
	public String getName() {
		return name;
	}

	/**
	 * Check whether the given name is a legal name for a item.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	True if the given string is effective, not
	 * 			empty and consisting only of letters, digits,
	 * 			hyphens and underscores.
	 * 			| if(name != null) && name.matches("[a-zA-Z_0-9-]+")
	 * 			| then result == true
	 * @note	This specification must be left open s.t. the subclasses can change it
	 */
	public abstract boolean isValidName(String name);

	/**
	 * Set the name of this item to the given name.
	 *
	 * @param   name
	 * 			The new name for the item.
	 * @post    If the given name is valid, the name of
	 *          this item is set to the given name,
	 *          otherwise the name of the disk item is set to a valid name (the default).
	 *          | if (isValidName(name))
	 *          |      then new.getName().equals(name)
	 *          |      else new.getName().equals(getDefaultName())
	 */
	@Raw @Model 
	private void setName(String name) {
		if (isValidName(name)) {
			this.name = name;
		} else {
			this.name = getDefaultName();
		}
	}

	/**
	 * Return the name for a new item which is to be used when the
	 * given name is not valid.
	 *
	 * @return	A valid item name.
	 *         	| isValidName(result)
	 */
	@Model
	private static String getDefaultName() {
		return "new_item";
	}

	/**
	 * Check whether the name of this item can be changed into the
	 * given name.
	 * 
	 * @return  False if this item is terminated, the given 
	 *          name isn't a valid name for the specific item, this item
	 *          is not writable, the given name is not different from the current name of this item
	 *          or this item is not a root item and the parent directory does
	 *          already contain an other item with the given name;
	 *          | if(isTerminated() || !isWritable() || !isValidName(name) ||
	 *          | 	getName().equals(name) || ( !isRoot() && getParentDirectory().exists(name) )
	 *          | then result == false
	 * @note	This specification must be left open s.t. the subclasses can change it
	 */
	public abstract boolean canAcceptAsNewName(String name);

	/**
	 * Set the name of disk item to the given name.
	 *
	 * @param	name
	 * 			The new name for this item.
	 * @effect  If this item can accept the given name as
	 *          its name, the name of this item is set to
	 *          the given name.
	 *          Otherwise there is no change
	 *          | if (canAcceptAsNewName(name))
	 *          | then setName(name)
	 * @effect  If this item can accept the given name as
	 *          its new name, the modification time of this item is 
	 *          updated.
	 *          | if (canAcceptAsNewName(name))
	 *          | then setModificationTime()
	 * @effect  If this item is not a root item, the order of the items in the parent
	 * 			directory is restored given the new name
	 * 			| if (!isRoot()) then 
	 * 			|	getParentDirectory().restoreOrderAfterNameChangeAt(getParentDirectory().getIndexOf(this))
	 * @throws  ItemNotWritableException(this)
	 *          This disk item is not writable.
	 *          | !isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is already terminated
	 * 			| isTerminated()
	 */
	public void changeName(String name) throws DiskItemNotWritableException, IllegalStateException {
		if (isTerminated()) throw new IllegalStateException("Disk item terminated!");
		if (!isWritable()) throw new DiskItemNotWritableException(this);
		if (canAcceptAsNewName(name)) {
			setName(name);
			setModificationTime();
			if(!isRoot()){
				int currentIndexInParent = getParentDirectory().getIndexOf(this);
				getParentDirectory().restoreOrderAfterNameChangeAt(currentIndexInParent);
			}
		}
	}

	/**
	 * Checks whether the name of this item is lexicographically 
	 * ordered after the given name, ignoring case.
	 * 
	 * @param 	name
	 *       	The name to compare with
	 * @return 	True if the given name is effective and the name of this item 
	 * 			comes strictly after the given name (ignoring case), 
	 *         	false otherwise.
	 *       	| result == (name != null) && (getName().compareToIgnoreCase(name) > 0)
	 */
	public boolean isOrderedAfter(String name) {
		return (name != null) && (getName().compareToIgnoreCase(name) > 0);
	}

	/**
	 * Checks whether the name of this item is lexicographically 
	 * ordered before the given name, ignoring case.
	 * 
	 * @param 	name
	 *       	The name to compare with
	 * @return 	True if the given name is effective and the name of this item 
	 * 			comes strictly before the given name (ignoring case), 
	 *         	false otherwise.
	 *       	| result == (name != null) && (getName().compareToIgnoreCase(name) < 0)
	 */
	public boolean isOrderedBefore(String name) {
		return (name != null) && (getName().compareToIgnoreCase(name) < 0);
	}

	/**
	 * Checks whether this item is ordered after the given other item
	 * according to the lexicographic ordering of their names,
	 * ignoring case.
	 * 
	 * @param 	other
	 *        	The item to compare with
	 * @return 	True if the given other item is effective, and the name
	 *         	of this item is lexicographically ordered after the name
	 *         	of the given other item (ignoring case),
	 *         	false otherwise.
	 *       	| result == (other != null) && 
	 *       	|           isOrderedAfter(other.getName())
	 */
	public boolean isOrderedAfter(@Raw DiskItem other) {
		return (other != null) && isOrderedAfter(other.getName());
	}

	/**
	 * Checks whether this item is ordered before the given other item
	 * according to the lexicographic ordering of their names,
	 * ignoring case.
	 * 
	 * @param 	other
	 *        	The item to compare with
	 * @return 	True if the given other item is effective, and the name
	 *         	of this item is lexicographically ordered before the name
	 *         	of the given other item (ignoring case),
	 *         	false otherwise.
	 *       	| result == (other != null) && 
	 *       	|           isOrderedBefore(other.getName())
	 */
	public boolean isOrderedBefore(@Raw DiskItem other) {
		return (other != null) && isOrderedBefore(other.getName());
	}
	
	
}
