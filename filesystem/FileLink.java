package filesystem;

import filesystem.exception.*;

public class FileLink extends Link{
	
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
	 * @param  refFile
	 *         The referenced file of the new link.
	 * @effect The new link is an item with the given
	 *         parent and name.
	 *         | super(name,refFile,parent)        
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
	public FileLink (String name, File refFile, Directory parent)
			throws IllegalArgumentException, ItemNotWritableException{
		super(name, refFile ,parent);
	}
	
	/********************************
	 * name
	 ********************************
	
	/**
	 * Check whether the given name is a legal name for the link.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	True if the given string is effective, not
	 * 			empty and consisting only of letters, digits, dots,
	 * 			hyphens and underscores; false otherwise.
	 * 			| result ==
	 * 			|	(name != null) && name.matches("[a-zA-Z_0-9.-]+")
	 */
	public boolean isValidName(String name) {
		return (name != null && name.matches("[a-zA-Z_0-9.-]+"));
	}
}