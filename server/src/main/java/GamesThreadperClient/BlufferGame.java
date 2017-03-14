package GamesThreadperClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import threadperClientprotocol.ProtocolCallback;
/**
 * BlufferGame Class: This class manuplate the game.. and holds all the
 * quistions And Players responses and calculate the score for every player
 * 
 * @author ibrahemi
 *
 */
public class BlufferGame extends Game {
	ConcurrentHashMap<String, String> QuestionAndRealResult;
	ConcurrentHashMap<String, Integer> PlayersAndPoints;
	LinkedList<String> Players;
	final String filePath = "/users/studs/bsc/2015/ibrahemi/Bluffer.json";
	ConcurrentHashMap<String, ProtocolCallback> PlayersProtocolCallback;
	LinkedList<String> Question;
	LinkedList<String> Answers;
	boolean isEnd = false;
	int countPlayerTxtresp;
	int countPlayerSelectresp;
	ConcurrentHashMap<String, String> TextResponse;
	ConcurrentHashMap<String, String> Choices; // the choice and the Answer
	ConcurrentHashMap<String, String> SelecetedChoicesByPLayers; // the player
																	// and the
																	// choice
																	// the
																	// player
																	// selected

	/**
	 * Constructor
	 * 
	 * @param gameName
	 *            - the name of the Game
	 */
	public BlufferGame(String gameName) {
		super(gameName);
		QuestionAndRealResult = new ConcurrentHashMap<String, String>();
		PlayersAndPoints = new ConcurrentHashMap<String, Integer>();
		Question = new LinkedList<String>();
		Answers = new LinkedList<String>();
		countPlayerTxtresp = 0;
		countPlayerSelectresp = 0;
		TextResponse = new ConcurrentHashMap<String, String>();
		Choices = new ConcurrentHashMap<String, String>();
		SelecetedChoicesByPLayers = new ConcurrentHashMap<String, String>();
	}

	/**
	 * this function receive from the manager the players once the Player
	 * STARTED
	 * 
	 * @param Players
	 *            - all the players
	 * @param PlayersProtocolCallback
	 *            - all the CallBack for players
	 */
	public void addPlayers(LinkedList<String> Players,
			ConcurrentHashMap<String, ProtocolCallback> PlayersProtocolCallback) {
		this.Players = Players;
		for (int i = 0; i < Players.size(); i++) {
			PlayersAndPoints.put(Players.get(i), new Integer(0));
		}
		this.PlayersProtocolCallback = PlayersProtocolCallback;

	}

	/**
	 * Reading quitsion and results from json file {@link JsonReader}
	 */
	public void intiliaze() {

		JsonReader json = new JsonReader(filePath);
		this.QuestionAndRealResult = json.RandomQuestions();

	}

