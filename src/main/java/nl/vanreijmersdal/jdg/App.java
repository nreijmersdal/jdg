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
import java.util.Date;
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
        data.outputFilename = "data.csv";
        data.outputRowCount = 10000;
        
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
    int currentRow = 0;
    int outputRowCount = 1;
    String outputFilename = "output.csv";
    // Columns contain a list of strings, first is the type, the rest are parameters
    private List<List<String>> columns = new ArrayList<List<String>>();
    
    String generateRow() throws ParseException {
        String row = "";
        row += ++currentRow;
        
     	Iterator<List<String>> iterator = columns.iterator();
        while(iterator.hasNext()) {
            row += seperator;
            Iterator<String> column = iterator.next().iterator();
            String name = column.next();
            String type = column.next();
            
            // Settings
            String settings[] = new String[4];
            int counter = 0;
            while(column.hasNext()) {
                settings[counter] = column.next();
                counter++;
            }
            
            // Add column data to the row
            if(type.equals("string")) {
                row += Generator.genString(Integer.parseInt(settings[0]));
            }
            if(type.equals("float")) {
                row += Generator.genFloat(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]), Integer.parseInt(settings[2]));
            }
            if(type.equals("int")) {
                row += Generator.genInt(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]));
            }
            if(type.equals("date")) {
                row += Generator.genDate(settings[0], settings[1]);
            }            
        }
        return row;
    }

    void generate() throws ParseException {
        File file = new File(outputFilename);
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
            for (int i = 0; i < outputRowCount; i++) {
                output.append(generateRow());
                output.newLine();
            }
            output.close();
        } catch (IOException e) {
        }
    }

    /**
     * Adds a column to the output file
     * First argument should be the type
     * Then the settings for the type
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
    static String genString(int lenght) {
        Random r = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890";
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
        String startYear  = start.substring(0, 3);
        String startMonth = start.substring(4, 5);
        String startDay   = start.substring(6, 7);
        String endYear  = end.substring(0, 3);
        String endMonth = end.substring(4, 5);
        String endDay   = end.substring(6, 7);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date startDate = dateFormat.parse(start);
        long startTimestamp = startDate.getTime();

        Date endDate = dateFormat.parse(end);
        long endTimestamp = endDate.getTime();
        
        long diff = endTimestamp - startTimestamp + 1;
        Timestamp newTimestamp = new Timestamp(startTimestamp + (long)(Math.random() * diff));
        
        return dateFormat.format(newTimestamp);
    }
}