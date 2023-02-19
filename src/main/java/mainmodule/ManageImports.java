package mainmodule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ManageImports {

	private static final String COMMA_DELIMITER = ",";
	private static File csvFile = null;
	private static Map<String, List<String>> map;
	private List<List<String>> records;

	private Map<String, String> pair = new HashMap<String, String>();
	public JSONArray tossups;
	public JSONObject question;
	public Set<String> categoriesSet;
	public Set<String> subCategoriesSet;
	public Set<String> subCategoriesWithCountSet;
	public Map<String, JSONArray> categoryTossupsMap = new HashMap<String, JSONArray>();
	public String currentPacket = new String();

	public void initialize() {
		categoriesSet = new HashSet<String>();
		subCategoriesSet = new HashSet<String>();
		subCategoriesWithCountSet = new HashSet<String>();
		categoryTossupsMap = new HashMap<String, JSONArray>();
		currentPacket = new String();
	}

	public void importQuestions01(File jsonFile) throws SQLException, FileNotFoundException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(jsonFile));
			JSONObject jsonObject = (JSONObject) obj;
			// loop array
			tossups = (JSONArray) ((JSONObject) jsonObject.get("data")).get("tossups");

			for (int i = 0; i < tossups.size(); i++) {
				JSONObject obj1 = (JSONObject) tossups.get(i);

				System.out.println(obj1.get("id"));
				System.out.println("\n");

				System.out.println(obj1.get("formatted_text"));
				System.out.println("\n");

				System.out.println(obj1.get("answer"));
				System.out.println("\n");

				JSONObject obj2 = (JSONObject) obj1.get("tournament");
				System.out.println(obj2.get("difficulty"));
				System.out.println("\n");

				JSONObject obj3 = (JSONObject) obj1.get("category");
				System.out.println(obj3.get("name"));
				System.out.println("\n");

				if (i > 1)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void importQuestions(File jsonFile) throws SQLException, FileNotFoundException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(jsonFile));
			JSONObject jsonObject = (JSONObject) obj;
			tossups = (JSONArray) ((JSONObject) jsonObject.get("data")).get("tossups");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void importQuestionsAppend(File jsonFile) throws SQLException, FileNotFoundException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		JSONArray tossupsCurrent = new JSONArray();
		try {
			Object obj = parser.parse(new FileReader(jsonFile));
			JSONObject jsonObject = (JSONObject) obj;
			if (tossups == null) {
				tossups = (JSONArray) ((JSONObject) jsonObject.get("data")).get("tossups");
			} else {
				tossupsCurrent = (JSONArray) ((JSONObject) jsonObject.get("data")).get("tossups");
			}
			for (Object term : tossupsCurrent) {
				JSONObject temp = (JSONObject) term;
				tossups.add(temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setCurrentPacket(File jsonFile) {
		currentPacket = jsonFile.getName();
	}

	public void showQuestions(String category, String difficulty) {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		for (int i = 0; i < tossups.size(); i++) {
			JSONObject question = (JSONObject) tossups.get(i);

			JSONObject categoryObj = (JSONObject) question.get("category");
//        	String difficultyObj = (String) ((JSONObject)question.get("tournament")).get("difficulty");

			if (category.equalsIgnoreCase((String) categoryObj.get("name"))) {
				System.out.println(question.get("id"));
				System.out.println("\n");

				System.out.println(question.get("formatted_text"));
				System.out.println("\n");

				System.out.println(question.get("answer"));
				System.out.println("\n");
			}
		}
	}

	public JSONObject getQuestion(String category, String difficulty) {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		for (int i = 0; i < tossups.size(); i++) {
			JSONObject question = (JSONObject) tossups.get(i);

			JSONObject categoryObj = (JSONObject) question.get("category");
			JSONObject difficultyObj = (JSONObject) question.get("tournament");

			if (category.equalsIgnoreCase((String) categoryObj.get("name"))) {
				this.question = question;
			}
		}
		return question;
	}

	public JSONObject getRandomQuestion(String category, String difficulty) {
		Random r = new Random();
		int randomInt = r.nextInt(tossups.size()) + 1;
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		JSONObject question = (JSONObject) tossups.get(randomInt);
		return question;
	}

	public JSONObject getRandomQuestionFromCategory(String category) {
		JSONArray catTossups = categoryTossupsMap.get(category);
		Random r = new Random();
		int randomInt = r.nextInt(catTossups.size()) + 1;
		ObjectMapper mapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		JSONObject question = (JSONObject) catTossups.get(randomInt - 1);
		return question;
	}

	public JSONObject getSpecificQuestionFromCategory(String category, Integer questionNumber) {
		JSONArray catTossups = categoryTossupsMap.get(category);
		JSONObject question = new JSONObject();
		for (Object entry : catTossups) {
			question = (JSONObject) entry;
			if (question.get("id").toString().equalsIgnoreCase(questionNumber.toString())) {
				break;
			}
		}
		System.out.println();
		return question;
	}

	public void findCategories() {
		for (int i = 0; i < tossups.size(); i++) {
			JSONObject question = (JSONObject) tossups.get(i);
			JSONObject categoryObj = (JSONObject) question.get("category");
			JSONObject subCategoryObj = (JSONObject) question.get("subcategory");
			if ((categoryObj != null) && (subCategoryObj != null)) {
				String cat = categoryObj.get("name").toString();
				if (!categoriesSet.contains(cat)) {
					categoriesSet.add(cat);
				}
				if (subCategoryObj.containsKey("name")) {
					String subCat = subCategoryObj.get("name").toString();
					if (!subCategoriesSet.contains(subCat)) {
						subCategoriesSet.add(subCat);
					}
				}

			}
		}
		System.out.println();
	}

	public void findCategoriesWithCount() {
		for (Map.Entry<String, JSONArray> entry : categoryTossupsMap.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(entry.getKey());
			sb.append(":");
			Integer count = entry.getValue().size();
			count++;
			sb.append(count);
			subCategoriesWithCountSet.add(sb.toString());
		}
		System.out.println();
	}

	public void fillCategories() {
		for (int i = 0; i < tossups.size(); i++) {
			JSONObject question = (JSONObject) tossups.get(i);
			JSONObject categoryObj = (JSONObject) question.get("category");
			JSONObject subCategoryObj = (JSONObject) question.get("subcategory");

			if ((categoryObj != null)) {
				String cat = categoryObj.get("name").toString();
				if (!categoryTossupsMap.containsKey(cat)) {
					JSONArray tossups = new JSONArray();
					categoryTossupsMap.put(cat, tossups);
				} else {
					JSONArray tossups = categoryTossupsMap.get(cat);
					tossups.add(question);
					categoryTossupsMap.put(cat, tossups);
				}

			}
		}
	}

	public int getCountForACategory(String category) {
		JSONArray entry = categoryTossupsMap.get(category);
		return entry.size();
	}

	public int getTotalQuestionsCount() {
		return tossups.size();
	}

	public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
		ManageImports ia = new ManageImports();
		ia.importQuestions(new File("C:\\temp\\t005\\ms_science_history.json"));
		ia.findCategories();
		ia.fillCategories();
		ia.findCategoriesWithCount();
//		for (int i=0; i<10; i++) {
//			System.out.println(ia.getRandomQuestionFromCategory("Science"));
//		}
		// ia.showQuestions("Science", "Middle School");
		// JSONObject question = ia.getRandomQuestion("Science", "Middle School");
		// System.out.println(question);
//		System.out.println(question.get("formatted_text"));
//		System.out.println();
//		System.out.println(question.get("answer"));

	}

}