package filesystem;

import java.util.*;

import be.kuleuven.cs.som.annotate.*;

import filesystem.exception.*;


/**
 * A class of directories.
 * @invar 	Each directory must have proper items registered in it.
 *        	| hasProperItems()
 * 
 * @author 	Tommy Messelis
 * @version 2.2 - 2016
 */
public class Directory extends DiskItem {

	/**********************************************************
	 * Constructors
	 **********************************************************/

	/**
	 * Initialize a new root directory with given name and writability.
	 * 
	 * @param  name
	 *         The name of the new directory.
	 * @param  writable
	 *         The writability of the new directory.
	 * @effect The new directory is a disk item with the given
	 *         name and writability.
	 *         | super(name,writable)
	 * @post   The new directory has no items.
	 *         | new.getNbItems() == 0
	 * 
	 */
	public Directory(String name, boolean writable) {
		super(name,writable);
	}

	/**
	 * Initialize a new writable root directory with given name.
	 * 
	 * @param  name
	 *         The name of the new directory.
	 * @effect The new root directory is initialized with the given name
	 *         and is writable.
	 *         | this(name,true)
	 */
	public Directory(String name) {
		this(name,true); 
	}


	/**
	 * Initialize a new directory with given parent directory, name and 
	 * writability.
	 * 
	 * @param  parent
	 *         The parent directory of the new directory.
	 * @param  name
	 *         The name of the new directory.
	 * @param  writable
	 *         The writability of the new directory.
	 * @effect The new directory is a disk item with the given
	 *         parent, name and writability.
	 *         | super(parent,name,writable)        
	 * @post   The new directory has no items.
	 *         | new.getNbItems() == 0
	 */
	public Directory(Directory parent, String name, boolean writable) 
			throws IllegalArgumentException, ItemNotWritableException {
		super(parent,name,writable);    
	}

	/**
	 * Initialize a new writable directory with given parent directory
	 * and name.
	 * 
	 * @param  parent
	 *         The parent directory of the new directory.
	 * @param  name
	 *         The name of the new directory.
	 * @effect The new directory is a disk item with the given
	 *         parent, name and writability.
	 *         | this(parent,name,true)       
	 */
	public Directory(Directory parent, String name) 
			throws IllegalArgumentException, ItemNotWritableException {
		this(parent,name,true);    
	}    

	
	
	
	/**********************************************************
	 * delete/termination
	 **********************************************************/

	/**
	 * Check whether this directory can be terminated.
	 * 
	 * @return	True if the directory is not yet terminated, is writable, contains 0 items
	 * 			and it is either a root or its parent directory is writable
	 * 			| result == getNbItems() == 0 && !isTerminated() && isWritable() 
	 * 			|            && (isRoot() || getParentDirectory().isWritable())
	 * @note	We have added a condition to the open specs of the superclass. Now the specification
	 * 			is in closed form.
	 */
	@Override
	public boolean canBeTerminated() {
		return getNbItems() == 0 && super.canBeTerminated();			
	}

	
	
	
	/**********************************************************
	 * Contents
	 **********************************************************/


	/**
	 * Variable referencing a list collecting all items contained by this				
	 * directory. The class DiskItem is responsible for controlling the 
	 * bidirectional relationship. Files and directories can only be added or deleted
	 * through the constructors/destructors of File and Directory and through
	 * the move and makeRoot methods, hence the protected methods for adding
	 * and removing items from the directory
	 * 
	 * @invar items references an effective list. 
	 *        | items != null
	 * @invar Each element in the list references an effective item. 
	 *        | for each item in items:
	 *        |   item != null
	 * @invar Each element in the list references a non-terminated item.
	 *        | for each item in items:
	 *        | !item.isTerminated()
	 * @invar Each element in the list (except the first element)
	 *        references an item that has a name which (ignoring case)
	 *        comes after the name of the immediately preceding element,
	 *        in lexicographic order. 
	 *        | for each I in 1..items.size() - 1:
	 *        |   items.get(I).isOrderedAfter(items.get(I-1))
	 * @invar Each element in the list references an item that references
	 *        back to this directory.
	 *        | for each item in items:
	 *        |   item.getParentDirectory() == this
	 */	
	private final List<Item> items = new ArrayList<Item>();  

