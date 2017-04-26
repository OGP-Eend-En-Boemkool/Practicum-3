package filesystem;

import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;

/**
 * An abstract class of disk items.
 *
 * @invar	Each disk item must have a properly spelled name.
 * 			| isValidName(getName())
 * @invar   Each disk item must have a valid creation time.
 *          | isValidCreationTime(getCreationTime())
 * @invar   Each disk item must have a valid modification time.
 *          | canHaveAsModificationTime(getModificationTime())
 * @invar   Each disk item must have a valid parent directory.
 *          | hasProperParentDirectory()
 * 
 * @author 	Lotte en Linde    
 * @version	1.0
 * 
 */
public abstract class DiskItem extends Item{

	/**
	 * Initialize a new root disk item with given name and writability.
	 * 
	 * @param  	name
	 *         	The name of the new disk item.
	 * @param  	writable
	 *         	The writability of the new disk item.
	 * @effect	The writability is set to the given flag
	 * 			| setWritable(writable)
	 * @post 	The disk item is a root item
	 * 			| new.isRoot()
	 * @post    The new creation time of this disk item is initialized to some time during
	 *          constructor execution.
	 *          | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *          | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post    The new disk item has no time of last modification.
	 *          | new.getModificationTime() == null
	 */
	@Model
	protected DiskItem(String name, boolean writable) {
		super(name);
		setWritable(writable);
	}

	/**
	 * Initialize a new disk item with given parent directory, name and 
	 * writability.
	 *   
	 * @param  	parent
	 *         	The parent directory of the new disk item.
	 * @param  	name
	 *         	The name of the new disk item.  
	 * @param  	writable
	 *         	The writability of the new disk item.
	 *         
	 * @effect	The writability is set to the given flag
	 * 			| setWritable(writable)
	 * @effect 	This item is added to the items of the parent directory
	 *         	| parent.addAsItem(this)
	 * @post    The new creation time of this disk item is initialized to some time during
	 *          constructor execution.
	 *          | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *          | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post    The new disk item has no time of last modification.
	 *          | new.getModificationTime() == null
	 * @post    The new disk item is not terminated.
	 *          | !new.isTerminated()
	 * @throws 	IllegalArgumentException
	 *         	The given parent directory is not effective
	 *         	| parent == null
	 * @throws 	ItemNotWritableException(parent)
	 *         	The given parent directory is effective, but not writable.
	 *         	| parent != null && !parent.isWritable()
	 * @throws 	IllegalArgumentException
	 *         	The given valid name already exists in the effective and writable parent directory
	 *          | parent != null && parent.isWritable() && 
	 *         	|   isValidName(name) && parent.containsItemWithName(name)
	 * @throws 	IllegalArgumentException
	 *         	The given name is not valid and the default name already exists in 
	 *         	the effective parent directory
	 *          | parent != null && parent.isWritable() && 
	 *         	|   !isValidName(name) && parent.containsItemWithName(getDefaultName())
	 */
	@Model
	protected DiskItem(Directory parent, String name, boolean writable) 
			throws IllegalArgumentException, ItemNotWritableException {
		super(parent,name);
		setWritable(writable);
	}

	/**********************************************************
	 * name - total programming
	 **********************************************************/

	/**
	 * Check whether the name of this disk item can be changed into the
	 * given name.
	 * 
	 * @return  True if this disk item is not terminated, the given 
	 *          name is a valid name for a disk item, this disk item
	 *          is writable, the given name is different from the current name of this disk item
	 *          and either this item is a root item or the parent directory does not 
	 *          already contain an other item with the given name;
	 *          false otherwise.
	 *          | result == !isTerminated() && isWritable() && isValidName(name) && 
	 *          |			!getName().equals(name) && ( isRoot() || !getParentDirectory().exists(name) )
	 */
	public boolean canAcceptAsNewName(String name) {
		return !isTerminated() && isWritable() && isValidName(name) && !getName().equals(name) &&
				(isRoot() || !getParentDirectory().exists(name));
	}	

	/**********************************************************
	 * writable
	 **********************************************************/
	
	/**
	 * Set the writability of this item to the given writability.
	 *
	 * @param 	isWritable
	 *        	The new writability
	 * @post  	The given writability is registered as the new writability
	 *        	for this item.
	 *        	| new.isWritable() == isWritable
	 * @throws	ItemNotWritableException(this)
	 * 			A directory that is not writable, can't be changed to writable
	 * 			| (this.getClass() == Directory) && (!this.isWritable())
	 */
	@Raw 
	protected abstract void setWritable(boolean isWritable)
			throws ItemNotWritableException;
	
	
	/**********************************************************
	 * parent directory
	 **********************************************************/
	
	/**
	 * Turns this item in a root item if allowed.
	 * 
	 * @post    The item is a root item.
	 *          | new.isRoot()
	 * @effect  If this item is not a root, this item is
	 *          removed from its parent directory.
	 *          | if (!isRoot())
	 *          | then getParentDirectory().removeAsItem(this)
	 * @effect  If this item is not a root, its modification time changed
	 * 			| if (!isRoot())
	 *          | then setModificationTime()         
	 * 
	 * @throws	ItemNotWritableException(this)
	 * 			This item is not a root and it is not writable
	 * 			| !isRoot() && !isWritable()
	 * @throws	ItemNotWritable(getParentDirectory())	
	 * 			This item is not a root and its parent directory is not writable
	 * 			| !isRoot() && !getParentDirectory().isWritable()
	 * @throws 	IllegalStateException
	 * 			This item is terminated
	 * 			| isTerminated()
	 * @throws	ItemCannotBeRootException(this)
	 * 			This item is not a directory
	 * 			| this.getClass() != Directory
	 */ 
	protected abstract void makeRoot()
			throws ItemNotWritableException, ItemCannotBeRootException;
	
	/**
	 * Check whether this item is a root item.
	 * 
	 * @return  True if this item has a non-effective parent directory;
	 *          false otherwise.
	 *        	| result == (getParentDirectory() == null)
	 */
	@Raw
	protected abstract boolean isRoot();
	
	/**********************************************************
	 * delete/termination
	 **********************************************************/
	
	/**
	 * Terminate this disk item.
	 * 
	 * @post 	This disk item is terminated.
	 *       	| new.isTerminated()
	 * @effect 	If this disk item is not terminated and it is not a root, it is made a root
	 * 			| if (!isTerminated() && !isRoot())  
	 * 			| then makeRoot()
	 * @throws 	IllegalStateException
	 * 		   	This disk item is not yet terminated and it can not be terminated.
	 * 		   	| !isTerminated() && !canBeTerminated()
	 */
	public void terminate() throws IllegalStateException{
		if(!isTerminated()){
			if (!canBeTerminated()) {
				throw new IllegalStateException("This item cannot be terminated");
			}
			if(!isRoot()){
				try{
					makeRoot();
				}catch(ItemNotWritableException e){
					//should not happen since this item and its parent are writable
					assert false;
				}
			}
			this.isTerminated = true;
		}
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
}
