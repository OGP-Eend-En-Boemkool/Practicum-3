package filesystem;
import static org.junit.Assert.*;
import java.util.Date;

import org.junit.*;

import filesystem.exception.*;

/**
 * A JUnit test class for testing the public methods of the Directory Class.
 * 
 * @author Linde en Lotte
 *
 */
public class DirectoryTest {
	
	Date timeBeforeConstruction;
	Date timeAfterConstruction;
	Date timeBeforeConstructionNotWritable;
	Date timeAfterConstructionNotWritable;

	Directory dirNameWritable;
	Directory dirName;
	Directory dirDirectoryNameWritable;
	Directory dirDirectoryName;
	Directory dirNotWritable;
	
	@Before
	public void setUpFixture(){
		timeBeforeConstruction = new Date();
		dirNameWritable = new Directory("map1", true);
		dirName = new Directory("map2");
		dirDirectoryNameWritable = new Directory(dirName, "map3", true);
		dirDirectoryName = new Directory(dirName, "map4", true);
		timeAfterConstruction = new Date();
		
		timeBeforeConstructionNotWritable = new Date();
		dirNotWritable = new Directory("map5", false);
		timeAfterConstructionNotWritable = new Date();
	}
	
	@Test
	public void testDirDirectoryNameWritable_legalCase(){
		assertEquals("map3", this.dirDirectoryNameWritable.getName());
		assertTrue(this.dirDirectoryNameWritable.canBeTerminated());
		assertFalse(this.dirDirectoryNameWritable.isRoot());
		assertTrue(this.dirDirectoryNameWritable.isWritable);
		assertNull(this.dirDirectoryNameWritable.getModificationTime());
		assertFalse(timeBeforeConstruction.after(this.dirDirectoryNameWritable.getCreationTime()));
		assertFalse(this.dirDirectoryNameWritable.getCreationTime().after(timeAfterConstruction));
		assertEquals(this.dirName, this.dirDirectoryNameWritable.getRoot());
		assertEquals(this.dirName, this.dirDirectoryNameWritable.getParentDirectory());
	}
	
	@Test
	public void testTerminate_legalCase() throws IllegalStateException {
		this.dirDirectoryName.terminate();
		assertTrue(this.dirDirectoryName.isTerminated());
		assertFalse(this.dirDirectoryName.canBeTerminated());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testTerminate_illegalCase() throws IllegalStateException {
		this.dirNotWritable.terminate();
	}
	
	@Test
	public void testGetNbItems(){
		assertEquals(2, this.dirName.getNbItems());
	}
	
	@Test
	public void testGetItemAt_legalCase() throws IndexOutOfBoundsException {
		assertEquals(this.dirDirectoryName, this.dirName.getItemAt(2));
	}
	
	@Test (expected = IndexOutOfBoundsException.class)
	public void testGetItemAt_illegalCase() throws IndexOutOfBoundsException {
		this.dirName.getItemAt(5);
	}
	
	@Test
	public void testCanHaveAsItem1(){
		File file = null;
		assertFalse(this.dirName.canHaveAsItem(file));
		this.dirDirectoryName.terminate();
		assertFalse(this.dirName.canHaveAsItem(dirDirectoryName));
		this.dirDirectoryNameWritable.terminate();
		assertFalse(this.dirDirectoryNameWritable.canHaveAsItem(dirNameWritable));
	}
	
	@Test
	public void testCanHaveAsItem2(){
		assertFalse(this.dirDirectoryName.canHaveAsItem(dirName));
		assertTrue(this.dirName.canHaveAsItem(dirNameWritable));
	}
	
	@Test
	public void canHaveAsItemAt(){
		assertFalse(this.dirDirectoryName.canHaveAsItemAt(dirName, 1));
		assertFalse(this.dirName.canHaveAsItemAt(dirDirectoryName, 5));
		assertFalse(this.dirName.canHaveAsItemAt(dirDirectoryNameWritable, 2));
		assertFalse(this.dirName.canHaveAsItemAt(dirNameWritable, 3));
		assertTrue(this.dirName.canHaveAsItemAt(dirDirectoryNameWritable, 1));
	}
	
	@Test
	public void testHasProperItems(){
		assertTrue(this.dirName.hasProperItems());
	}
	
	@Test
	public void testHasAsItem(){
		assertTrue(this.dirName.hasAsItem(dirDirectoryName));
		assertFalse(this.dirName.hasAsItem(dirNameWritable));
	}
	
	@Test
	public void testAddAsItem_legalCase() throws IllegalArgumentException{
		this.dirName.addAsItem(dirNameWritable);
		assertTrue(this.dirName.hasAsItem(dirNameWritable));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testAddAsItem_illegalCase1() throws IllegalArgumentException{
		this.dirName.addAsItem(dirDirectoryName);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testAddAsItem_illegalCase2() throws IllegalArgumentException{
		this.dirDirectoryName.terminate();
		this.dirName.addAsItem(this.dirDirectoryName);
	}
	
	@Test
	public void testRemoveAsItem_legalCase() throws IllegalArgumentException{
		this.dirName.removeAsItem(dirDirectoryName);
		assertFalse(this.dirName.hasAsItem(dirDirectoryName));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveAsItem_illegalCase() throws IllegalArgumentException{
		this.dirName.removeAsItem(dirNameWritable);
	}
	
	@Test
	public void testIsDirectOrIndirectSubdirectoryOf_legalCase()
			throws IllegalArgumentException {
		assertTrue(this.dirDirectoryName.isDirectOrIndirectSubdirectoryOf(dirName));
		assertFalse(this.dirNameWritable.isDirectOrIndirectSubdirectoryOf(dirName));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testIsDirectOrIndirectSubdirectoryOf_illegalCase()
			throws IllegalArgumentException {
		Directory dir = null;
		this.dirDirectoryName.isDirectOrIndirectSubdirectoryOf(dir);
	}
	
	@Test
	public void testContainsItemWithName(){
		assertTrue(this.dirName.containsItemWithName("map4"));
		assertFalse(this.dirName.containsItemWithName("Peer"));
	}
	
	@Test
	public void testGetIndexOf_legalCase() throws IllegalArgumentException {
		assertEquals(2, this.dirName.getIndexOf(dirDirectoryName));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetIndexOf_illegalCase() throws IllegalArgumentException {
		this.dirName.getIndexOf(dirNameWritable);
	}
	
	@Test
	public void testMakeRoot_legalCase()
			throws ItemNotWritableException, ItemCannotBeRootException {
		this.dirDirectoryName.makeRoot();
		assertTrue(this.dirDirectoryName.isRoot());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testMakeRoot_illegalCase1()
			throws ItemNotWritableException, ItemCannotBeRootException {
		this.dirDirectoryName.terminate();
		this.dirDirectoryName.makeRoot();
		
	}
	
}
