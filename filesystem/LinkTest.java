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
	
	File testFile;
	
	Directory testDirectory;
	Directory parentDirectory;
	
	Type txt;
	
	@Before
	public void setUpFixture(){
		parentDirectory = new Directory ("parent",true);
		testDirectory = new Directory (parentDirectory,"map");
		testFile = new File (testDirectory,"Bestand", txt);
		timeBeforeConstruction = new Date();
		fileLink = new FileLink("linkNaarBestand",testFile,testDirectory);
		directoryLink = new DirectoryLink("linkNaarMap",testDirectory,testDirectory);
		timeAfterConstruction = new Date();
	}
	
	@Test 
	public void fileLink_legalCase() {
		assertEquals("linkNaarBestand",fileLink.getName());
		assertTrue(fileLink.isWritable());
		assertNull(fileLink.getModificationTime());
		assertFalse(timeBeforeConstruction.after(fileLink.getCreationTime()));
		assertFalse(fileLink.getCreationTime().after(timeAfterConstruction));
	}
	
}
