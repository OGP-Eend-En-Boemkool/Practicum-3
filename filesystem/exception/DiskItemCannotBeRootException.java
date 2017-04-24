package filesystem.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import filesystem.DiskItem;

public class DiskItemCannotBeRootException extends RuntimeException {

	/**
	 * Variable referencing the disk item that can't be root.
	 */
	private final DiskItem item;
	
	/**
	 * Initialize this new cannot be root exception involving the
	 * given disk item.
	 * 
	 * @param	item
	 * 			The disk item for the new not writable exception.
	 * @post	The disk item involved in the new cannot be root exception
	 * 			is set to the given disk item.
	 * 			| new.getItem() == item
	 */
	@Raw
	public DiskItemCannotBeRootException(DiskItem item) {
		this.item = item;
	}
	
	/**
	 * Return the disk item involved in this cannot be root exception.
	 */
	@Raw @Basic
	public DiskItem getItem() {
		return item;
	}
	
}
