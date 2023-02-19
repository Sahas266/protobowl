package mainmodule;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.simple.JSONObject;

public class ScoreKeeperManager {
	public static String historyKeeperFileName = new String("historyKeeper.json");
	public static String historyKeeperFileFullPath = new String();
	public static FileWriter file;

	public static void initialize() {
		String appPath = Paths.get(".").toAbsolutePath().normalize().toString();
		historyKeeperFileFullPath = appPath.concat("\\").concat(historyKeeperFileName);
	}

	public static String currentDateTime() {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String strDate = dateFormat.format(date);
		return strDate;
	}

	public static void recordScore(Integer correct, Integer wrong, Integer skipped, String packet) throws IOException {
		JSONObject json = new JSONObject();
		try {
			json.put("correct", correct.toString());
			json.put("wrong", wrong.toString());
			json.put("skipped", skipped.toString());
			json.put("packet", packet.toString());
			json.put("datetime", currentDateTime());

			System.out.println(historyKeeperFileFullPath);

			file = new FileWriter(historyKeeperFileFullPath, true);
			file.write(json.toJSONString());
			file.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		initialize();
		recordScore(1, 2, 3, "packet1");

	}

}
