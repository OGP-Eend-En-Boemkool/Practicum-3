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
	
	Type txt;
	
	@Before
	public void setUpFixture(){
		testDirectory = new Directory("map");
		timeBeforeConstruction = new Date();
		fileDirectoryNameTypeSizeWritable = new File(testDirectory,"Bestand.txt",txt,100,true);
		fileDirectoryNameType = new File(testDirectory,"Bestand.txt",txt);
		timeAfterConstruction = new Date();
		
		timeBeforeConstructionNotWritable = new Date();
		fileNotWritable = new File(testDirectory,"Bestand.txt",txt);
		timeAfterConstructionNotWritable = new Date();
	}
	
	@Test 
	public void testFileDirectoryNameTypeSizeWritable_legalCase() {
		assertEquals("bestand.txt",fileDirectoryNameTypeSizeWritable.getName());
		assertEquals("txt",fileDirectoryNameTypeSizeWritable.getType());
		assertEquals(fileDirectoryNameTypeSizeWritable.getSize(),100);
		assertTrue(fileDirectoryNameTypeSizeWritable.isWritable());
		assertNull(fileDirectoryNameTypeSizeWritable.getModificationTime());
		assertFalse(timeBeforeConstruction.after(fileDirectoryNameTypeSizeWritable.getCreationTime()));
		assertFalse(fileDirectoryNameTypeSizeWritable.getCreationTime().after(timeAfterConstruction));
	}
	
}
