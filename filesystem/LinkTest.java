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
	public void fileLink_illegalCase() {
		assertFalse(fileLink.isValidName("Ik ben Linde"));
		testFile.terminate();
		fileLink.getRefDiskItem();

		
		
	}

	
	
}
