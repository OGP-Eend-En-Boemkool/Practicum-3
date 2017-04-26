package filesystem;

import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;

/**
 * A class of links.
 */

public abstract class Link extends Item {
	
	/**********************************************************
	 * Constructors
	 **********************************************************/
	
	/**
	 * Initialize a new link with given parent directory, name and 
	 * referenced disk item.
	 * 
	 * @param  parent
	 *         The parent directory of the new link.
	 * @param  name
	 *         The name of the new link.
	 * @param  refDiskItem
	 *         The referenced disk item of the new link.
	 * @effect The new link is an item with the given
	 *         parent and name.
	 *         | super(parent,name)  
	 * @effect The       
	 * @post   The new creation time of this link is initialized to some time during
	 *         constructor execution.
	 *         | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *         | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post   The new link has no time of last modification.
	 *         | new.getModificationTime() == null
	 * @post    The new link is not terminated.
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
	 *         	|   isValidName(name) && parent.containsDiskItemWithName(name)
	 * @throws 	IllegalArgumentException
	 *         	The given name is not valid and the default name already exists in 
	 *         	the effective parent directory
	 *          | parent != null && parent.isWritable() && 
	 *         	|   !isValidName(name) && parent.containsDiskItemWithName(getDefaultName())
	 */
	public Link (String name, DiskItem refDiskItem, Directory parent)
			throws IllegalArgumentException, ItemNotWritableException{
		super(parent, name);
		setRefDiskItem(refDiskItem);		
	}
	
	/**********************************************************
	 * Referenced disk item
	 **********************************************************/
	
	/**
	 * Variable referencing the referenced disk item of this link.
	 */
	private DiskItem refDiskItem = null;
	
	/**
	 * Return the referenced disk item of this link.
	 */
	@Raw @Basic 
	public DiskItem getRefDiskItem() throws UnvalidLinkException{
		if (refDiskItem.isTerminated()){
			throw new UnvalidLinkException(this);
		}
		return refDiskItem;
	}
	
	/**
	 * Initialize the referenced Disk Item of the new link
	 * 
	 * @param 	refDiskItem
	 * 			The referenced disk item
	 * @post	If the given referenced disk item isn't terminated, 
	 * 			the refDiskItem is set to the given disk item
	 * 			|if(!isTerminated())
	 * 			|	then new.getRefDiskItem().equals(refDiskItem)
	 * @throws 	IllegalStateException
	 * 			The given disk item already has been terminated
	 * 			| isTerminated()
	 */
	private void setRefDiskItem(DiskItem refDiskItem) throws IllegalStateException{
		if ( isTerminated()) 
			throw new IllegalStateException("Disk item is terminated!");
		this.refDiskItem = refDiskItem;
	}
	
	/**********************************************************
	 * name - total programming
	 **********************************************************/

	/**
	 * Check whether the name of this link can be changed into the
	 * given name.
	 * 
	 * @return  True if this link is not terminated, the given 
	 *          name is a valid name for the link, 
	 *          the given name is different from the current name of this link
	 *          and the parent directory does not 
	 *          already contain an other item with the given name;
	 *          false otherwise.
	 *          | result == !isTerminated() && isValidName(name) && 
	 *          |			!getName().equals(name) && !getParentDirectory().exists(name) )
	 */
	public boolean canAcceptAsNewName(String name) {
		return !isTerminated() && isValidName(name) && !getName().equals(name) &&
			 !getParentDirectory().exists(name);
	}
	
	/**********************************************************
	 * delete/termination
	 **********************************************************/
	
	/**
	 * Terminate this link.
	 * 
	 * @post 	This link is terminated.
	 *       	| new.isTerminated()
	 * @throws 	IllegalStateException
	 * 		   	This link is not yet terminated and it can not be terminated.
	 * 		   	| !isTerminated() && !canBeTerminated()
	 */
	public void terminate() throws IllegalStateException{
		if(!isTerminated()){
			if (!canBeTerminated()) {
				throw new IllegalStateException("This item cannot be terminated");
			}
			this.isTerminated = true;
		}
	}
	
	/**
	 * Check whether this item can be terminated.
	 * 
	 * @return	True if the item is not yet terminated, is writable and it is either a root or
	 * 			its parent directory is writable
	 * 			| if (isTerminated() || !getParentDirectory().isWritable()))
	 */
	public boolean canBeTerminated(){
		return !isTerminated() && getParentDirectory().isWritable();
	}
	
	/**********************************************************
	 * parent directory
	 **********************************************************/
	
	/**
	 * Check whether this item is a root item.
	 * 
	 * @return  false, a link can never be a root item
	 */
	@Raw
	public boolean isRoot() {
		return false;
	}
	
	/**********************************************************
	 * writable
	 **********************************************************/
	
	/**
	 * Check whether this item is writable.
	 */
	@Raw @Basic
	public boolean isWritable(){
		return true;
	
	}
}
