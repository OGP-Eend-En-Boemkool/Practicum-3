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

public class LinkTest {

	Date timeBeforeConstruction;
	Date timeAfterConstruction;

	Link fileLink;
	Link directoryLink;
	Link fileLinkTerminate;
	Link directoryLinkTerminate;
	Link terminateLink;
	
	File testFile;
	File terminateFile;
	
	Directory testDirectory;
	Directory parentDirectory;
	Directory notWritableDirectory;
	Directory nullDirectory;
	
	@Before
	public void setUpFixture(){
		parentDirectory = new Directory ("parent",true);
		testDirectory = new Directory (parentDirectory,"map");
		notWritableDirectory = new Directory ("notWritable",false);
		testFile = new File (testDirectory,"Bestand", Type.TEXT);
		terminateFile = new File (testDirectory,"Terminate", Type.TEXT);
		timeBeforeConstruction = new Date();
		fileLink = new FileLink("linkNaarBestand",testFile,testDirectory);
		directoryLink = new DirectoryLink("linkNaarMap",testDirectory,testDirectory);
		fileLinkTerminate = new FileLink("linkNaarBestandTerminate",testFile,testDirectory);
		directoryLinkTerminate = new DirectoryLink("linkNaarMapTerminate",testDirectory,testDirectory);
		timeAfterConstruction = new Date();
	}
	
	@Test 
	public void fileLink_legalCase() {
		assertEquals("linkNaarBestand",fileLink.getName());
		assertTrue(fileLink.isWritable());
		assertNull(fileLink.getModificationTime());
		assertFalse(timeBeforeConstruction.after(fileLink.getCreationTime()));
		assertFalse(fileLink.getCreationTime().after(timeAfterConstruction));
		assertTrue(fileLink.isValidName("linkNaarBestand"));
		assertEquals(testFile,fileLink.getRefDiskItem());
		assertTrue(fileLink.canAcceptAsNewName("nieuweNaam"));
		assertFalse(fileLink.isTerminated());
		assertTrue(fileLink.canBeTerminated());
		assertFalse(fileLink.isRoot());
		assertTrue(fileLink.isWritable());
		assertEquals("new_item",fileLink.getDefaultName());
		fileLink.changeName("nieuweNaam");
		assertEquals("nieuweNaam",fileLink.getName());
		assertTrue(fileLink.isOrderedAfter(fileLinkTerminate));
		assertFalse(fileLink.isOrderedBefore(directoryLink));
		assertTrue(fileLink.isOrderedAfter("algebra"));
		assertTrue(fileLink.isOrderedBefore("zalig"));
		assertTrue(fileLink.isValidCreationTime(fileLink.getCreationTime()));
		assertTrue(fileLink.canHaveAsModificationTime(fileLink.getModificationTime()));
		fileLinkTerminate.changeName("bubbelvis");
		assertTrue(fileLink.hasOverlappingUsePeriod(fileLinkTerminate));
		fileLinkTerminate.terminate();
		assertTrue(fileLinkTerminate.isTerminated);
		assertEquals(testDirectory,fileLink.getParentDirectory());
		assertEquals(parentDirectory,fileLink.getRoot());
		assertFalse(fileLink.isDirectOrIndirectParentOf(parentDirectory));
		assertTrue(parentDirectory.isDirectOrIndirectParentOf(fileLink));
		assertTrue(testDirectory.isDirectOrIndirectParentOf(fileLink));
		fileLink.move(parentDirectory);
		assertEquals(parentDirectory,fileLink.getParentDirectory());
		assertTrue(fileLink.hasProperParentDirectory());
		assertTrue(fileLink.canHaveAsParentDirectory(parentDirectory));
	}
	
	@Test (expected = UnvalidLinkException.class)
	public void fileLink_illegalCase1() {
		assertFalse(fileLink.isValidName("Ik ben Linde"));
		testFile.terminate();
		fileLink.getRefDiskItem();
	}
	
	@Test 
	public void fileLink_illegalCase2() {
		assertFalse(fileLink.canAcceptAsNewName("De wereld is een toverbal"));
		assertFalse(fileLink.canAcceptAsNewName("linkNaarBestand"));
		assertFalse(fileLink.canAcceptAsNewName("linkNaarMap"));
		fileLink.terminate();
		assertFalse(fileLink.canAcceptAsNewName("Linde"));	
	}
	
	@Test (expected = IllegalStateException.class)
	public void fileLink_illegalCase3() {
		testDirectory.setWritable(false);
		assertFalse(fileLink.canBeTerminated());
		fileLink.terminate();
		assertFalse(fileLink.canBeTerminated());
	}