	/**
	 * Return the number of items of this directory.
	 */
	@Basic @Raw 
	public int getNbItems() {
		return items.size();
	}

	/**
	 * Return the item registered at the given position in this directory.
	 * 
	 * @param 	index
	 *        	The index of the item to be returned.
	 * @throws 	IllegalArgumentException
	 *         	The given index is not strictly positive or exceeds the number
	 *         	of items registered in this directory. 
	 *         	| (index < 1) || (index > getNbItems())
	 */
	@Basic @Raw
	public Item getItemAt(int index) throws IndexOutOfBoundsException {
		try{
			return items.get(index - 1);
		} catch (IndexOutOfBoundsException e) {
			//The exception e contains a message indicating that 'index-1' is out of bounds
			//Here, we throw a new Exception with the right information
			throw new IndexOutOfBoundsException("Index out of bounds: "+index);
		}
	}

	/**
	 * Check whether this directory can have the given item as one of its items.
	 *
	 * @param   item
	 *          The item to be checked.
	 * @return 	If the given item is not effective or if it is terminated or if this
	 * 			directory is terminated, 
	 * 			then false,
	 * 			else if the given item is the same as this directory then false
	 * 				 else if the given item is a direct or indirect parent of this directory then false
	 * 					  else if the given item is an item of this directory
	 * 						   then true if and only if the given item has a unique name in this directory
	 * 						   else true if and only if the name of the given item does not yet exist
	 * 								in this directory and the given item is a root item or the parent
	 * 								directory of the given item is writable
	 * 			| if (item == null || item.isTerminated() || this.isTerminated()) 
	 * 			| then result == false
	 * 			| else if (item == this) then result == false
	 * 			|	   else if (item.isDirectOrIndirectParentOf(this)) then false
	 * 			|			else if (this.hasAsItem(item))
	 * 			|				 then result == for one I in 1..getNbItems:
	 *          |      								item.getName().equalsIgnoreCase(getItemAt(I).getName())
	 * 			|				 else result == (!this.containsDiskItemWithName(item.getName()) &&
	 * 			|									(item.isRoot() || item.getParentDirectory().isWritable())) 	
	 * 
	 * @note	This checker does not verify the consistency of the bidirectional relationship.
	 */
	@Raw
	public boolean canHaveAsItem(@Raw Item item) {
		if (item == null || item.isTerminated() || this.isTerminated()) return false;
		if (item.isDirectOrIndirectParentOf(this)) return false;
		if (this.hasAsItem(item)) {
			int count = 0;
			for (int position=1;position<=getNbItems();position++){
				 if (item.getName().equalsIgnoreCase(getItemAt(position).getName())) count++;
			}
			return count == 1;
		}else{
			return (!this.containsItemWithName(item.getName()) && (item.isRoot() || item.getParentDirectory().isWritable())); 
		}
	}

