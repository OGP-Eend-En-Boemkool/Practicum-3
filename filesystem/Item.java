
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
    
	public Item(String name) {
		setName(name);
	}
	
	public Item(Directory parent, String name) throws IllegalArgumentException, ItemNotWritableException {
		if (parent == null) 
			throw new IllegalArgumentException();
		if (parent.isWritable() && isValidName(name) && parent.containsItemWithName(name))
			throw new IllegalArgumentException();
		if (parent.isWritable() && !isValidName(name) && parent.containsItemWithName(getDefaultName()))
			throw new IllegalArgumentException();
		if (!parent.isWritable()) 
			throw new ItemNotWritableException(parent);

		setName(name);
		setParentDirectory(parent);
		try {
			parent.addAsItem(this);
		} catch (ItemNotWritableException e) {
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
	protected boolean isTerminated = false;


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
	public abstract void terminate() throws IllegalStateException;

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
	protected static String getDefaultName() {
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
	 * Set the name of the item to the given name.
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
	 *          This item is not writable.
	 *          | !isWritable()
	 * @throws 	IllegalStateException
	 * 			This item is already terminated
	 * 			| isTerminated()
	 */
	public void changeName(String name) throws ItemNotWritableException, IllegalStateException {
		if (isTerminated()) throw new IllegalStateException("Item is terminated!");
		if (!isWritable()) throw new ItemNotWritableException(this);
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
	public boolean isOrderedAfter(@Raw Item other) {
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
	public boolean isOrderedBefore(@Raw Item other) {
		return (other != null) && isOrderedBefore(other.getName());
	}
	
	/**********************************************************
	 * creationTime
	 **********************************************************/

	/**
	 * Variable referencing the time of creation.
	 */
	private final Date creationTime = new Date();

	/**
	 * Return the time at which this item was created.
	 */
	@Raw @Basic @Immutable 
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Check whether the given date is a valid creation time.
	 *
	 * @param  	date
	 *         	The date to check.
	 * @return 	True if and only if the given date is effective and not
	 * 			in the future.
	 *         	| result == 
	 *         	| 	(date != null) &&
	 *         	| 	(date.getTime() <= System.currentTimeMillis())
	 */
	public static boolean isValidCreationTime(Date date) {
		return 	(date!=null) &&
				(date.getTime()<=System.currentTimeMillis());
	}




	/**********************************************************
	 * modificationTime
	 **********************************************************/

	/**
	 * Variable referencing the time of the last modification,
	 * possibly null.
	 */
	private Date modificationTime = null;

	/**
	 * Return the time at which this item was last modified, that is
	 * at which the name or content was last changed. If this item has
	 * not yet been modified after construction, null is returned.
	 */
	@Raw @Basic
	public Date getModificationTime() {
		return modificationTime;
	}

	/**
	 * Check whether this item can have the given date as modification time.
	 *
	 * @param	date
	 * 			The date to check.
	 * @return 	True if and only if the given date is either not effective
	 * 			or if the given date lies between the creation time and the
	 * 			current time.
	 *         | result == (date == null) ||
	 *         | ( (date.getTime() >= getCreationTime().getTime()) &&
	 *         |   (date.getTime() <= System.currentTimeMillis())     )
	 */
	@Raw
	public boolean canHaveAsModificationTime(Date date) {
		return (date == null) ||
				( (date.getTime() >= getCreationTime().getTime()) &&
						(date.getTime() <= System.currentTimeMillis()) );
	}

	/**
	 * Set the modification time of this item to the current time.
	 *
	 * @post   The new modification time is effective.
	 *         | new.getModificationTime() != null
	 * @post   The new modification time lies between the system
	 *         time at the beginning of this method execution and
	 *         the system time at the end of method execution.
	 *         | (new.getModificationTime().getTime() >=
	 *         |                    System.currentTimeMillis()) &&
	 *         | (new.getModificationTime().getTime() <=
	 *         |                    (new System).currentTimeMillis())
	 */
	@Model @Raw
	protected void setModificationTime() {
		modificationTime = new Date();
	}

	/**
	 * Return whether this item and the given other item have an
	 * overlapping use period.
	 *
	 * @param 	other
	 *        	The other item to compare with.
	 * @return 	False if the other item is not effective
	 * 			False if the prime object does not have a modification time
	 * 			False if the other item is effective, but does not have a modification time
	 * 			otherwise, true if and only if the open time intervals of this item and
	 * 			the other item overlap
	 *        	| if (other == null) then result == false else
	 *        	| if ((getModificationTime() == null)||
	 *        	|       other.getModificationTime() == null)
	 *        	|    then result == false
	 *        	|    else 
	 *        	| result ==
	 *        	| ! (getCreationTime().before(other.getCreationTime()) && 
	 *        	|	 getModificationTime().before(other.getCreationTime()) ) &&
	 *        	| ! (other.getCreationTime().before(getCreationTime()) && 
	 *        	|	 other.getModificationTime().before(getCreationTime()) )
	 */
	public boolean hasOverlappingUsePeriod(Item other) {
		if (other == null) return false;
		if(getModificationTime() == null || other.getModificationTime() == null) return false;
		return ! (getCreationTime().before(other.getCreationTime()) && 
				getModificationTime().before(other.getCreationTime()) ) &&
				! (other.getCreationTime().before(getCreationTime()) && 
						other.getModificationTime().before(getCreationTime()) );
	}

	/**********************************************************
	 * writable
	 **********************************************************/

	/**
	 * Variable registering whether or not this item is writable.
	 */
	protected boolean isWritable = true;

	/**
	 * Check whether this item is writable.
	 */
	@Raw @Basic
	public abstract boolean isWritable();
	
	/**********************************************************
	 * parent directory
	 **********************************************************/	

	/**
	 * Variable referencing the directory (if any) to which this 
	 * item belongs.
	 */
	private Directory parentDirectory = null;


	/**
	 * Return the root item to which this item directly or indirectly
	 * belongs. In case this item is a root item, the item itself is 
	 * the result.
	 * 
	 * @return If this item is a root item, this item is returned;
	 *         Otherwise the root to which the parent item of this 
	 *         item belongs is returned.
	 *         | if (isRoot())
	 *         | then result == this
	 *         | else result == getParentDirectory().getRoot()
	 */
	public Item getRoot() {
		if (isRoot()) {
			return this;
		} else {
			return getParentDirectory().getRoot();
		}
	}

	/**
	 * Move this item to a given directory.
	 * 
	 * @param   target
	 *          The target directory.
	 * @effect  If this item is not a root, this item is
	 *          removed from its parent directory.
	 *          | if (!isRoot())
	 *          | then getParentDirectory().removeAsItem(this) 
	 * @effect  This item is added to the target directory.
	 *          | target.addAsItem(this)
	 * @effect  The modification time is updated.
	 *          | setModificationTime()
	 * @post    The given directory is registered as the parent directory 
	 *          of this item.
	 *          | new.getParentDirectory() == target
	 * @throws  IllegalArgumentException
	 *          The given target directory is not effective, or the parent
	 *          directory of this item is the given target directory,
	 *          or the target directory cannot have this item
	 *          | (target == null) || 
	 *          | (target == getParentDirectory()) ||
	 *          | (!target.canHaveAsItem(this))
	 * @throws	ItemNotWritableException(this)
	 * 			This item is not writable
	 * 			| !isWritable()
	 * @throws	ItemNotWritableException(target)
	 * 			This target is effective, but not writable
	 * 			| (target != null) && !target.isWritable()
	 * @throws 	IllegalStateException
	 * 			This item is terminated
	 * 			| isTerminated()
	 */
	public void move(Directory target) 
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		if ( isTerminated()) 
			throw new IllegalStateException("Item is terminated!");
		if ( (target == null) || (getParentDirectory() == target) || !target.canHaveAsItem(this))
			throw new IllegalArgumentException();
		if (!isWritable())
			throw new ItemNotWritableException(this);
		if (!target.isWritable())
			throw new ItemNotWritableException(target);

		if (!isRoot()) {
			try{
				getParentDirectory().removeAsItem(this);
				//our disk item becomes raw now
			}catch(IllegalArgumentException e){
				//this cannot happen because of the class invariants
				assert false;
			}
		}
		setParentDirectory(target); 
		try{
			target.addAsItem(this); //this is a raw item because it's not yet registered in the new parent
									//so the formal argument of addAsItem should be annotated @Raw
		}catch(IllegalArgumentException e){
			//this should not happen, because it can have this item
			assert false;
		}catch(ItemNotWritableException e){
			//this should not happen, because we checked it
			assert false;
		}
		setModificationTime();
	}

	/**
	 * Check whether this item is a root item.
	 * 
	 * @return  True if this item has a non-effective parent directory;
	 *          false otherwise.
	 *        	| result == (getParentDirectory() == null)
	 */
	@Raw
	protected abstract boolean isRoot();

	/** 
	 * Check whether this item has a proper parent directory as
	 * its parent directory.
	 * 
	 * @return  true if this item can have its registered parent directory 
	 * 			as its parent directory and it is either a root, or 
	 * 			its registered parent directory has this item as a registered item.
	 *          | result == canHaveAsParentDirectory(getParentDirectory()) &&
	 *			|            (isRoot() || getParentDirectory().hasAsItem(this))
	 *	@note	This checker is split up in two parts, the consistency of the 
	 *			bidirectional relationship is added to the functionality of 
	 *			the other checker (canHaveAsParentDirectory())
	 */
	@Raw 
	public boolean hasProperParentDirectory() {
		return canHaveAsParentDirectory(getParentDirectory()) &&
				(isRoot() || getParentDirectory().hasAsItem(this));
	}
	
	/** 
	 * Check whether this item can have the given directory as
	 * its parent directory.
	 * 
	 * @param  	directory
	 *          The directory to check.
	 * @return  If this item is terminated, 
	 * 			true if the given directory is not effective, 
	 * 			false otherwise.
	 *          | if (this.isTerminated())
	 *          | then result == (directory == null)
	 * @return	If this item is not terminated,
	 * 				if the given directory is not effective,
	 * 				then true if this item is a root item or the parent of this item is writable, 
	 * 					 false otherwise
	 * 				else if the given directory is terminated, then false
	 * 					 if this disk item is the same as the given directory, then false
	 * 					 if this disk item is a direct or indirect parent of the given directory, then false
	 * 					 else true if the given directory is writable and it can have this item as an item
	 * 							and this item is a root or the parent directory of this item is writable,
	 * 						  false otherwise.
	 *			| if (!this.isTerminated())
	 *			| then if (directory == null)
	 *			|	   then result == (isRoot() || this.getParentDirectory().isWritable())
	 *			|	   else if (directory.isTerminated()) then result == false
	 *			|		 	if (directory == this) then result == false
	 *			|			if (this.isDirectOrIndirectParentOf(directory)) then result == false
	 *			|			else result == (directory.isWritable() && directory.canHaveAsItem(this) &&
	 *			|							(this.isRoot() || this.getParentDirectory().isWritable()) )
	 *	@note	This checker checks all conditions except the consistency of the bidirectional relationship
	 *			This checker can thus be used to check whether a disk item can accept a directory
	 *			as its new parent directory
	 */
	@Raw 
	public boolean canHaveAsParentDirectory(Directory directory) {
		if (this.isTerminated())
			return (directory == null);
		if (directory == null)
			return (this.isRoot() || this.getParentDirectory().isWritable());
		if (directory.isTerminated())
			return false;
		if (this.isDirectOrIndirectParentOf(directory))
			return false;
		else{ 
			Directory parentDirectory = this.getParentDirectory();
			if (parentDirectory == null)
				return (directory.isWritable) && directory.canHaveAsItem(this);
			else{
			return (directory.isWritable() && directory.canHaveAsItem(this) &&
				this.getParentDirectory().isWritable()
				);}
		}
	}

	/**
	 * Check whether this item is a direct or indirect parent of to the given item.
	 * 
	 * @param 	other
	 *        	The item to check.
	 * @return 	If the given item is non-effective, then false
	 * 			| if (item == null) then result == false
	 * @return	If the given item is effective, 
	 * 			then true if this item is the direct or indirect parent of the given item,
	 * 				 false otherwise
	 *      	| if (item != null)
	 *      	| then result == (this == item.getParentDirectory() ||
	 *      	|				  isDirectOrIndirectParentOf(item.getParentDirectory() )
	 */
	@Raw
	public boolean isDirectOrIndirectParentOf(@Raw Item item) {
		if(item == null) return false;
		else return (this == item.getParentDirectory() || isDirectOrIndirectParentOf(item.getParentDirectory()));
	}

	/**
	 * Set the parent directory of this item to the given directory.
	 *
	 * @param  directory
	 *         The new parent directory for this item.
	 * @post   The parent directory of this item is set to the given 
	 *         directory.
	 *         | new.getParentDirectory() == directory
	 * @throws IllegalArgumentException
	 *         This item cannot have the given directory as its
	 *         parent directory.
	 *         | ! canHaveAsParentDirectory(parentDirectory)
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 */
	@Raw @Model
	protected void setParentDirectory(Directory parentDirectory)
			throws IllegalArgumentException, IllegalStateException {
		if ( isTerminated()) 
			throw new IllegalStateException("Disk item is terminated!");
		if (!canHaveAsParentDirectory(parentDirectory)) {
			throw new IllegalArgumentException("Inappropriate item!");
		}
		this.parentDirectory = parentDirectory;
	}

	/**
	 * Return the parent directory (if any) to which this item
	 * applies.
	 */
	@Raw @Basic
	public Directory getParentDirectory() {
		return parentDirectory;
	}
	
	/***********************************
	 * extra method
	 ***********************************
	 
	 /**
	  * Return a string with the absolute path from this item.
	  * 
	  * @return	String with absolute path from this item.
	  * 			| for all parentdirectories from this {
	  * 			|		return = / + parentDirectory.getName() + / + this.getName()
	  * 			| }
	  */
	 	public String getAbsolutePath(){
	 		String path;
	 		if (this.isRoot()){
	 			path = "/" + this.getName(); 
	 		}
	 		else {
	 			if (this instanceof File){
	 				File file = (File)this;
	 				path = "/" + this.getName() + "." + file.getType().getExtension();
	 			}
	 			else {
	 				path = "/" + this.getName();
	 			}
	 			Directory dir = this.getParentDirectory();
	 			while (this.getRoot() != dir){
	 				path = "/" + dir.getName() + path;
	 				dir = dir.getParentDirectory();
	 			}
	 			path = "/" + this.getRoot().getName() + path;
	 		}
	 		return path;
	 	}
}