	@Test (expected = IllegalStateException.class)
	public void fileLink_illegalCase4() {
		fileLink.terminate();
		fileLink.changeName("blub");
	}
	
	@Test 
	public void fileLink_illegalCase5() {
		String name = null;
		assertFalse(fileLink.isOrderedAfter(nullDirectory));
		assertFalse(fileLink.isOrderedAfter("zalig"));
		assertFalse(fileLink.isOrderedAfter(directoryLink));
		assertFalse(fileLink.isOrderedAfter(name));
	}
	
	@Test
	public void fileLink_illegalCase6() {
		String name = null;
		assertFalse(fileLink.isOrderedBefore(nullDirectory));
		assertFalse(fileLink.isOrderedBefore(name));
		fileLink.changeName("Zever");
		assertFalse(fileLink.isOrderedBefore(directoryLink));
		assertFalse(fileLink.isOrderedBefore("Aap"));
	}
	
	@Test
	public void fileLink_illegalCase7() {
		Date date = null;
		assertFalse(fileLink.isValidCreationTime(date));
	}
	
	@Test
	public void fileLink_illegalCase8() {
		Date date = null;
		assertTrue(fileLink.canHaveAsModificationTime(date));	
	}
	
	@Test
	public void fileLink_illegalCase9() {
		assertFalse(fileLink.hasOverlappingUsePeriod(nullDirectory));
		directoryLink.changeName("woohoo");
		assertFalse(fileLink.hasOverlappingUsePeriod(directoryLink));
		fileLink.changeName("ooievaar");
		assertFalse(fileLink.hasOverlappingUsePeriod(directoryLinkTerminate));
		nullDirectory = new Directory(testDirectory,"null");
		assertFalse(fileLink.hasOverlappingUsePeriod(nullDirectory));
		assertFalse(nullDirectory.hasOverlappingUsePeriod(fileLink));
	}
	