	/**
	 * Starts the game and calls for SendQuestionToPlayers function to send
	 * Quistion to the players
	 * 
	 */
	public void startGame() {

		this.intiliaze();

		for (Map.Entry<String, String> entry : QuestionAndRealResult.entrySet()) {
			Question.add(entry.getKey());
			Answers.add(entry.getValue());

		}
		SendQuestionToPlayers();

	}
	/**
	 * This Function sends Quistion to all Players
	 */
	public void SendQuestionToPlayers() {
		for (int i = 0; i < Players.size(); i++) {
			ProtocolCallback callPlayer = PlayersProtocolCallback.get(Players.get(i));
			try {
				callPlayer.sendMessage("ASKTXT " + Question.getLast());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	/**
	 * Sends to all players the AskChoices
	 * 
	 * @param AskChoices
	 */
	public void SendASKCHOICESToPlayers(String AskChoices) {
		for (int i = 0; i < Players.size(); i++) {
			ProtocolCallback callPlayer = PlayersProtocolCallback.get(Players.get(i));
			try {
				callPlayer.sendMessage(AskChoices);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * Save the Response for this player and once All players had been responsed
	 * send for all players ASKCHOICES
	 * 
	 * @param response
	 * @param Player
	 */
	public void Txtresp(String response, String Player) {

		TextResponse.put(response, Player);
		countPlayerTxtresp++;

		// now we know that all the players submit the TXTRESP and then we send
		// the ASKCHOICES
		if (countPlayerTxtresp == Players.size()) {
			// LinkedList<String> AllPlayersResponses = TextResponse
			List<String> answers = new ArrayList<String>(TextResponse.keySet());
			String RealAnswer = Answers.getLast();

			answers.add(RealAnswer); // adds the Real answer to the
										// LPlayersAndPointsinkedList
			String ASKCHOICES = "";
			int sizeOfAnswers = answers.size();
			int c = 0;
			// here we randomly select ASKCHOICES WE WANT TO SEND FOR ALL THE
			// PLAYERS

			Random random = new Random();
			while (!answers.isEmpty()) {
				int randomPosition = random.nextInt(answers.size());
				String ans = answers.remove(randomPosition);
				ASKCHOICES += c + "." + ans + " ";
				Choices.put(c + "", ans);
				c++;

			}

			SendASKCHOICESToPlayers("ASKCHOICES " + ASKCHOICES);

		}

	}

	/**
	 * calculating and manuplating all thing relating to the game once all the
	 * players have been choosed then sends the results for every one and once
	 * the game is end then sends the summary.
	 * 
	 * @param Player
	 * @param Choice
	 */
	public void Selectresp(String Choice, String player) {
		countPlayerSelectresp++;
		// check correctned of Choice
		SelecetedChoicesByPLayers.put(player, Choice);
		// TextResponse
		if (countPlayerSelectresp == Players.size()) {
			ConcurrentHashMap<String, Integer> ThisTurnQuistionPoints = new ConcurrentHashMap<String, Integer>();// this
																													// hashmap
																													// allows
																													// to
																													// us
																													// to
																													// know
																													// how
																													// much
																													// points
																													// every
																													// player
			// receive
			// in
			// this
			// turn
			// of
			// question
			// filling the hashmap
			for (int i = 0; i < Players.size(); i++) {
				ThisTurnQuistionPoints.put(Players.get(i), new Integer(0));
			}

			for (int i = 0; i < Players.size(); i++) {
				String choiceThePlayerSelected = SelecetedChoicesByPLayers.get(Players.get(i));
				String TheAnswerThePlayerSelected = Choices.get(choiceThePlayerSelected);
				if (TheAnswerThePlayerSelected.equals(Answers.getLast())) {
					System.out.println(Players.get(i));

					PlayersAndPoints.replace(Players.get(i), PlayersAndPoints.get(Players.get(i)) + 10); // add
																											// 10
																											// points
																											// to
																											// the
																											// player
																											// Choices
																											// The
																											// RealAnswer

					ThisTurnQuistionPoints.replace(Players.get(i),
							new Integer(ThisTurnQuistionPoints.get(Players.get(i)).intValue() + 10));

				} else {

					if (TextResponse.containsKey(TheAnswerThePlayerSelected)) {
						String ThePlayerReceived5Points = TextResponse.get(TheAnswerThePlayerSelected);
						PlayersAndPoints.replace(ThePlayerReceived5Points,
								new Integer(PlayersAndPoints.get(ThePlayerReceived5Points).intValue() + 5));
						ThisTurnQuistionPoints.replace(ThePlayerReceived5Points,
								new Integer(ThisTurnQuistionPoints.get(ThePlayerReceived5Points).intValue() + 5));

					}
				}

			}

			for (Map.Entry<String, Integer> entry : ThisTurnQuistionPoints.entrySet()) {
				String response = "";
				String PlayerName = entry.getKey();
				ProtocolCallback CallBackPlayer = PlayersProtocolCallback.get(PlayerName);
				String ThePointsPlayerGetsInThisRound = entry.getValue() + "";
				String choiceThePlayerSelected = SelecetedChoicesByPLayers.get(PlayerName);
				String TheAnswerThePlayerSelected = Choices.get(choiceThePlayerSelected);
				if (TheAnswerThePlayerSelected.equals(Answers.getLast())) {
					response = "GAMEMSG CORRECT! +";

				} else {
					response = "GAMEMSG WRONG! +";

				}
				response += entry.getValue() + "pts";
				try {
					CallBackPlayer.sendMessage(response);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			Answers.removeLast();
			Question.removeLast();

			if (!Question.isEmpty()) {
				TextResponse = new ConcurrentHashMap<String, String>();
				Choices = new ConcurrentHashMap<String, String>();
				SelecetedChoicesByPLayers = new ConcurrentHashMap<String, String>();

				countPlayerSelectresp = 0;
				countPlayerTxtresp = 0;

				SendQuestionToPlayers();
			} else {

				Summary();
			}

		}

	}

	/**
	 * @return true if the game ended else false;
	 */
	public boolean isEndGame() {
		return isEnd;
	}
	/**
	 * sends for all players the summary of the game
	 */
	public void Summary() {
		isEnd = true;

		String response = "";
		response = "GAMSEMSG Summary: ";
		int counter = 0; // to check the last one
		for (Map.Entry<String, Integer> entry : PlayersAndPoints.entrySet()) {
			String PlayerName = entry.getKey();
			String PointsOwn = entry.getValue() + "";
			counter++;
			if (counter == PlayersAndPoints.size()) {
				response += PlayerName + ": " + PointsOwn;
			} else {
				response += PlayerName + ": " + PointsOwn + ", ";

			}

		}
		for (int i = 0; i < Players.size(); i++) {
			try {
				PlayersProtocolCallback.get(Players.get(i)).sendMessage(response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * @return if the Choice is legal returns true else false
	 */
	public boolean HaveChoice(int Choice) {

		if ((0 <= Choice) && Choice <= Players.size()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * remove all things related to this player from the game
	 * @param namePlayer
	 */
	public void Quit(String namePlayer) {

		ProtocolCallback callPlayer = PlayersProtocolCallback.get(namePlayer);
		PlayersAndPoints.remove(namePlayer);
		PlayersProtocolCallback.remove(namePlayer);
		if (TextResponse.containsKey(namePlayer)) {
			countPlayerTxtresp--;
			TextResponse.remove(namePlayer);
			if (SelecetedChoicesByPLayers.containsKey(namePlayer)) {
				SelecetedChoicesByPLayers.remove(namePlayer);
				countPlayerSelectresp--;

			}
		}
        if(Players.size() == 1){
        	isEnd= true;
        System.out.println(isEnd);
        }
        	
		Players.remove(namePlayer);

	}

}
