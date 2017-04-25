package filesystem.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import filesystem.Link;

public class UnvalidLinkException extends RuntimeException {

	/**
	 * Variable referencing the link that isn't valid
	 */
	private final Link link;
	
	/**
	 * Initialize this new unvalid link exception involving the
	 * given link.
	 * 
	 * @param	link
	 * 			The given link
	 * @post	The link involved in the new unvalid link exception
	 * 			is set to the given disk item.
	 * 			| new.getLink() == link
	 */
	@Raw
	public UnvalidLinkException(Link link) {
		this.link = link;
	}
	
	/**
	 * Return the link involved in this unvalid link exception.
	 */
	@Raw @Basic
	public Link getLink() {
		return link;
	}
	
}