package mainmodule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

public class ModeratorManager {
	public static int questionsCounter = 0;
	public static int correctAnswersCounter = 0;
	public static int wrongAnswersCounter = 0;
	public static int skippedCounter = 0;
	public static ManageImports ma;
	public static JFrame frame;

	public static void initialize() {
		ma = MainMenu.ma;
		frame = MainMenu.frame;
	}

	public static String promptUserForCategory() {
		Object[] category = ma.subCategoriesWithCountSet.toArray();
		String c = (String) JOptionPane.showInputDialog(frame, "Choose a category : \n", "Customized Dialog",
				JOptionPane.PLAIN_MESSAGE, null, category, "5");
		c = c.split(":")[0];
		return c;
	}

	public static int promptUserForQusetionsCount() {
		int requestedCount;
		Object[] challengeCount = { "5", "10", "15", "20", "100", "all-ordered", "all-random" };
		String s = (String) JOptionPane.showInputDialog(frame, "How many questions do you like to take : \n",
				"Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, challengeCount, "5");
		if (s.equalsIgnoreCase("all-ordered")) {
			requestedCount = -1;
		} else if (s.equalsIgnoreCase("all-random")) {
			requestedCount = -2;
		} else {
			requestedCount = Integer.parseInt(s);
		}
		return requestedCount;

	}

	public static int promptQuestion(String category, String questionString, int total, int requestedCount,
			int totalCountAvailable, Boolean multipart) {
		StringBuilder sb = new StringBuilder();
		String[] tokens = questionString.split(" ");

		int wordCount = 0;
		int que = -1;
		int tokenCount = 0;
		for (String token : tokens) {
			if (token.equalsIgnoreCase("NEWLINE")) {
				sb.append(" ");
				sb.append("\n");
				wordCount = 0;
			} else {
				sb.append(token).append(" ");
			}
			wordCount++;
			if (wordCount == 10) {
				wordCount = 0;
				sb.append("\n");
			}
		}

		Object[] questOptionsWithClue = { "Reveal Answer", "Last Clue" };
		Object[] questOptionsWithoutClue = { "Reveal Answer" };
		StringBuilder title = new StringBuilder();
		title.append(category).append("    ").append("Question : ").append(total).append("/").append(requestedCount)
				.append("    ").append("Available : ").append(totalCountAvailable).append("    ").append("Packet : ")
				.append(ma.currentPacket);

		if (multipart) {
			que = JOptionPane.showOptionDialog(frame, sb.toString(), title.toString(), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, questOptionsWithClue, questOptionsWithClue[0]);
		} else {
			que = JOptionPane.showOptionDialog(frame, sb.toString(), title.toString(), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, questOptionsWithoutClue, questOptionsWithoutClue[0]);
		}

		return que;
	}

	public static void askQuestionsByCategory(String category) throws FileNotFoundException, SQLException, IOException {
		questionsCounter = 0;
		List<Integer> correctAnswers = new ArrayList<Integer>();
		List<Integer> wrongAnswers = new ArrayList<Integer>();
		List<Integer> skippedAnswers = new ArrayList<Integer>();
		correctAnswersCounter = 0;
		wrongAnswersCounter = 0;
		skippedCounter = 0;
		Boolean allOrdered = Boolean.FALSE;
		Boolean allRandom = Boolean.FALSE;

		if (category.equalsIgnoreCase("prompt"))
			category = promptUserForCategory();

		int requestedCount = promptUserForQusetionsCount();
		if (requestedCount == -1) {
			requestedCount = ma.getCountForACategory(category);
			allOrdered = Boolean.TRUE;
		}
		if (requestedCount == -2) {
			requestedCount = ma.getCountForACategory(category);
			allRandom = Boolean.TRUE;
		}

		int totalCountAvailable = ma.getTotalQuestionsCount();
		Set<String> doneSet = new HashSet<String>();
		int total = 1;
		// for (total = 1; total <= requestedCount; total++) {
		String clueSeparator = new String("For the point");
		while (total <= requestedCount) {
			// JSONObject question = ma.getRandomQuestion("Science", "Middle School");
			JSONObject question = ma.getRandomQuestionFromCategory(category);
			if (doneSet.contains(question.get("id").toString())) {
				continue;
			} else {
				doneSet.add(question.get("id").toString());
			}

			String questionString = (String) question.get("text");

			int que = -1;
			if (questionString.contains(clueSeparator)) {
				String[] tokens = questionString.split(clueSeparator);
				StringBuilder sbquestion = new StringBuilder();
				for (String token : tokens) {
					sbquestion.append(token);
					if (sbquestion.toString().contains(clueSeparator)) {
						que = promptQuestion(category, sbquestion.toString(), total, requestedCount,
								totalCountAvailable, Boolean.FALSE);
					} else {
						que = promptQuestion(category, sbquestion.toString(), total, requestedCount,
								totalCountAvailable, Boolean.TRUE);
					}
					sbquestion.append(clueSeparator);
				}
			} else {
				promptQuestion(category, questionString, total, requestedCount, totalCountAvailable, Boolean.FALSE);
			}

			questionsCounter++;

			Object[] answerOptions = { "Correct", "Wrong", "Skipped", "Done" };
			int ans = JOptionPane.showOptionDialog(frame, (String) question.get("answer"), "Answer",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, answerOptions,
					answerOptions[0]);

			if (ans == 0) {
				correctAnswersCounter++;
				correctAnswers.add(Integer.parseInt(question.get("id").toString()));
			}
			if (ans == 1) {
				wrongAnswersCounter++;
				wrongAnswers.add(Integer.parseInt(question.get("id").toString()));
			}
			if (ans == 2) {
				skippedCounter++;
				skippedAnswers.add(Integer.parseInt(question.get("id").toString()));
			}
			if (ans == 3) {
				break;
			}
			total++;
		}

		if (total < requestedCount) {
			skippedCounter = skippedCounter + (requestedCount - total + 1);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Score Check :").append("\n").append(" Correct : ").append(correctAnswersCounter).append("\n")
				.append(" Wrong   : ").append(wrongAnswersCounter).append("\n").append(" Skipped : ")
				.append(skippedCounter).append("\n");
		Object[] scoreCheckOptions = { "Close", "ReTry-WrongOnes" };
		int retake = JOptionPane.showOptionDialog(frame, sb.toString(), "Score Check", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, scoreCheckOptions, scoreCheckOptions[0]);

		List<Integer> reattemptList = new ArrayList<Integer>();
		reattemptList.addAll(wrongAnswers);
		reattemptList.addAll(skippedAnswers);
		if (retake == 1) {
			askSelectedQuestionsByCategory(category, reattemptList);
		}
	}

	public static void askSelectedQuestionsByCategory(String category, List<Integer> list)
			throws FileNotFoundException, SQLException, IOException {
		questionsCounter = 0;
		List<Integer> correctAnswers = new ArrayList<Integer>();
		List<Integer> wrongAnswers = new ArrayList<Integer>();
		List<Integer> skippedAnswers = new ArrayList<Integer>();
		correctAnswersCounter = 0;
		wrongAnswersCounter = 0;
		skippedCounter = 0;
		int requestedCount = list.size();
		int totalCountAvailable = ma.getCountForACategory(category);
		int currentCount = 1;

		for (Integer questionNumber : list) {
			currentCount++;
			JSONObject question = ma.getSpecificQuestionFromCategory(category, questionNumber);
			StringBuilder sb = new StringBuilder();
			String questionString = (String) question.get("text");
			String[] tokens = questionString.split(" ");
			int wordCount = 0;
			for (String token : tokens) {
				if (token.equalsIgnoreCase("NEWLINE")) {
					sb.append(" ");
					sb.append("\n");
					wordCount = 0;
				} else {
					sb.append(token).append(" ");
				}
				wordCount++;
				if (wordCount == 10) {
					wordCount = 0;
					sb.append("\n");
				}
			}

			Object[] questOptions = { "Reveal Answer" };
			StringBuilder title = new StringBuilder();
			title.append(category).append("    ").append("Question : ").append(currentCount).append("/")
					.append(requestedCount).append("    ").append("Available : ").append(totalCountAvailable)
					.append("    ").append("Packet : ").append(ma.currentPacket);

			int que = JOptionPane.showOptionDialog(frame, sb.toString(), title.toString(),
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, questOptions,
					questOptions[0]);
			questionsCounter++;

			Object[] answerOptions = { "Correct", "Wrong", "Skipped", "Done" };
			int ans = JOptionPane.showOptionDialog(frame, (String) question.get("answer"), "Answer",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, answerOptions,
					answerOptions[0]);

			if (ans == 0) {
				correctAnswersCounter++;
				correctAnswers.add(Integer.parseInt(question.get("id").toString()));
			}
			if (ans == 1) {
				wrongAnswersCounter++;
				wrongAnswers.add(Integer.parseInt(question.get("id").toString()));

			}
			if (ans == 2) {
				skippedCounter++;
				skippedAnswers.add(Integer.parseInt(question.get("id").toString()));
			}
			if (ans == 3) {
				break;
			}
		}

		if (currentCount < requestedCount) {
			skippedCounter = skippedCounter + (requestedCount - currentCount + 1);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Score Check :").append("\n").append(" Correct : ").append(correctAnswersCounter).append("\n")
				.append(" Wrong   : ").append(wrongAnswersCounter).append("\n").append(" Skipped : ")
				.append(skippedCounter).append("\n");
		Object[] scoreCheckOptions = { "Gold", "Silver", "Bronze" };
		int que = JOptionPane.showOptionDialog(frame, sb.toString(), "Score Check", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, scoreCheckOptions, scoreCheckOptions[0]);
	}

	@Deprecated
	public void askHistoryQuestions() throws FileNotFoundException, SQLException, IOException {
		questionsCounter = 0;
		correctAnswersCounter = 0;
		wrongAnswersCounter = 0;
		skippedCounter = 0;

		Object[] challengeCount = { "5", "10", "15", "20", "100" };
		String s = (String) JOptionPane.showInputDialog(frame, "How many questions do you like to take : \n",
				"Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, challengeCount, "5");

		int totalCountAvailable = ma.getTotalQuestionsCount();
		int requestedCount = Integer.parseInt(s);
		int total;

		for (total = 1; total <= requestedCount; total++) {
			// JSONObject question = ma.getRandomQuestion("Science", "Middle School");
			JSONObject question = ma.getRandomQuestionFromCategory("History");
			StringBuilder sb = new StringBuilder();
			String questionString = (String) question.get("text");
			String[] tokens = questionString.split(" ");
			int wordCount = 0;
			for (String token : tokens) {
				sb.append(token).append(" ");
				wordCount++;
				if (wordCount == 10) {
					wordCount = 0;
					sb.append("\n");
				}
			}

			Object[] questOptions = { "Reveal Answer" };
			StringBuilder title = new StringBuilder();
			title.append("History-MiddleSchool     ").append("Question : ").append(total).append("/")
					.append(requestedCount).append("    ").append("Available : ").append(totalCountAvailable);

			int que = JOptionPane.showOptionDialog(frame, sb.toString(), title.toString(),
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, questOptions,
					questOptions[0]);
			questionsCounter++;

			Object[] answerOptions = { "Correct", "Wrong", "Skipped", "Done" };
			int ans = JOptionPane.showOptionDialog(frame, (String) question.get("answer"), "Answer",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, answerOptions,
					answerOptions[0]);

			if (ans == 0) {
				correctAnswersCounter++;
			}
			if (ans == 1) {
				wrongAnswersCounter++;
			}
			if (ans == 2) {
				skippedCounter++;
			}
			if (ans == 3) {
				break;
			}
		}

		if (total < requestedCount) {
			skippedCounter = skippedCounter + (requestedCount - total);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Score Check :").append("\n").append(" Correct : ").append(correctAnswersCounter).append("\n")
				.append(" Wrong   : ").append(wrongAnswersCounter).append("\n").append(" Skipped : ")
				.append(skippedCounter).append("\n");
		Object[] scoreCheckOptions = { "Gold", "Silver", "Bronze" };
		int que = JOptionPane.showOptionDialog(frame, sb.toString(), "Score Check", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, scoreCheckOptions, scoreCheckOptions[0]);
		questionsCounter++;
	}

	@Deprecated
	public void askScienceQuestions() throws FileNotFoundException, SQLException, IOException {
		questionsCounter = 0;
		correctAnswersCounter = 0;
		wrongAnswersCounter = 0;
		skippedCounter = 0;

		Object[] challengeCount = { "5", "10", "15", "20", "100" };
		String s = (String) JOptionPane.showInputDialog(frame, "How many questions do you like to take : \n",
				"Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, challengeCount, "5");

		int totalCountAvailable = ma.getTotalQuestionsCount();
		int requestedCount = Integer.parseInt(s);
		int total;

		for (total = 1; total <= requestedCount; total++) {
			// JSONObject question = ma.getRandomQuestion("Science", "Middle School");
			JSONObject question = ma.getRandomQuestionFromCategory("Science");
			StringBuilder sb = new StringBuilder();
			String questionString = (String) question.get("text");
			String[] tokens = questionString.split(" ");
			int wordCount = 0;
			for (String token : tokens) {
				sb.append(token).append(" ");
				wordCount++;
				if (wordCount == 10) {
					wordCount = 0;
					sb.append("\n");
				}
			}

			Object[] questOptions = { "Reveal Answer" };
			StringBuilder title = new StringBuilder();
			title.append("Science     ").append("Question : ").append(total).append("/").append(requestedCount)
					.append("    ").append("Available : ").append(totalCountAvailable);

			int que = JOptionPane.showOptionDialog(frame, sb.toString(), title.toString(),
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, questOptions,
					questOptions[0]);
			questionsCounter++;

			Object[] answerOptions = { "Correct", "Wrong", "Skipped", "Done" };
			int ans = JOptionPane.showOptionDialog(frame, (String) question.get("answer"), "Answer",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, answerOptions,
					answerOptions[0]);

			if (ans == 0) {
				correctAnswersCounter++;
			}
			if (ans == 1) {
				wrongAnswersCounter++;
			}
			if (ans == 2) {
				skippedCounter++;
			}
			if (ans == 3) {
				break;
			}
		}

		if (total < requestedCount) {
			skippedCounter = skippedCounter + (requestedCount - total);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Score Check :").append("\n").append(" Correct : ").append(correctAnswersCounter).append("\n")
				.append(" Wrong   : ").append(wrongAnswersCounter).append("\n").append(" Skipped : ")
				.append(skippedCounter).append("\n");
		Object[] scoreCheckOptions = { "Gold", "Silver", "Bronze" };
		int que = JOptionPane.showOptionDialog(frame, sb.toString(), "Score Check", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, scoreCheckOptions, scoreCheckOptions[0]);
		questionsCounter++;
	}
}
