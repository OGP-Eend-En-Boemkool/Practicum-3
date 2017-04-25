package filesystem.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import filesystem.Item;

public class ItemCannotBeRootException extends RuntimeException {

	/**
	 * Variable referencing the item that can't be root.
	 */
	private final Item item;
	
	/**
	 * Initialize this new cannot be root exception involving the
	 * given item.
	 * 
	 * @param	item
	 * 			The item for the new cannot be root exception.
	 * @post	The item involved in the new cannot be root exception
	 * 			is set to the given item.
	 * 			| new.getItem() == item
	 */
	@Raw
	public ItemCannotBeRootException(Item item) {
		this.item = item;
	}
	
	/**
	 * Return the item involved in this cannot be root exception.
	 */
	@Raw @Basic
	public Item getItem() {
		return item;
	}
	
}