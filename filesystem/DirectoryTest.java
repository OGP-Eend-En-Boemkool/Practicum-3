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
		assertTrue(this.dirDirectoryNameWritable.isWritable());
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
	
	@Test
	public void testCanAcceptAsNewName_legalCase() {
		assertTrue(dirNameWritable.canAcceptAsNewName("eend"));
	}
	
	@Test
	public void testCanAcceptAsNewName_illegalCase() {
		assertFalse(dirNameWritable.canAcceptAsNewName("€€nd"));
		assertFalse(dirNotWritable.canAcceptAsNewName("eend"));
	}

	@Test (expected = IllegalStateException.class)
	public void testChangeName_illegalCase1()
			throws ItemNotWritableException, IllegalStateException {
		this.dirNameWritable.terminate();
		this.dirNameWritable.changeName("Olaf");
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testChangeName_illegalCase2()
			throws ItemNotWritableException, IllegalStateException {
		this.dirNotWritable.changeName("Olaf");
	}
	
	@Test
	public void testChangeName_illegalCase3()
			throws ItemNotWritableException, IllegalStateException {
		String name = this.dirNameWritable.getName();
		this.dirName.changeName("$dollar$");
		assertEquals(name, dirNameWritable.getName());
	}
	
	@Test
	public void testIsOrderedAfter(){
		assertTrue(this.dirDirectoryName.isOrderedAfter(dirDirectoryNameWritable));
		assertFalse(this.dirDirectoryNameWritable.isOrderedAfter(dirDirectoryName));
		assertTrue(this.dirDirectoryName.isOrderedAfter("bestand1"));
	}
	
	@Test
	public void testIsOrderedBefore(){
		assertFalse(this.dirDirectoryName.isOrderedBefore(dirDirectoryNameWritable));
		assertTrue(this.dirDirectoryNameWritable.isOrderedBefore(dirDirectoryName));
		assertFalse(this.dirDirectoryName.isOrderedBefore("bestand1"));
		assertFalse(this.dirDirectoryNameWritable.isOrderedBefore("bestand2"));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_UnmodifiedFiles(){
		File one = new File(dirDirectoryName, "one", Type.PDF);
		sleep();
		File other = new File(dirDirectoryName, "other", Type.JAVA);
		
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other.changeName("newNameOther");
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other = new File(dirDirectoryName, "other", Type.JAVA);
		one.changeName("newNameOne");
		assertFalse(one.hasOverlappingUsePeriod(other));	
	}
	
	@Test
	public void testMove_legalCase()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir;
		dir = new Directory("dir");
		this.dirDirectoryName.move(dir);
		assertEquals(dir, this.dirDirectoryName.getParentDirectory());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testMove_illegalCase1()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = new Directory("dir");
		this.dirDirectoryName.terminate();
		this.dirDirectoryName.move(dir);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testMove_illegalCase2()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = null;
		this.dirDirectoryName.move(dir);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testMove_illegalCase3()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		this.dirDirectoryName.move(dirName);
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testMove_illegalCase4()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = new Directory("dir");
		this.dirNotWritable.move(dir);
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testMove_illegalCase5()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = new Directory("dir", false);
		this.dirNotWritable.move(dir);
	}
	
	@Test
	public void testHasProperParentDirectory(){
		assertTrue(this.dirDirectoryName.hasProperParentDirectory());
	}
	
	@Test
	public void testCanHaveAsParentDirectory(){
		Directory dir = new Directory("dir");
		assertTrue(this.dirDirectoryName.canHaveAsParentDirectory(dir));
	}
	
	@Test
	public void testIsDirectOrIndirectParentOf(){
		assertFalse(this.dirDirectoryName.isDirectOrIndirectParentOf(dirDirectoryNameWritable));
	}
	
	private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	@Test (expected = ItemNotWritableException.class)
	public void testMakeRoot_illegalCase2()
			throws ItemNotWritableException, ItemCannotBeRootException {
		Directory dir = new Directory(this.dirName, "dir", false);
		dir.makeRoot();
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testMakeRoot_illegalCase3()
			throws ItemNotWritableException, ItemCannotBeRootException {
		Directory dir = new Directory(this.dirName, "dir");
		dirName.setWritable(false);
		dir.makeRoot();
	}
	
	@Test
	public void testGetTotalDiskUsage() throws IndexOutOfBoundsException {
		File file = new File(this.dirNameWritable, "file", Type.JAVA, 200, true);
		assertEquals(200, this.dirNameWritable.getTotalDiskUsage());
	}

}
