package filesystem;
import static org.junit.Assert.*;
import java.util.Date;

import org.junit.*;

import filesystem.exception.*;

/**
 * A JUnit test class for testing the public methods of the File Class.
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
		assertEquals("bestand1.txt", fileDirectoryNameTypeSizeWritable.toString());
		assertTrue(fileDirectoryNameTypeSizeWritable.canBeTerminated());
		assertEquals(fileDirectoryNameTypeSizeWritable.getSize(),100);
		assertFalse(fileDirectoryNameTypeSizeWritable.isRoot());
		assertTrue(fileDirectoryNameTypeSizeWritable.isWritable());
		assertNull(fileDirectoryNameTypeSizeWritable.getModificationTime());
		assertFalse(timeBeforeConstruction.after(fileDirectoryNameTypeSizeWritable.getCreationTime()));
		assertFalse(fileDirectoryNameTypeSizeWritable.getCreationTime().after(timeAfterConstruction));
		assertEquals(testDirectory, this.fileDirectoryNameTypeSizeWritable.getRoot());
		assertEquals(testDirectory, this.fileDirectoryNameTypeSizeWritable.getParentDirectory());
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
	public void testTerminate_illegalCase() throws IllegalStateException {
		fileNotWritable.terminate();
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
		assertTrue(this.fileDirectoryNameType.isOrderedAfter("bestand1"));
		assertFalse(this.fileDirectoryNameTypeSizeWritable.isOrderedAfter("bestand2"));
	}
	
	@Test
	public void testIsOrderedBefore(){
		assertFalse(this.fileDirectoryNameType.isOrderedBefore(fileDirectoryNameTypeSizeWritable));
		assertTrue(this.fileDirectoryNameTypeSizeWritable.isOrderedBefore(fileDirectoryNameType));
		assertFalse(this.fileDirectoryNameType.isOrderedBefore("bestand1"));
		assertTrue(this.fileDirectoryNameTypeSizeWritable.isOrderedBefore("bestand2"));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_UnmodifiedFiles(){
		File one = new File(testDirectory, "one", Type.PDF);
		sleep();
		File other = new File(testDirectory, "other", Type.JAVA);
		
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other.changeName("newNameOther");
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other = new File(testDirectory, "other", Type.JAVA);
		one.changeName("newNameOne");
		assertFalse(one.hasOverlappingUsePeriod(other));	
	}
	
	@Test
	public void testHasOverlappingUsePeriod_UnmodifiedFileAndDirectory(){
		File one = new File(testDirectory, "one", Type.PDF);
		sleep();
		Directory other = new Directory("other");
		
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other.changeName("newNameOther");
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other = new Directory("other");
		one.changeName("newNameOne");
		assertFalse(one.hasOverlappingUsePeriod(other));	
	}
	
	@Test
	public void testHasOverlappingUsePeriod_UnmodifiedFileAndFileLink(){
		File one = new File(testDirectory, "one", Type.PDF);
		sleep();
		FileLink other = new FileLink("other", this.fileDirectoryNameType, testDirectory);
		
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other.changeName("newNameOther");
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other = new FileLink("other", this.fileDirectoryNameType, testDirectory);
		one.changeName("newNameOne");
		assertFalse(one.hasOverlappingUsePeriod(other));	
	}
	
	@Test
	public void testHasOverlappingUsePeriod_UnmodifiedFileAndDirectoryLink(){
		File one = new File(testDirectory, "one", Type.PDF);
		sleep();
		DirectoryLink other = new DirectoryLink("other", testDirectory, testDirectory);
		
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other.changeName("newNameOther");
		assertFalse(one.hasOverlappingUsePeriod(other));
		
		other = new DirectoryLink("other", testDirectory, testDirectory);
		one.changeName("newNameOne");
		assertFalse(one.hasOverlappingUsePeriod(other));	
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedNoOverlap_2files(){
		File one, two, three;
		one = new File(testDirectory, "one", Type.PDF);
		sleep();
		
		one.changeName("newNameOne");
        sleep();
        two = new File(testDirectory, "two", Type.JAVA);
        two.changeName("newNameTwo");
	    assertFalse(one.hasOverlappingUsePeriod(two));
	    
		two.changeName("newNameTwo2");
        sleep();
        three = new File(testDirectory, "three", Type.PDF);
        three.changeName("newNameThree");
        assertFalse(three.hasOverlappingUsePeriod(two));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedNoOverlap_fileAndDir(){
		File one, three;
		Directory two;
		one = new File(testDirectory, "one", Type.PDF);
		sleep();
		
		one.changeName("newNameOne");
        sleep();
        two = new Directory("two");
        two.changeName("newNameTwo");
	    assertFalse(one.hasOverlappingUsePeriod(two));
	    
		two.changeName("newNameTwo2");
        sleep();
        three = new File(testDirectory, "three", Type.PDF);
        three.changeName("newNameThree");
        assertFalse(three.hasOverlappingUsePeriod(two));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedNoOverlap_fileAndFilelink(){
		File one, three;
		FileLink two;
		one = new File(testDirectory, "one", Type.PDF);
		sleep();
		
		one.changeName("newNameOne");
        sleep();
        two = new FileLink("two", this.fileDirectoryNameType, this.testDirectory);
        two.changeName("newNameTwo");
	    assertFalse(one.hasOverlappingUsePeriod(two));
	    
		two.changeName("newNameTwo2");
        sleep();
        three = new File(testDirectory, "three", Type.PDF);
        three.changeName("newNameThree");
        assertFalse(three.hasOverlappingUsePeriod(two));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedNoOverlap_fileAndDirlink(){
		File one, three;
		DirectoryLink two;
		one = new File(testDirectory, "one", Type.PDF);
		sleep();
		
		one.changeName("newNameOne");
        sleep();
        two = new DirectoryLink("two", this.testDirectory, this.testDirectory);
        two.changeName("newNameTwo");
	    assertFalse(one.hasOverlappingUsePeriod(two));
	    
		two.changeName("newNameTwo2");
        sleep();
        three = new File(testDirectory, "three", Type.PDF);
        three.changeName("newNameThree");
        assertFalse(three.hasOverlappingUsePeriod(two));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_A_2files(){
	    File one, other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new File(testDirectory, "other", Type.PDF);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_A_fileAndDir(){
	    File one;
	    Directory other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new Directory("other");
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_A_fileAndFilelink(){
	    File one;
	    FileLink other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new FileLink("other", this.fileDirectoryNameType, testDirectory);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_A_fileAndDirlink(){
	    File one;
	    DirectoryLink other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new DirectoryLink("other", testDirectory, testDirectory);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_B_2files(){
       	File one, other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new File(testDirectory, "other", Type.PDF);
	
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_B_fileAndDir(){
       	File one;
       	Directory other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new Directory("other");
	
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_B_fileAndFilelink(){
       	File one;
       	FileLink other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new FileLink("other", this.fileDirectoryNameType, testDirectory);
	
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_B_fileAndDirlink(){
       	File one;
       	DirectoryLink other;
		one = new File(testDirectory, "one", Type.JAVA);
		sleep();
		other = new DirectoryLink("other", testDirectory, testDirectory);
	
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_C_2files(){
        File one, other;
		other = new File(testDirectory, "other", Type.JAVA);
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
		
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_C_fileAndDir(){
        File one;
        Directory other;
		other = new Directory("other");
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
		
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_C_fileAndFilelink(){
        File one;
        FileLink other;
		other = new FileLink("other", this.fileDirectoryNameType, testDirectory);
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
		
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_C_fileAndDirlink(){
        File one;
        DirectoryLink other;
		other = new DirectoryLink("other", testDirectory, testDirectory);
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
		
		other.changeName("newNameOther");
        sleep();
        one.changeName("newNameOne");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_D_2files(){
		File one, other;
		other = new File(testDirectory, "other", Type.JAVA);
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_D_fileAndDir(){
		File one;
		Directory other;
		other = new Directory("other");
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_D_fileAndFilelink(){
		File one;
		FileLink other;
		other = new FileLink("other", this.fileDirectoryNameType, testDirectory);
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testHasOverlappingUsePeriod_ModifiedOverlap_D_fileAndDirlink(){
		File one;
		DirectoryLink other;
		other = new DirectoryLink("other", testDirectory, testDirectory);
		sleep();
		one = new File(testDirectory, "one", Type.PDF);
	
		one.changeName("newNameOne");
        sleep();
        other.changeName("newNameOther");
        assertTrue(one.hasOverlappingUsePeriod(other));
	}
	
	@Test
	public void testMove_legalCase()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir;
		dir = new Directory("dir");
		this.fileDirectoryNameType.move(dir);
		assertEquals(dir, this.fileDirectoryNameType.getParentDirectory());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testMove_illegalCase1()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = new Directory("dir");
		this.fileDirectoryNameType.terminate();
		this.fileDirectoryNameType.move(dir);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testMove_illegalCase2()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = null;
		this.fileDirectoryNameType.move(dir);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testMove_illegalCase3()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		this.fileDirectoryNameType.move(testDirectory);
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testMove_illegalCase4()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = new Directory("dir");
		this.fileNotWritable.move(dir);
	}
	
	@Test (expected = ItemNotWritableException.class)
	public void testMove_illegalCase5()
			throws IllegalArgumentException, ItemNotWritableException, IllegalStateException {
		Directory dir = new Directory("dir", false);
		this.fileNotWritable.move(dir);
	}
	
	@Test
	public void testHasProperParentDirectory(){
		assertTrue(this.fileDirectoryNameType.hasProperParentDirectory());
	}
	
	@Test
	public void testCanHaveAsParentDirectory(){
		Directory dir = new Directory("dir");
		assertTrue(this.fileDirectoryNameType.canHaveAsParentDirectory(dir));
	}
	
	@Test
	public void testIsDirectOrIndirectParentOf(){
		assertFalse(this.fileDirectoryNameType.isDirectOrIndirectParentOf(fileDirectoryNameTypeSizeWritable));
	}
	
	private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
}
