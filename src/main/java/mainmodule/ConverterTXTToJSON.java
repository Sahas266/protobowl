package mainmodule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConverterTXTToJSON {

	public static Map<Integer, HashMap<String,String>> jsonMap = new HashMap<Integer, HashMap<String,String>>();
	public static List<String> fileAsStrings = new ArrayList<String>();
	public static String category = new String();
	public static String path = new String();
	public static String name = new String();
	
	public static void processFile(String file) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				fileAsStrings.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void fillJsonMap1() {
		HashMap<String,String> object = new HashMap<String,String>();
		Integer counter=1;
		for (String s : fileAsStrings) {
			if (!s.startsWith("Answer") || !s.startsWith("ANSWER")) {
				object.put("text", s);
			} else {
				object.put("answer", s);
			}
			if (object.containsKey("text") && object.containsKey("answer")) {
				object.put("category", category);
				jsonMap.put(counter,object);
				counter++;
				object = new HashMap<String,String>();
			}
		}
	} 
	
	public static void fillJsonMap2() {
		HashMap<String,String> object = new HashMap<String,String>();
		Integer counter=1;
		StringBuilder sb = new StringBuilder();
		for (String s : fileAsStrings) {
			if (!s.startsWith("ANSWER")) {
				sb.append(s).append(" ");
			} else {
				object.put("text", sb.toString());
				object.put("answer", s);
				sb = new StringBuilder();
			}
			if (object.containsKey("text") && object.containsKey("answer")) {
				object.put("category", category);
				jsonMap.put(counter,object);
				counter++;
				object = new HashMap<String,String>();
			}
		}
	} 
	
	public static void fillJsonMapMultipleChoice() {
		HashMap<String,String> object = new HashMap<String,String>();
		Integer counter=1;
		StringBuilder sb = new StringBuilder();
		for (String s : fileAsStrings) {
			if (!s.startsWith("ANSWER")) {
				sb.append(s).append(" NEWLINE ");
			} else {
				object.put("text", sb.toString());
				object.put("answer", s);
				sb = new StringBuilder();
			}
			if (object.containsKey("text") && object.containsKey("answer")) {
				object.put("category", category);
				jsonMap.put(counter,object);
				counter++;
				object = new HashMap<String,String>();
			}
		}
	} 
	
	public static void writeJsonMapToFile(String file) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    StringBuilder sb1 = new StringBuilder();
	    sb1.append("{").append("\n");
	    sb1.append("\"data\": {").append("\n");
	    sb1.append("\"num_tossups_found\": ").append("1,").append("\n");
	    sb1.append("\"num_tossups_shown\": ").append("1,").append("\n");
	    sb1.append("\"tossups\": [").append("\n");
	    writer.write(sb1.toString());
		for (Map.Entry<Integer,HashMap<String,String>> entry : jsonMap.entrySet() ) {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("\n");
			sb.append("\"id\": ").append(entry.getKey()).append(",").append("\n");
			sb.append("\"text\": \"").append(entry.getValue().get("text")).append("\",").append("\n");
			sb.append("\"answer\": \"").append(entry.getValue().get("answer")).append("\",").append("\n");
			sb.append("\"category\": {").append("\n");
			sb.append("     \"name\": \"").append(entry.getValue().get("category")).append("\" \n");
			sb.append("},").append("\n");
			sb.append("\"subcategory\": {").append("\n");
			sb.append("     \"name\": \"").append(entry.getValue().get("category")).append("\" \n");
			sb.append("}").append("\n");			
			sb.append("},").append("\n");		
			System.out.println(sb.toString());
			writer.write(sb.toString());
		}
	    StringBuilder sb2 = new StringBuilder();
	    sb2.append("]").append("\n");
	    sb2.append("}").append("\n");
	    sb2.append("}").append("\n");
	    writer.write(sb2.toString());
		writer.close();
	}
	
	public static void convertOneFileLoop() throws IOException {
		category = "Science ScienceBee";
		String directoryPath = new String("C:\\Temp\\t005\\Protobowl\\release\\packets\\NationalScienceBee\\2016-2017");
		Set<String> fileSetPackets = new HashSet<>();
		Set<String> fileSet = new HashSet<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath.toString()))) {
			for (Path path : stream) {
				if (!Files.isDirectory(path)) {
					fileSet.add(path.getFileName().toString());
				}
			}
		}
		for (String entry : fileSet) {
			if (entry.contains(".txt")) {
				fileSetPackets.add(entry);
			}
		}

		for (String entry : fileSet) {
			StringBuilder sb1 = new StringBuilder();
			sb1.append(directoryPath).append("\\").append(entry);
			System.out.println(sb1.toString());

			StringBuilder sb2 = new StringBuilder();
			sb2.append(directoryPath).append("\\").append(entry.replace(".txt", ".json"));
			System.out.println(sb2.toString());

			String file = new String(sb1.toString());
			processFile(file);
			// fillJsonMap2();
			fillJsonMapMultipleChoice();
			String ofile = new String(sb2.toString());
			writeJsonMapToFile(ofile);
		}
	}
	
	
	public static void convertallFiles(String selectedFolder) throws IOException {
		category="Science ScienceBee";
		
	    Set<String> fileSet = new HashSet<>();
	    Set<String> fileSetPackets = new HashSet<>();

	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(selectedFolder.toString()))) {
	        for (Path path : stream) {
	            if (!Files.isDirectory(path)) {
	                fileSet.add(path.getFileName()
	                    .toString());
	            }
	        }
	    }
		for (String entry : fileSet) {
			if (entry.contains(".txt")) {
				fileSetPackets.add(entry);
			}
		}
		
		for (String entry : fileSetPackets) {
			StringBuilder source = new StringBuilder();
			source.append(selectedFolder).append("\\").append(entry);
			StringBuilder dest = new StringBuilder();
			dest.append(selectedFolder).append("\\").append(entry.replaceAll(".txt", ".json"));
			processFile(source.toString());
			fillJsonMap1();
			writeJsonMapToFile(dest.toString());
		}
	}

	
	public static void processFile() {
		StringBuilder sb1 = new StringBuilder();
		sb1.append(path)
		  .append("\\")
		  .append(name);
		System.out.println(sb1.toString());	
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(sb1.toString()));
			String line = reader.readLine();
			StringBuilder question = new StringBuilder();
			StringBuilder answer = new StringBuilder();
			while (line != null) {
				line = reader.readLine();
				if (!line.startsWith("ANSWER")) {
					question.append(line);
				}
				if (line.startsWith("ANSWER")) {
					answer = new StringBuilder(line);
					System.out.println(question);
					System.out.println(line);
					question = new StringBuilder();
					answer = new StringBuilder();
				}
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void convertOneFile() throws IOException {
		StringBuilder sb1 = new StringBuilder();
		sb1.append(path)
		  .append("\\")
		  .append(name);
		System.out.println(sb1.toString());
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append(path)
		  .append("\\")
		  .append(name.replace(".txt", ".json"));
		System.out.println(sb2.toString());
	
		String file = new String(sb1.toString());
		processFile(file);
		//fillJsonMap2();
		fillJsonMapMultipleChoice();
		String ofile = new String(sb2.toString());
		writeJsonMapToFile(ofile);
	}
	
	public static void main(String[] args) throws IOException {
		//convertallFiles("C:\\Temp\\t005\\Protobowl\\release\\packets\\NationalScienceBee\\2020-2021");
		path = new String("C:\\Temp\\t005\\Protobowl\\release\\packets\\NationalScienceBee\\2018-2019");
		name = "2019-National-Science-Bee-Regional-Finals-Set-2-Round-3.txt";		
		category="Science ScienceBee";
//		processFile();
		convertOneFile();
	}
}
