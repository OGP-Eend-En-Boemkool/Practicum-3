package filesystem.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import filesystem.Directory;

public class NotAllWritableException extends RuntimeException {

	/**
	 * Variable referencing the directory that has at least one item in its content that is
	 * not writable.
	 */
	private final Directory dir;
	
	/**
	 * Initialize this new not all writable exception involving the
	 * given directory.
	 * 
	 * @param	dir
	 * 			The directory for the new not all writable exception.
	 * @post	The directory involved in the new not all writable exception
	 * 			is set to the given item.
	 * 			| new.getItem() == item
	 */
	@Raw
	public NotAllWritableException(Directory dir) {
		this.dir = dir;
	}
	
	/**
	 * Return the directory involved in this not all writable exception.
	 */
	@Raw @Basic
	public Directory getDir() {
		return dir;
	}
	
}
