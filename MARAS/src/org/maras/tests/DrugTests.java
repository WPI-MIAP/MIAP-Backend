/**
 * 
 */
package org.maras.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.maras.framework.Drug;

/**
 * @author Andrew Schade
 *
 */
public class DrugTests {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Drug.readDrugs("C:\\wpi\\MARAS\\test2.json");
	}

	@Test
	public void testObvious() {
		assertEquals("aspirin", Drug.match("aspirin".toLowerCase()).getShortName());
	}
	@Test
	public void testDosage() {
		assertEquals("aspirin", Drug.match("aspirin500mg").getShortName());
	}
	@Test
	public void testWords() {
		assertEquals("aspirin", Drug.match("aspirinpill").getShortName());
	}
	@Test
	public void testCombination()
	{
		assertEquals("canestenv", Drug.match("CanestenCombi500mgPessary2Cream".toLowerCase()).getShortName());
	}

}
