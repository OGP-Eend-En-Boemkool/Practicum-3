package filesystem;
import static org.junit.Assert.*;
import java.util.Date;

import org.junit.*;

import filesystem.exception.*;

/**
 * A JUnit test class for testing the public methods of the File Class
 * 
 * @author Lotte en Linde
 *
 */
public class FileTest {
	
	Date timeBeforeConstruction;
	Date timeAfterConstruction;
	Date timeBeforeConstructionNotWritable;
	Date timeAfterConstructionNotWritable;


	File fileDirectoryNameTypeSizeWritable ;
	File fileDirectoryNameType;
	File fileNotWritable;
	
	Directory testDirectory;
	
	@Before
	public void setUpFixture(){
		testDirectory = new Directory("map");
		
		timeBeforeConstruction = new Date();
		fileDirectoryNameTypeSizeWritable = new File(testDirectory,"bestand1", Type.TEXT,100,true);
		fileDirectoryNameType = new File(testDirectory,"bestand2",Type.TEXT);
		timeAfterConstruction = new Date();
		
		timeBeforeConstructionNotWritable = new Date();
		fileNotWritable = new File(testDirectory,"bestand3",Type.TEXT, 600, false);
		timeAfterConstructionNotWritable = new Date();
		
	}
	
	@Test
	public void testFileDirectoryNameTypeSizeWritable_legalCase() {
		assertEquals("bestand1",fileDirectoryNameTypeSizeWritable.getName());
		assertEquals(Type.TEXT,fileDirectoryNameTypeSizeWritable.getType());
		assertEquals("bestand2.txt", fileDirectoryNameType.toString());
		assertTrue(fileDirectoryNameTypeSizeWritable.canBeTerminated());
		assertTrue(fileDirectoryNameType.canBeTerminated());
		assertFalse(fileNotWritable.canBeTerminated());
		assertEquals(fileDirectoryNameTypeSizeWritable.getSize(),100);
		assertFalse(fileDirectoryNameTypeSizeWritable.isRoot());
		assertTrue(fileDirectoryNameTypeSizeWritable.isWritable());
		assertNull(fileDirectoryNameTypeSizeWritable.getModificationTime());
		assertFalse(timeBeforeConstruction.after(fileDirectoryNameTypeSizeWritable.getCreationTime()));
		assertFalse(fileDirectoryNameTypeSizeWritable.getCreationTime().after(timeAfterConstruction));
	}
	
	@Test
	public void testEnlarge_legalCase() throws ItemNotWritableException {
		fileDirectoryNameTypeSizeWritable.enlarge(200);
		assertEquals(300, fileDirectoryNameTypeSizeWritable.getSize());
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testEnlarge_illegalCase() throws ItemNotWritableException {
		fileNotWritable.enlarge(300);
	}
	
	@Test
	public void testShorten_legalCase() throws ItemNotWritableException {
		fileDirectoryNameTypeSizeWritable.shorten(50);
		assertEquals(50, fileDirectoryNameTypeSizeWritable.getSize());
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testShorten_illegalCase() throws ItemNotWritableException {
		fileNotWritable.shorten(300);
	}
	
	@Test
	public void testCanAcceptAsNewName_legalCase() {
		assertTrue(fileDirectoryNameTypeSizeWritable.canAcceptAsNewName("eend"));
	}
	
	@Test
	public void testCanAcceptAsNewName_illegalCase() {
		assertFalse(fileDirectoryNameTypeSizeWritable.canAcceptAsNewName("€€nd"));
		assertFalse(fileNotWritable.canAcceptAsNewName("eend"));
	}
	
	@Test (expected = ItemCannotBeRootException.class)
	public void testMakeRoot_illegalCase()
			throws ItemNotWritableException, ItemCannotBeRootException {
		fileNotWritable.makeRoot();
	}
	
	@Test
	public void testTerminate_legalCase() throws IllegalStateException {
		fileDirectoryNameTypeSizeWritable.terminate();
		assertTrue(fileDirectoryNameTypeSizeWritable.isTerminated());
		assertFalse(fileDirectoryNameTypeSizeWritable.canBeTerminated());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testTerminate_illegalCase1() throws IllegalStateException {
		fileNotWritable.terminate();
	}
	
	@Test
	public void testTerminate_illegalCase2() throws IllegalStateException {
		fileDirectoryNameTypeSizeWritable.terminate();
		assertFalse(fileDirectoryNameTypeSizeWritable.canBeTerminated());
	}
	
	@Test
	public void testChangeName_legalCase() 
			throws ItemNotWritableException, IllegalStateException {
		int index = this.testDirectory.getIndexOf(fileDirectoryNameTypeSizeWritable);
		fileDirectoryNameTypeSizeWritable.changeName("boemkool");
		assertEquals("boemkool", this.fileDirectoryNameTypeSizeWritable.getName());
		assertTrue(index != this.testDirectory.getIndexOf(fileDirectoryNameTypeSizeWritable));
	}
	
	@Test (expected = IllegalStateException.class)
	public void testChangeName_illegalCase1()
			throws ItemNotWritableException, IllegalStateException {
		this.fileDirectoryNameTypeSizeWritable.terminate();
		this.fileDirectoryNameTypeSizeWritable.changeName("Olaf");
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testChangeName_illegalCase2()
			throws ItemNotWritableException, IllegalStateException {
		this.fileNotWritable.changeName("Olaf");
	}
	
	@Test
	public void testChangeName_illegalCase3()
			throws ItemNotWritableException, IllegalStateException {
		String name = this.fileDirectoryNameTypeSizeWritable.getName();
		this.fileDirectoryNameType.changeName("$dollar$");
		assertEquals(name, fileDirectoryNameTypeSizeWritable.getName());
	}
	
	@Test
	public void testIsOrderedAfter(){
		assertTrue(this.fileDirectoryNameType.isOrderedAfter(fileDirectoryNameTypeSizeWritable));
		assertFalse(this.fileDirectoryNameTypeSizeWritable.isOrderedAfter(fileDirectoryNameType));
	}
	
}