	/**
	 * Check whether this directory can have the given item at the given index.
	 *
	 * @param   item
	 *          The item to be checked.
	 * @param   index
	 *          The index to be checked.
	 * @return	False if this directory cannot have the given item at any index
	 * 			otherwise, false if the given index is not positive or exceeds the 
	 * 			number of items with more than one
	 * 				otherwise, if the item is in this directory 
	 * 						   then true if and only if it is ordered after its predecessor 
	 * 								and before it successor (if those exist)
	 * 						   else true if and only if inserting the item at the given index
	 * 								would not result in an unordered sequence
	 * 			| if (!canHaveAsItem(item)) then result == false
	 * 			| else if (index < 1 || index > getNbItems() +1) then result == false
	 * 			|	   else if (hasAsItem(item))
	 * 			|			then result == (index == 1 || getItemAt(index-1).isOrderedBefore(item))
	 * 			|							&& (index == getNbItems() || getItemAt(index+1).isOrderedAfter(item))
	 * 			|			else result == (index == 1 || getItemAt(index-1).isOrderedBefore(item))
	 * 			|							&& (index == getNbItems() + 1 || getItemAt(index).isOrderedAfter(item))
	 * @note	This checker checks all conditions, except the consistency of the bidirectional relationship.
	 * 			It can be used to verify existing items in this directory, as well as to verify whether
	 * 			a new item can be added at a certain position.
	 */
	@Raw
	public boolean canHaveAsItemAt(@Raw Item item, int index){ 
		if (!canHaveAsItem(item))
			return false;
		if ((index < 1) || (index > getNbItems()+1))
			return false;
		if(hasAsItem(item)){
			return (index == 1 ||  getItemAt(index-1).isOrderedBefore(item))
					&& (index == getNbItems() || getItemAt(index+1).isOrderedAfter(item));  
		}else{
			return (index == 1 ||  getItemAt(index-1).isOrderedBefore(item))
					&& (index == getNbItems() + 1 || getItemAt(index).isOrderedAfter(item));  

		}
	}