	@Test (expected = IllegalStateException.class)
	public void fileLink_illegalCase10() {
		fileLink.terminate();
		fileLink.move(parentDirectory);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void fileLink_illegalCase11() {
		fileLink.move(nullDirectory);
		fileLink.move(testDirectory);
		fileLink.move(notWritableDirectory);
	}
	
	@Test 
	public void fileLink_illegalCase12() {
		fileLink.terminate();
		assertFalse(fileLink.canHaveAsParentDirectory(notWritableDirectory));
	}
	
	@Test
	public void fileLink_illegalCase13() {
		Directory dir = new Directory ("flubber");
		dir.terminate();
		assertFalse(fileLink.canHaveAsParentDirectory(dir));
	}
	
	@Test (expected = IllegalStateException.class)
	public void fileLink_illegalCase14() {
		fileLink.terminate();
		fileLink.setParentDirectory(parentDirectory);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void fileLink_illegalCase15() {
		fileLink.setParentDirectory(notWritableDirectory);
	}
	
	@Test 
	public void directoryLink_legalCase() {
		assertEquals("linkNaarMap",directoryLink.getName());
		assertTrue(directoryLink.isWritable());
		assertNull(directoryLink.getModificationTime());
		assertFalse(timeBeforeConstruction.after(directoryLink.getCreationTime()));
		assertFalse(directoryLink.getCreationTime().after(timeAfterConstruction));
		assertTrue(directoryLink.isValidName("linkNaarMap"));
		assertEquals(testDirectory,directoryLink.getRefDiskItem());
		assertTrue(directoryLink.canAcceptAsNewName("nieuweNaam"));
		assertFalse(directoryLink.isTerminated());
		assertTrue(directoryLink.canBeTerminated());
		assertFalse(directoryLink.isRoot());
		assertTrue(directoryLink.isWritable());
		assertEquals("new_item",directoryLink.getDefaultName());
		directoryLink.changeName("nieuweNaam");
		assertEquals("nieuweNaam",directoryLink.getName());
		assertTrue(directoryLink.isOrderedAfter(directoryLinkTerminate));
		assertFalse(directoryLink.isOrderedBefore(fileLink));
		assertTrue(directoryLink.isOrderedAfter("algebra"));
		assertTrue(directoryLink.isOrderedBefore("zalig"));
		assertTrue(directoryLink.isValidCreationTime(directoryLink.getCreationTime()));
		assertTrue(directoryLink.canHaveAsModificationTime(directoryLink.getModificationTime()));
		directoryLinkTerminate.changeName("bubbelvis");
		assertTrue(directoryLink.hasOverlappingUsePeriod(directoryLinkTerminate));
		directoryLinkTerminate.terminate();
		assertTrue(directoryLinkTerminate.isTerminated);
		assertEquals(testDirectory,directoryLink.getParentDirectory());
		assertEquals(parentDirectory,directoryLink.getRoot());
		assertFalse(directoryLink.isDirectOrIndirectParentOf(parentDirectory));
		assertTrue(parentDirectory.isDirectOrIndirectParentOf(directoryLink));
		assertTrue(testDirectory.isDirectOrIndirectParentOf(directoryLink));
		directoryLink.move(parentDirectory);
		assertEquals(parentDirectory,directoryLink.getParentDirectory());
		assertTrue(directoryLink.hasProperParentDirectory());
		assertTrue(directoryLink.canHaveAsParentDirectory(parentDirectory));
	}
	
	@Test 
	public void directoryLink_illegalCase1() {
		assertFalse(directoryLink.isValidName("Ik ben Linde"));
	}
	
	@Test 
	public void directoryLink_illegalCase2() {
		assertFalse(directoryLink.canAcceptAsNewName("De wereld is een toverbal"));
		assertFalse(directoryLink.canAcceptAsNewName("linkNaarMap"));
		assertFalse(directoryLink.canAcceptAsNewName("linkNaarBestand"));
		directoryLink.terminate();
		assertFalse(directoryLink.canAcceptAsNewName("Linde"));	
	}
	
	@Test (expected = IllegalStateException.class)
	public void directoryLink_illegalCase3() {
		testDirectory.setWritable(false);
		assertFalse(directoryLink.canBeTerminated());
		directoryLink.terminate();
		assertFalse(directoryLink.canBeTerminated());
	}

	@Test (expected = IllegalStateException.class)
	public void directoryLink_illegalCase4() {
		directoryLink.terminate();
		directoryLink.changeName("blub");
	}
	
	@Test 
	public void directoryLink_illegalCase5() {
		String name = null;
		assertFalse(directoryLink.isOrderedAfter(nullDirectory));
		assertFalse(directoryLink.isOrderedAfter("zalig"));
		assertFalse(directoryLink.isOrderedAfter(directoryLinkTerminate));
		assertFalse(directoryLink.isOrderedAfter(name));
	}
	
	@Test
	public void directoryLink_illegalCase6() {
		String name = null;
		assertFalse(directoryLink.isOrderedBefore(nullDirectory));
		assertFalse(directoryLink.isOrderedBefore(name));
		directoryLink.changeName("Zever");
		assertFalse(directoryLink.isOrderedBefore(fileLink));
		assertFalse(directoryLink.isOrderedBefore("Aap"));
	}
	
	@Test
	public void directoryLink_illegalCase7() {
		Date date = null;
		assertFalse(directoryLink.isValidCreationTime(date));
	}
	
	@Test
	public void directoryLink_illegalCase8() {
		Date date = null;
		assertTrue(directoryLink.canHaveAsModificationTime(date));	
	}
	
	@Test
	public void directoryLink_illegalCase9() {
		assertFalse(directoryLink.hasOverlappingUsePeriod(nullDirectory));
		fileLink.changeName("woohoo");
		assertFalse(directoryLink.hasOverlappingUsePeriod(fileLink));
		directoryLink.changeName("ooievaar");
		assertFalse(directoryLink.hasOverlappingUsePeriod(directoryLinkTerminate));
		nullDirectory = new Directory(testDirectory,"null");
		assertFalse(directoryLink.hasOverlappingUsePeriod(nullDirectory));
		assertFalse(nullDirectory.hasOverlappingUsePeriod(directoryLink));
	}
	
	@Test (expected = IllegalStateException.class)
	public void directoryLink_illegalCase10() {
		directoryLink.terminate();
		directoryLink.move(parentDirectory);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void directoryLink_illegalCase11() {
		directoryLink.move(nullDirectory);
		directoryLink.move(testDirectory);
		directoryLink.move(notWritableDirectory);
	}
	
	@Test 
	public void directoryLink_illegalCase12() {
		directoryLink.terminate();
		assertFalse(directoryLink.canHaveAsParentDirectory(notWritableDirectory));
	}
	
	@Test
	public void directoryLink_illegalCase13() {
		Directory dir = new Directory ("flubber");
		dir.terminate();
		assertFalse(directoryLink.canHaveAsParentDirectory(dir));
	}
	
	@Test (expected = IllegalStateException.class)
	public void directoryLink_illegalCase14() {
		directoryLink.terminate();
		directoryLink.setParentDirectory(parentDirectory);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void directoryLink_illegalCase15() {
		directoryLink.setParentDirectory(notWritableDirectory);
	}
}
