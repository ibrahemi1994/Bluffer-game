package GamesThreadperClient;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * JsonReader -This Object handles the reading and creating The game Questions
 * and Answers
 *
 */
public class JsonReader {
	ConcurrentHashMap<String, String> QuestionAndRealAnswer;
	ConcurrentHashMap<String, String> RealAnswerAndQuestion;
	ConcurrentHashMap<String, String> RandomThreeQuestionsAndAnswers;


	private JSONParser parser = new JSONParser();

	JSONObject jsonObject;

	/**
	 * Constructor
	 * 
	 * @param filePath
	 *            - where the json file placed
	 */
	public JsonReader(String filePath) {
		QuestionAndRealAnswer = new ConcurrentHashMap<>();
		RealAnswerAndQuestion = new ConcurrentHashMap<String, String>();
		RandomThreeQuestionsAndAnswers = new ConcurrentHashMap<String, String>();
		Object obj = null;

		try {
			obj = parser.parse(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.jsonObject = (JSONObject) obj;

		JSONArray arrQuestion = (JSONArray) jsonObject.get("questions");
		for (int i = 0; i < arrQuestion.size(); i++) {
			JSONObject jsonObj = (JSONObject) arrQuestion.get(i);
			QuestionAndRealAnswer.put(jsonObj.get("questionText").toString(), jsonObj.get("realAnswer").toString());
			RealAnswerAndQuestion.put(jsonObj.get("realAnswer").toString(), jsonObj.get("questionText").toString());

		}

		for (int i = 0; i < 3; i++) {
			Random random = new Random();
			List<String> keys = new ArrayList<String>(QuestionAndRealAnswer.keySet());
			String randomKey = keys.get(random.nextInt(keys.size()));
			String Answer = QuestionAndRealAnswer.get(randomKey);
			String Question = RealAnswerAndQuestion.get(Answer);
			RandomThreeQuestionsAndAnswers.put(Question, Answer);
			RealAnswerAndQuestion.remove(Answer);
			QuestionAndRealAnswer.remove(randomKey);


		}
		
	}

	public ConcurrentHashMap<String, String> RandomQuestions() {
		return RandomThreeQuestionsAndAnswers;
	}

}
