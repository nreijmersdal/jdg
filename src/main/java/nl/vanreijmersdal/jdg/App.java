package nl.vanreijmersdal.jdg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Java Data Generator
 */
public class App {
    public static void main( String[] args ) throws ParseException {
        Data data = new Data();

        //Settings
        data.filename = "data.csv";
        data.seperator = '\t';
        data.numberOfResults = 10000;
        
        // Add columns
        data.addColumn("DATE", "date", "20100101", "20130118");
        data.addColumn("STRING", "string", "10");
        data.addColumn("INT", "int", "10", "1000");
        data.addColumn("FLOAT", "float", "10", "1000", "3");
        data.addColumn("LONGSTRING", "string", "255");
        
        // Create file
        data.generate();
    }
}

class Data {
    char seperator = '\t';
    int rowId = 0;
    int numberOfResults = 1;
    String filename = "output.csv";
    
    // columns contain a list of strings, first is the type, the rest are parameters
    private List<List<String>> columns = new ArrayList<List<String>>();
    
    String generateRow() throws ParseException {
        String row = "" + ++rowId;
        
     	Iterator<List<String>> iterator = columns.iterator();
        while(iterator.hasNext()) {
            row += seperator;
            Iterator<String> column = iterator.next().iterator();
            String name = column.next();
            String type = column.next();
            
            // Settings
            String settings[] = new String[3];
            int key = 0;
            while(column.hasNext()) {
                settings[key++] = column.next();
            }
            
            // Append column data to the row
            if(type.equals("string")) {
                row += Generator.genString(Integer.parseInt(settings[0]));
            } else if(type.equals("float")) {
                row += Generator.genFloat(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]), Integer.parseInt(settings[2]));
            } else if(type.equals("int")) {
                row += Generator.genInt(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]));
            } else if(type.equals("date")) {
                row += Generator.genDate(settings[0], settings[1]);
            }            
        }
        return row;
    }

    void generate() throws ParseException {
        File file = new File(filename);
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            // Header
            String header = "ID";
            Iterator<List<String>> iterator = columns.iterator();
            while(iterator.hasNext()) {
                header += seperator;
                Iterator<String> column = iterator.next().iterator();
                String columnName = column.next();
                header += columnName;
            }
            output.append(header);
            output.newLine();
            
            // Rows
            for (int i = 0; i < numberOfResults; i++) {
                output.append(generateRow());
                output.newLine();
            }
            output.close();
        } catch (IOException e) {
        }
    }

    /**
     * Adds a column to the output file
     * First argument is the field name
     * Second argument is the type
     * Then come the settings for the type
     * 
     * @param args 
     */
    void addColumn(String... args) {
       ArrayList<String> item = new ArrayList<String>();
       item.addAll(Arrays.asList(args));
       columns.add(item);     
    }
}

class Generator {
    static String alphabet = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890";
    static String alphabet_utf8 = "àáâãäåæ";

    static String genString(int lenght) {
        Random r = new Random();
        String result = "";
        for (int i = 0; i < lenght; i++) {
            result += alphabet.charAt(r.nextInt(alphabet.length()));
        }

        return result;
    }   

    static String genFloat(int min, int max, int decimals) {
        Random r = new Random();
        int randomNumber = r.nextInt(max - min + 1) + min;  
        
        int minDecimals = 0;
        int maxDecimals = (int) (Math.pow(10, decimals) - 1);
        String randomDecimal = Integer.toString(r.nextInt(maxDecimals - minDecimals + 1) + minDecimals);
        while(randomDecimal.length() < decimals){
            randomDecimal = "0" + randomDecimal;
        }
        
        return randomNumber + "." + randomDecimal;
    }

    static String genInt(int min, int max) {
        Random r = new Random();
        return Integer.toString(r.nextInt(max - min + 1) + min);  
    }

    static String genDate(String start, String end) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        long startTimestamp = dateFormat.parse(start).getTime();
        long endTimestamp = dateFormat.parse(end).getTime();
        long diff = endTimestamp - startTimestamp + 1;

        Timestamp newTimestamp = new Timestamp(startTimestamp + (long)(Math.random() * diff));
        
        return dateFormat.format(newTimestamp);
    }
}