	/**
	 * Check whether this directory has valid items.
	 *
	 * @return  True if and only if this directory can have all its items 
	 * 			at their respective indices
	 *          | result ==
	 *          |   for each I in 1..getNbItems() :
	 *          |     canHaveAsItemAt(getItemAt(I),I) && getItemAt(I).getParentDirectory() == this
	 * @note	This checker ensures the consistency of the bidirectional relationship
	 * 			and calls another checker to check all other requirements (except this consistency)
	 */
	@Raw 
	public boolean hasProperItems() {
		for (int i=1; i <= getNbItems(); i++) {
			if (!canHaveAsItemAt(getItemAt(i), i) && getItemAt(i).getParentDirectory() == this){
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the given item is registered in this directory.
	 * 
	 * @param 	item
	 *        	The item to be checked.
	 * @return 	True if an item equal to the given item is registered at some
	 *         	position in this directory;
	 *         	false otherwise.
	 *         	| result == 
	 *         	|    for some I in 1..getNbItems() :
	 *         	| 	      (getItemAt(I) == item)
	 */
	@Raw
	public boolean hasAsItem(@Raw Item item) { 
		for (int i=1; i<=getNbItems(); i++) {
			if (getItemAt(i) == item)
				return true;
		}
		return false;
	}

	/**
	 * Add the given item to the items registered in this directory.
	 *
	 * @param   item
	 *          The item to be added.
	 * @post    The number of items registered in this directory is
	 *          incremented with 1.
	 *          | new.getNbItems() == getNbItems() + 1   
	 * @post    The given item is added to the items registered
	 *          in this directory.
	 *          | new.hasAsItem(item)
	 * @post    All items registered in this directory, that are 
	 *          ordered after the given item are shifted one position
	 *          to the right.
	 *          | for each I in 1..getNbItems():
	 *          |   if (getItemAt(I).isOrderedAfter(item))
	 *          |     then new.getItemAt(I+1) == getItemAt(I)
	 * @effect 	The new modification time of this directory is updated.
	 *         	| setModificationTime()
	 * @throws  IllegalArgumentException
	 *          The item already exists in this directory or it can not have the given item as item.
	 *          | hasAsItem(item) || !canHaveAsItem(item)
	 */ 
	@Model
	protected void addAsItem(@Raw Item item) throws IllegalArgumentException{
		if(hasAsItem(item) || !canHaveAsItem(item))
			throw new IllegalArgumentException();
		//now find the right index to add this item
		int index = 1;
		while(index <= getNbItems() && getItemAt(index).isOrderedBefore(item)){
			index++;
		}
		addItemAt(item, index);
		setModificationTime();
	}
	
	/**
	 * Insert the given item at the given index.
	 * 
	 * @param   item
	 *          The item to be added.
	 * @param   index
	 *          The index where the given item must be inserted.
	 * @post    The number of items registered in this directory is
	 *          incremented with 1.
	 *          | new.getNbItems() == getNbItems() + 1   
	 * @post    The given item is inserted at the given index.
	 *          | new.getItemAt(index) == item
	 * @post    All items after the given index are shifted
	 *          one position to the right.
	 *          | for each I in index..getNbItems():
	 *          |   new.getItemAt(I+1) == getItemAt(I)
	 * @throws  IllegalArgumentException
	 *          This directory already contains the given item or cannot have it at the given index.
	 *          | hasAsItem(item) || !canHaveAsItemAt(item,index)
	 */
	private void addItemAt(@Raw Item item, int index) throws IllegalArgumentException {
		if (hasAsItem(item) || !canHaveAsItemAt(item,index))
			throw new IllegalArgumentException("cannot add the given item to this directory");
		items.add(index-1,item);
	}
	
	/**
	 * Remove the given item from this directory.
	 *
	 * @param 	item
	 *        	The item to remove
	 * @effect 	If the item was in this directory, it is removed at the position it was registered
	 *         	| if (hasAsItem(item) then removeItemAt(getIndexOf(item))
	 * @effect 	The new modification time of this directory is updated.
	 *         	| setModificationTime()
	 * @throws 	IllegalArgumentException
	 *         	The given item is not in the directory
	 *         	| ! hasAsItem(item)
	 */
	@Raw @Model
	protected void removeAsItem(@Raw Item item) throws IllegalArgumentException{
		
		if(!hasAsItem(item))
			throw new IllegalArgumentException("This item is not present in this directory");
		try{
			removeItemAt(getIndexOf(item));
		}catch(IndexOutOfBoundsException e){
			//this will not happen
			assert(false);
		}
		setModificationTime();
	}

	/**
	 * Remove the given item at the given index from this directory.
	 *
	 * @param 	index
	 *        	The index from the item to remove.
	 * @post	This directory no longer has the item at the given index as an item
	 * 			| !new.hasAsItem(getItemAt(index))
	 * @post  	All elements to the right of the removed item
	 *        	are shifted left by 1 position.
	 *        	| for each I in index+1..getNbItems():
	 *        	|   new.getItemAt(I-1) == getItemAt(I)
	 * @post  	The number of items has decreased by one
	 *        	| new.getNbItems() == getNbItems() - 1
	 * @throws	IndexOutOfBoundsException
	 *        	The given position is not positive or exceeds the number
	 *        	of items registered in this directory. 
	 *        	| (index < 1) || (index > getNbItems())
	 */
	@Raw @Model 
	private void removeItemAt(int index) throws IndexOutOfBoundsException{
		if(index < 1 || index > getNbItems())
			throw new IndexOutOfBoundsException("Index out of bounds: "+index);
		items.remove(index-1);
	}
	
	/**
	 * Check whether this directory is a direct or indirect subdirectory
	 * of the given other directory
	 * 
	 * @param 	other
	 *        	The directory to check with.
	 * @return 	True if and only if the other directory is a direct or indirect 
	 * 			parent of this directory
	 * 			| result == other.isDirectOrIndirectParentOf(this)
	 * @throws 	IllegalArgumentException
	 *         	The given other directory is not effective.
	 *         	| other == null
	 */
	public boolean isDirectOrIndirectSubdirectoryOf(Directory other) 
			throws IllegalArgumentException {
		if(other == null) throw new IllegalArgumentException("Other directory is null.");
		return other.isDirectOrIndirectParentOf(this);        
	}
	
	
	/**
	 * Check whether this directory contains an item with the given name
	 * @param	name
	 * 			The name to check.
	 * @return	true if and only if this name exists in this directory
	 * 			| result == exists(name)
	 */
	@Raw
	public boolean containsItemWithName(String name){
		return exists(name);
	}

	/**
	 * Check whether an item with the given name is registered 
	 * in this directory (ignoring case).
	 *
	 * @param   name
	 *          The name to be checked.
	 * @return  True if an item with the given name (ignoring case)
	 *          is registered at some position in this directory; 
	 *          false otherwise.
	 *        | result ==
	 *        |   (for some I in 1..getNbItems():
	 *        |      getItemAt(I).getName().equalsIgnoreCase(name))
	 */
	@Raw
	public boolean exists(String name) {
		for (int i=1;i<=getNbItems();i++) {
			if (getItemAt(i).getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the item in this directory with the given name.
	 * 
	 * @param 	name
	 *        	The name of the item to be looked up.
	 * @return 	the item in this directory with the given name, if such an item exists
	 * 			null otherwise
	 * 			| if (exists(name)) 
	 * 			| then (hasAsItem(result) && 
	 *         	| 		result.getName().equalsIgnoreCase(name))
	 *         	| else result == null
	 * @note	This operation should complete in O(log(n)) time
	 */
	public Item getItem(String name) {
		//do a binary search!
		int low = 1;
		int high = getNbItems();
		int middle = (low+high)/2;
		while (low <= high) {
			Item middleItem = getItemAt(middle);
			if(middleItem.getName().equalsIgnoreCase(name)) 
				return middleItem;
			if (middleItem.isOrderedAfter(name)) {
				high = middle;
			} else {
				low = middle+1;
			}
		}
		//if not found, return null
		return null;
	}
	
	/**
	 * Return the position at which the given item is registered.
	 *
	 * @param   item
	 *          The item to be searched.
	 * @return  The given item is registered in this directory at the
	 *          resulting position.
	 *          | getItemAt(result)==item
	 * @throws  IllegalArgumentException
	 *          The given item is not in the directory
	 *          | ! hasAsItem(item)
	 */
	public int getIndexOf(@Raw Item item) throws IllegalArgumentException {
		if(!hasAsItem(item))
			throw new IllegalArgumentException("This item is not present in this directory");
		else{
			for(int i=1; i<=getNbItems(); i++){
				if(getItemAt(i) == item) return i;
			}
			//this will never happen!
			assert false;
			return -1;
		}
	}
	
	/**
	 * Restore the order of the items in this directory, 
	 * given only one of them may be in the wrong position due to a name change
	 * 
	 * @param	index
	 * 			the index of the item with a new name
	 * @post	this directory has proper items	
	 * 			| hasProperItems()
	 * @post	The index of a certain number of items has changed
	 * 			| for each I in 1..getNbItems() :
	 *        	|   for some J in 1..getNbItems() :
	 *        	|      new.getItemAt(J) == getItemAt(I)
	 * @throws	IndexOutOfBoundsException
	 * 			The given index is not valid
	 * 			| (index < 1) || (index > getNbItems())
	 */
	@Raw @Model
	protected void restoreOrderAfterNameChangeAt(int index) {
		if(index < 1 || index > getNbItems())
			throw new IndexOutOfBoundsException("The index is not valid");
		try{
			Item item = getItemAt(index);
			removeItemAt(index);
			addAsItem(item);
		}catch(IllegalArgumentException e){
			//this should not happen
			assert false;
		}catch(ItemNotWritableException e){
			//this should not happen
			assert false;
		}catch(IndexOutOfBoundsException e){
			//this should not happen
			assert false;
		}
	}
	
	/**********************************
	 * root
	 **********************************
	
	/**
	 * Turns this disk item in a root disk item.
	 * 
	 * @post    The disk item is a root disk item.
	 *          | new.isRoot()
	 * @effect  If this disk item is not a root, this disk item is
	 *          removed from its parent directory.
	 *          | if (!isRoot())
	 *          | then getParentDirectory().removeAsItem(this)
	 * @effect  If this disk item is not a root, its modification time changed
	 * 			| if (!isRoot())
	 *          | then setModificationTime()         
	 * 
	 * @throws	DiskItemNotWritableException(this)
	 * 			This disk item is not a root and it is not writable
	 * 			| !isRoot() && !isWritable()
	 * @throws	DiskItemNotWritable(getParentDirectory())	
	 * 			This disk item is not a root and its parent directory is not writable
	 * 			| !isRoot() && !getParentDirectory().isWritable()
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated
	 * 			| isTerminated()
	 * @throws	DiskItemCannotBeRootException(this)
	 * 			This disk item is not a directory (will never occur in this case)
	 * 			| this.getClass() != Directory
	 */ 
	public void makeRoot()
			throws ItemNotWritableException, ItemCannotBeRootException {
		if ( isTerminated()) 
			throw new IllegalStateException("Diskitem is terminated!");
		if (!isRoot()) {
			if (!isWritable()) 
				throw new ItemNotWritableException(this);
			if(!getParentDirectory().isWritable())
				throw new ItemNotWritableException(getParentDirectory());

			Directory dir = getParentDirectory();
			setParentDirectory(null); 
			//this item is now in a raw state
			dir.removeAsItem(this);
			setModificationTime();
		}
	}

	/**
	 * Check whether this item is a root item.
	 * 
	 * @return  True if this item has a non-effective parent directory;
	 *          false otherwise.
	 *        	| result == (getParentDirectory() == null)
	 */
	@Raw
	public boolean isRoot() {
		return getParentDirectory() == null;
	}
	
	/*********************************
	 * writable
	 *********************************
	
	/**
	 * Set the writability of this directory to the given writability if allowed.
	 *
	 * @param 	isWritable
	 *        	The new writability
	 * @post  	The given writability is registered as the new writability
	 *        	for this disk item.
	 *        	| new.isWritable() == isWritable
	 * @throws	DiskItemNotWritableException(this)
	 * 			The directory is not writable, that can't be changed.
	 * 			| !this.isWritable()
	 */
	@Raw 
	public void setWritable(boolean isWritable) throws ItemNotWritableException {
		if (this.isWritable) {
			this.isWritable = isWritable;
		}
		else {
			throw new ItemNotWritableException(this);
		}
		
	}
	
	/*********************************
	 * name
	 *********************************
	
	/**
	 * Check whether the given name is a legal name for a directory.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	True if the given string is effective, not
	 * 			empty and consisting only of letters, digits,
	 * 			hyphens and underscores; false otherwise.
	 * 			| result ==
	 * 			|	(name != null) && name.matches("[a-zA-Z_0-9-]+")
	 */
	public boolean isValidName(String name) {
		return (name != null && name.matches("[a-zA-Z_0-9-]+"));
	}
	
	/**********************************
	 * iterator
	 **********************************
	
	/**
	 * return this directory (needed for the iterator)
	 * 
	 * @return	this directory
	 * 			| this
	 */
	public Directory returnThis(){
		return this;
	}
	
	/**
	 * a directory-iterator to iterate over the elements in a directory
	 * 
	 */
	public DirectoryIterator iterator() {
		return new DirectoryIterator(){

			/**
			 * variable referencing to the current item
			 * current is set to the directory that called the iterator when there are no
			 * more items left to iterate
			 */
			private Item current = getItemAt(1);
			
			/**
			 * Return the number of remaining disk items to be
			 * returned by this directory-iterator, including
			 * the current item.
			 * 
			 * @return	The resulting number cannot be negative.
			 * 			| result >= 0 
			 */
			@Override
			public int getNbRemainingItems() {
				if (current == returnThis()) {
					return 0;
				}
				else {
					return getNbItems() - (getIndexOf(current) - 1);
				}
				
			}
			
			/**
			 * Return the current disk item of this directory-iterator.
			 * 
			 * @return	The current item
			 * @throws	IndexOutOfBoundsException
			 * 			This directory-iterator has no current item.
			 * 			| getNbRemainingItems() == 0
			 */
			@Override
			public Item getCurrentItem() throws IndexOutOfBoundsException {
				if (getNbRemainingItems() != 0){
					return current;
				}
				else {
					throw new IndexOutOfBoundsException();
				}
			}

			/**
			 * Advance the current item of this directory-iterator to the
			 * next disk item. 
			 * 
			 * @pre		This directory-iterator must still have some remaining items.
			 * 			| getNbRemainingItems() > 0
			 * @post	The number of remaining disk items is decremented
			 * 			by 1.
			 * 			| new.getNbRemainingItems() == getNbRemainingItems() - 1
			 * @post	If the current item before calling this method is the last item,
			 * 			then this method sets the current item to the directory that
			 * 			called this method
			 * 			| if (getNbRemainingItems() == 1) {
			 * 			| 		current = returnThis()
			 * 			| }
			 */
			@Override
			public void advance() {
				if (getNbRemainingItems() > 1){
					current = getItemAt(getIndexOf(current)+1);
				}
				else {
					current = returnThis();
				}
			}

			/**
			 * Reset this directory-iterator to its first item.
			 */
			@Override
			public void reset() {
				current = getItemAt(1);
			}
			
		};
	}
	
	/********************************
	 * extra methods
	 ********************************
	
	/**
	 * return the total disk usage from this directory, with every direct and indirect item
	 * 
	 * @return	the total disk usage
	 * @throws 	IndexOutOfBoundsException
	 * 			dirIt has no current item.
	 * 			| getNbRemainingItems() == 0
	 */
	public long getTotalDiskUsage() throws IndexOutOfBoundsException {
		DirectoryIterator dirIt = iterator();
		long total = 0;
		while (dirIt.getNbRemainingItems() != 0) {
			if (dirIt.getCurrentItem() instanceof Directory){
				Directory dir = (Directory)dirIt.getCurrentItem();
				total += dir.getTotalDiskUsage();
			}
			else if (dirIt.getCurrentItem() instanceof File){
				File file = (File)dirIt.getCurrentItem();
				total += file.getSize();
			}
			dirIt.advance();
		}
		return total;
	}
	
	/**
	 * Terminates all items (direct and indirect) in this directory if they are all
	 * writable.
	 * 
	 * @post	All items in this directory are terminated
	 * 			| for all items in this {
	 * 			|		item.isTerminated()
	 * 			| }
	 * @throws 	NotAllWritableException
	 * 			Not all items in this directory are writable
	 * 			| !this.allWritable()
	 * @throws 	IllegalStateException
	 * 			This item is not yet terminated and it can not be terminated.
	 * 		   	| !isTerminated() && !canBeTerminated()
	 */
	public void deleteRecursive() throws NotAllWritableException, IllegalStateException {
		if (this.allWritable() && this.canBeTerminated()){
			DirectoryIterator dirIt = iterator();
			while (dirIt.getNbRemainingItems() != 0){
				if (dirIt.getCurrentItem() instanceof Directory){
					Directory dir = (Directory)dirIt.getCurrentItem();
					dir.deleteRecursive();
					dir.terminate();
				}
				else {
					dirIt.getCurrentItem().terminate();
				}
				dirIt.advance();
			}
		}
		else {
			throw new NotAllWritableException(this);
		}
	}
	
	/**
	 * Return if all direct and indirect items are writable or not.
	 * 
	 * @return	true if and only if all direct and indirect items are writable,
	 * 			false otherwise.
	 * 			| result ==
	 * 			| (for all items in this {
	 * 			|		item.isWritable()
	 * 			| })
	 */
	private boolean allWritable(){
		DirectoryIterator dirIt = iterator();
		if (!this.isWritable()){
			return false;
		}
		boolean allWritable = true;
		while (dirIt.getNbRemainingItems() != 0){
			if (dirIt.getCurrentItem() instanceof Directory){
				Directory dir = (Directory)dirIt.getCurrentItem();
				if (!dir.isWritable()){
					allWritable = false;
				}
				else if (!dir.allWritable()){
					allWritable = false;
				}
			}
			else if (dirIt.getCurrentItem() instanceof File){
				File file = (File)dirIt.getCurrentItem();
				if (!file.isWritable()){
					allWritable = false;
				}
			}
			dirIt.advance();
		}
		return allWritable;
	}
	
}
