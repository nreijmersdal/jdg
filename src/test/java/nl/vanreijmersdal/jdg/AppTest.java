package nl.vanreijmersdal.jdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import junit.framework.TestCase;

/**
 * Unit test for jdg App.
 */
public class AppTest extends TestCase {

    public void testRandomStringLenght3() {
        String string = Generator.genString(3);
        assertEquals(3,string.length());
    }

    public void testRandomStringLenght5() {
        String string = Generator.genString(5);
        assertEquals(5,string.length());
    }

    public void testRandomInt() {
        // Generate a float between 0 and 10000 with 2 decimals
        int value = Integer.parseInt(Generator.genInt(0,10000));
        if(value < 0 || value > 10000) {
            fail("Returned value is outside of defined range");
        }
    }
    
    public void testRandomFloat() {
        // Generate a float between 0 and 10000 with 2 decimals
        String value = Generator.genFloat(0,10000,2);
        String[] parts = value.split("\\.");
        int intpart = Integer.parseInt(parts[0]);
        if(intpart < 0 || intpart > 10000) {
            fail("Returned value is outside of defined range");
        }
        assertEquals(2, parts[1].length());
    }

    public void testRandomMultipleInt() {
        String value = Generator.genMultipleInt("|",4,0,10000);
        String[] parts = value.split("\\|");
        int intpart;
        int counter = 0;
        for (String stringValue : parts) {
          intpart = Integer.parseInt(stringValue);
          if(intpart < 0 || intpart > 10000) {
            fail("Returned value is outside of defined range");       
          } else {
            counter++;
          }
        }
        assertEquals(4, counter);
    }

    public void testRandomMultipleIntDiffSeperator() {
        String value = Generator.genMultipleInt("-",4,0,10000);
        String[] parts = value.split("-");
        int intpart;
        int counter = 0;
        for (String stringValue : parts) {
          intpart = Integer.parseInt(stringValue);
          if(intpart < 0 || intpart > 10000) {
            fail("Returned value is outside of defined range");       
          } else {
            counter++;
          }
        }
        assertEquals(4, counter);
    }
    
    public void testRandomFloat2() {
        // Generate a float between 0 and 10000 with 2 decimals
        String value = Generator.genFloat(0,10000,9);
        String[] parts = value.split("\\.");
        int intpart = Integer.parseInt(parts[0]);
        if(intpart < 0 || intpart > 10000) {
            fail("Returned value is outside of defined range");
        }
        assertEquals(9, parts[1].length());
    }
    
    public void testRandomDate() throws ParseException {
        String date = Generator.genDate("20100101","20121231");
        if(Integer.parseInt(date) < 20100101 || Integer.parseInt(date) > 20121231) {
            fail("Returned value is outside of defined range");
        }       
        assertEquals(8, date.length());
    }
    
    public void testDataRow() throws ParseException {
        Data data = new Data();
        String row = data.generateRow();
        assertEquals("1", row);  
    }

    public void testDataTwoRows() throws ParseException {
        Data data = new Data();
        String row = data.generateRow();
        String row2 = data.generateRow();
        assertEquals("1", row);  
        assertEquals("2", row2);  
    }

    public void testDataAddColumn() throws ParseException {
        Data data = new Data();
        data.addColumn("FIELD","string", "10");
        String row = data.generateRow();
        String[] parts = row.split("\t");
        assertEquals("1", parts[0]);  
        assertEquals(10,parts[1].length());
    }  

    public void testDataAddTwoColumns() throws ParseException {
        Data data = new Data();
        data.addColumn("FIELD","string", "10");
        data.addColumn("FIELD","string", "5");
        String row = data.generateRow();
        String[] parts = row.split("\t");
        assertEquals("1", parts[0]);  
        assertEquals(10,parts[1].length());
        assertEquals(5,parts[2].length());
    }  

    public void testDataMixed() throws ParseException {
        Data data = new Data();
        data.addColumn("STRING","string", "10");
        data.addColumn("FLOAT","float", "100", "999", "2");
        data.addColumn("INT","int", "100", "999");
        data.addColumn("DATE","date", "19993112", "20010101");
        data.addColumn("MULTIPLEINT","multipleint", "|", "3", "100", "999");
        String row = data.generateRow();
        String[] parts = row.split("\t");
        assertEquals("1", parts[0]);  
        assertEquals(10,parts[1].length());
        assertEquals(6,parts[2].length());
        assertEquals(3,parts[3].length());       
        assertEquals(8,parts[4].length());
        assertEquals(11, parts[5].length());
    }  
    
    public void testDataSeperator() throws ParseException {
        Data data = new Data();
        data.seperator = ',';
        data.addColumn("FIELD","string", "6");
        String row = data.generateRow();
        String[] parts = row.split(",");
        assertEquals("1", parts[0]);  
        assertEquals(6,parts[1].length());
   }
    
    public void testDataFileCreation() throws ParseException {
        Data data = new Data();
        data.generate();
        File f = new File("output.csv");
        assertTrue(f.exists());
    }

    public void testDataFileContents() throws ParseException {
        Data data = new Data();
        String row = "";
        data.generate();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader("output.csv"));
            row = buffer.readLine(); // header
            row = buffer.readLine();
            buffer.close();
        } catch (IOException e) {
        }
        assertEquals("1", row);  
    }

    public void testDataFileNumberOfLinesTen() throws ParseException {
        Data data = new Data();
        data.numberOfResults = 10;
        data.generate();
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader("output.csv"));
            lnr.skip(Long.MAX_VALUE);
            assertEquals(10 + 1, lnr.getLineNumber());  
        } catch (IOException e) {
        }
    }

    public void testDataFileNumberOfLines1001() throws ParseException {
        Data data = new Data();
        data.numberOfResults = 1001;
        data.generate();
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader("output.csv"));
            lnr.skip(Long.MAX_VALUE);
            assertEquals(1001 + 1, lnr.getLineNumber());  
        } catch (IOException e) {
        }
    }
    
    public void testDataFileHeader() throws ParseException {
        Data data = new Data();
        String header = "";
        data.addColumn("FIELDNAME1","string", "10");
        data.addColumn("FIELDNAME2","string", "10");
        data.generate();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader("output.csv"));
            header = buffer.readLine();
            buffer.close();
        } catch (IOException e) {
        }
        assertEquals("ID\tFIELDNAME1\tFIELDNAME2", header);
    }
   
}
