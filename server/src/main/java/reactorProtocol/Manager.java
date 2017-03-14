package reactorProtocol;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import GamesReactor.BlufferGame;
import GamesReactor.Game;
import Tokenizer.StringMessage;
import Tokenizer.ThTokinzer;

public class Manager implements AsyncServerProtocol {
	/// every room and its players and for every player its messages it have
	ConcurrentHashMap<String, LinkedList<String>> RoomsAndPlayers;
	ConcurrentHashMap<String, Game> RoomsAndGamesIntheRoom;
	ConcurrentHashMap<String, Integer> RoomActivesGames;
	ConcurrentHashMap<String, String> PlayerAndRoomThatJoin;
	ConcurrentHashMap<ProtocolCallback, String> socketAndNick;
	ConcurrentHashMap<String, ProtocolCallback> NickAndSocket;
	LinkedList<String> _allGamesOnTheServer;
	int flag = 0;
	// Game blufferGame = null;

	private Manager() {
		RoomsAndPlayers = new ConcurrentHashMap<String, LinkedList<String>>();
		RoomsAndGamesIntheRoom = new ConcurrentHashMap<String, Game>();
		PlayerAndRoomThatJoin = new ConcurrentHashMap<String, String>();
		socketAndNick = new ConcurrentHashMap<ProtocolCallback, String>();
		NickAndSocket = new ConcurrentHashMap<String, ProtocolCallback>();
		RoomActivesGames = new ConcurrentHashMap<String, Integer>();
		_allGamesOnTheServer = new LinkedList<String>();
		_allGamesOnTheServer.add("BLUFFER");

	}

	/** Don't let anyone else instantiate this class */
	private static class SingletonHolder {
		private static Manager instance = new Manager();
	}

	/**
	 * constructor -
	 * 
	 * @return return the instance of this singleton class (The Manager)
	 */
	public static Manager getInstance() {
		return SingletonHolder.instance;
	}
	/**
	 * Detetmine whether the given message is the termination message .
	 *
	 * @param msg
	 *            the message to examine
	 * @return true if the message is the termination message , false otherwise
	 */
	public boolean isEnd(String msg) {

		return false;
	}

	/**
	 * Is the protocol in a closing state ?. When a protocol is in a closing
	 * state , it ’s handler should write out all pending data , and close the
	 * connection .
	 *
	 * @return true if the protocol is in closing state .
	 */
	public boolean shouldClose() {
		// TODO Auto-generated method stub
		if (flag == 1) {
			flag = 0;
			return true;
		}
		return false;
	}

	/**
	 * Indicate to the protocol that the client disconnected .
	 */
	public void connectionTerminated() {
		// TODO Auto-generated method stub

	}

	/**
	 * Processes a message .
	 *
	 * @param msg
	 *            the message to process
	 * @param callback
	 *            an instance of ProtocolCallback unique to the connection from
	 *            which msg originated .
	 */
	public void processMessage(Object msg, ProtocolCallback callback) {
		String[] Splitarr= msgSplit((StringMessage) msg);
		String command =Splitarr[0];
		String paramater =Splitarr[1];

		System.out.println(command);
		System.out.println(paramater);

		if (command.equals("NICK")) {
			nickCommand(command, paramater, callback);
		}

		else if (command.equals("JOIN")) {
			joinCommand(command, paramater, callback);

		} else if (command.equals("MSG")) {
			msgCommand(command, paramater, callback);

		} 
		else
			if (command.equals("LISTGAMES")) {
			listGames(callback);
			
		} else if (command.equals("STARTGAME")) {
		         
			startGame(command , paramater , callback);
			
		}

		else if (command.equals("TXTRESP")) {

			txtResp(command , paramater , callback);

		} else if (command.equals("SELECTRESP")) {

			selecetResp(command , paramater , callback);
		}
		// we must fill
		else if (command.equals("QUIT")) {
			quitCommand(command , paramater , callback);
			
		}
		
		else {
			try {
				callback.sendMessage("SYSMSG " + command + paramater + " UNDEFINED");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	/**
	 * splitting the msg into command and paramater
	 * @param msg
	 * @return array of str that have command and paramater
	 */
	private String[] msgSplit(StringMessage msg){
		String commandAndParamter = msg.toString();
		StringBuilder commandBuild = new StringBuilder();
		StringBuilder paramaterBuild = new StringBuilder();
		boolean check = true;

		for (int i = 0; i < commandAndParamter.length(); i++) {

			if ((commandAndParamter.charAt(i) != ' ') && check) {

				commandBuild.append(commandAndParamter.charAt(i));
			} else {
				if (commandAndParamter.charAt(i) == ' ')
					check = false;
				else
					paramaterBuild.append(commandAndParamter.charAt(i));
			}
		}

		String command = commandBuild.toString();
		String paramater = paramaterBuild.toString();
		String[] str = new String[2];
		str[0] = command;
		str[1] = paramater;
        return str;
	}
	
	
	
	
	/**
	 * removes everything that related to this player
	 * @param command
	 * @param paramater
	 * @param callback
	 */
	private void quitCommand(String command, String paramater, ProtocolCallback callback) {
		String response = "";
		if (socketAndNick.containsKey(callback)) {
			String PlayerName = socketAndNick.get(callback);
			if (PlayerAndRoomThatJoin.containsKey(PlayerName)) {
				String RoomName = PlayerAndRoomThatJoin.get(PlayerName);
				if (RoomActivesGames.get(RoomName) == 1) {
					RoomsAndGamesIntheRoom.get(RoomName).Quit(PlayerName);
					if (RoomsAndGamesIntheRoom.get(RoomName).isEndGame())
						RoomActivesGames.put(RoomName, 0);
				}
  
			
				
				String roomThePlayerJoined = PlayerAndRoomThatJoin.get(PlayerName);
				PlayerAndRoomThatJoin.remove(PlayerName);
				RoomsAndPlayers.get(roomThePlayerJoined).remove(PlayerName);
				if(RoomsAndPlayers.get(RoomName).size()==0){
					
					RoomActivesGames.remove(RoomName);
					RoomsAndGamesIntheRoom.remove(RoomName);
					RoomsAndPlayers.remove(RoomName);
				}

			}
			socketAndNick.remove(callback);
			NickAndSocket.remove(PlayerName);
		


		}

		response = "SYSMSG QUIT ACCEPTED";
		try {
			callback.sendMessage(response);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		flag = 1;
		return;		
	}

	/**
	 * send the Selecet to the Game {@link Game}
	 * @param command
	 * @param paramater
	 * @param callback
	 */
	private void selecetResp(String command, String paramater, ProtocolCallback callback) {
		boolean check1 =true;
		if (!socketAndNick.containsKey(callback)) {
			try {
				callback.sendMessage("SYSMSG SELECTRESP REJECETED");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String PlayerName = socketAndNick.get(callback);
			if (!PlayerAndRoomThatJoin.containsKey(PlayerName)) {
				try {
					callback.sendMessage("SYSMSG SELECTRESP REJECETED");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String RoomName = PlayerAndRoomThatJoin.get(PlayerName);
				if (RoomActivesGames.get(RoomName) == 0) {
					try {
						callback.sendMessage("SYSMSG SELECTRESP REJECETED");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {

					// paramater = paramater.toLowerCase();

					String PName = socketAndNick.get(callback);
					String RName = PlayerAndRoomThatJoin.get(PlayerName);
					Game gm = RoomsAndGamesIntheRoom.get(RoomName);
					try {
						int choice = Integer.parseInt(paramater);
						check1 = gm.HaveChoice(choice);
					} catch (NumberFormatException e) {
						// Will Throw exception!
						check1 = false;
					}

					if (gm != null && check1) {

						try {
							callback.sendMessage("SYSMSG SELECTRESP ACCEPTED");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gm.Selectresp(paramater, socketAndNick.get(callback));

					} else {
						try {
							callback.sendMessage("SYSMSG SELECTRESP REJECTED");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		}
		
	}
 
	/**
	 * send the TXTRESP to the {@link Game}
	 * @param command
	 * @param paramater
	 * @param callback
	 */
	private void txtResp(String command, String paramater, ProtocolCallback callback) {
		if (!socketAndNick.containsKey(callback)) {
			try {
				callback.sendMessage("SYSMSG TXTRESP REJECETED");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String PlayerName = socketAndNick.get(callback);
			if (!PlayerAndRoomThatJoin.containsKey(PlayerName)) {
				try {
					callback.sendMessage("SYSMSG TXTRESP REJECETED");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String RoomName = PlayerAndRoomThatJoin.get(PlayerName);
				if (RoomActivesGames.get(RoomName) == 0) {
					try {
						callback.sendMessage("SYSMSG TXTRESP REJECETED");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						callback.sendMessage("SYSMSG TXTRESP ACCEPTED");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					paramater = paramater.toLowerCase();
					String PName = socketAndNick.get(callback);
					String RName = PlayerAndRoomThatJoin.get(PlayerName);
					Game gm = RoomsAndGamesIntheRoom.get(RoomName);
					if (gm != null)
						gm.Txtresp(paramater, socketAndNick.get(callback));
				}
			}
		}		
	}

	/**
	 * Starting the game
	 * @param command
	 * @param paramater
	 * @param callback
	 */
	private void startGame(String command, String paramater, ProtocolCallback callback) {
		String response="";
		String gameName = paramater;
		String response1 = "";
		String PlayerName = "";
		System.out.println(command + "+" + paramater);

		if (!socketAndNick.containsKey(callback)) // if the player not in
													// any room.
			response = "SYSMSG STARTGAME REJECETED";

		else {

			PlayerName = socketAndNick.get(callback);
			if (!PlayerAndRoomThatJoin.containsKey(PlayerName)) { // checking
																	// if
																	// the
																	// player
																	// had
																	// been
																	// joined
																	// a
																	// room
				response = "SYSMSG STARTGAME REJECETED";
			} else {
				System.out.println(gameName);

				if (_allGamesOnTheServer.contains(gameName)) {
					String RoomName = PlayerAndRoomThatJoin.get(PlayerName);
					if (RoomsAndGamesIntheRoom.get(RoomName).isEndGame())
						RoomActivesGames.put(RoomName, 0);
					if (RoomActivesGames.get(RoomName) == 0) {
						try {
							callback.sendMessage("SYSMSG STARTGAME ACCEPTED");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (gameName.equals("BLUFFER")) {
							Game Gm = null;

							Gm = new BlufferGame("BLUFFER");
							System.out.println(RoomsAndPlayers.get(RoomName));
							Gm.addPlayers(RoomsAndPlayers.get(RoomName), NickAndSocket);
							Gm.startGame();
							RoomActivesGames.put(RoomName, 1);
							RoomsAndGamesIntheRoom.replace(RoomName, Gm);

						} else {
							response = "SYSMSG STARTGAME UNDEFINED";
						}
					} else {
						response = "SYSMSG STARTGAME REJECETED";

					}

				} else {
					System.out.println(command + "+" + paramater);

					response = "SYSMSG STARTGAME UNDEFINED";
				}

				// json reader
			}
		}

		try {
			callback.sendMessage(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * sends the Listgames to the player
	 * @param callback
	 */
	private void listGames( ProtocolCallback callback) {
		String response="";
		for (int i = 0; i < _allGamesOnTheServer.size(); i++) {
			response = response + _allGamesOnTheServer.get(i) + " ";
		}
		try {
			callback.sendMessage("SYSMSG LISTGAMES ACCEPTED " + response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	private void msgCommand(String command, String paramater, ProtocolCallback callback) {
		String response = "";
		if (!socketAndNick.containsKey(callback)) // if the player not in
			// any room.
			response = "SYSMSG MSG REJECETED";
		else {
			if (!PlayerAndRoomThatJoin.containsKey(socketAndNick.get(callback))) { // checking
				// if
				// the
				// player
				// had
				// been
				// joined
				// a
				// room
				response = "SYSMSG MSG REJECETED";
			} else {
				response = "SYSMSG MSG ACCEPTED";
				String messageToSend = paramater;
				String playerSender = socketAndNick.get(callback);
				String RoomsThatThePlayerJoin = PlayerAndRoomThatJoin.get(playerSender);
				LinkedList<String> Players = RoomsAndPlayers.get(RoomsThatThePlayerJoin);
				for (int i = 0; i < Players.size(); i++) {
					if (!Players.get(i).equals(playerSender)) {
						String player_ = Players.get(i);
						String toSend = "";
						ProtocolCallback callb = NickAndSocket.get(player_);
						toSend = "USRMSG " + playerSender + " : " + messageToSend;
						try {
							callb.sendMessage(toSend);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}
		}

		try {
			callback.sendMessage(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void joinCommand(String command, String paramater, ProtocolCallback callback) {
		String response = "";
		if (!socketAndNick.containsKey(callback)) {
			response = "SYSMSG JOIN REJECTED";

		}

		else {
			String player1 = socketAndNick.get(callback);

			if (PlayerAndRoomThatJoin.containsKey(player1)) {

				response = "SYSMSG JOIN REJECTED";

			} else {
				String player = socketAndNick.get(callback);
				System.out.println(player);
				if (RoomsAndPlayers.containsKey(paramater)) {
					if (RoomActivesGames.get(paramater)== 0) { // checking
																// if the
																// room
																// availbale
																// and didnt
																// statted
																// yet
						RoomsAndPlayers.get(paramater).addLast(player);
						PlayerAndRoomThatJoin.put(player, paramater);
						response = "SYSMSG JOIN ACCEPTED";
					} else {
						System.out.println("FFFFFFF");

						response = "SYSMSG JOIN REJECTED";
					}
				} else {
					RoomsAndPlayers.put(paramater, new LinkedList<String>());
					RoomsAndPlayers.get(paramater).addLast(player);
					RoomActivesGames.put(paramater, new Integer(0));
					RoomsAndGamesIntheRoom.put(paramater, new BlufferGame("BLUFFER"));
					PlayerAndRoomThatJoin.put(player, paramater);
					response = "SYSMSG JOIN ACCEPTED";

				}
			}
		}
		try {
			callback.sendMessage(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void nickCommand(String command, String paramater, ProtocolCallback callback) {
		System.out.println(NickAndSocket.containsKey(paramater));
		System.out.println(socketAndNick.containsKey(callback));
		String response = "";
		if ((paramater.equals("")) | (NickAndSocket.containsKey(paramater) | socketAndNick.containsKey(callback))) {

			response = "SYSMSG NICK REJECTED";

		} else {
			socketAndNick.put(callback, paramater);
			NickAndSocket.put(paramater, callback);
			response = "SYSMSG NICK ACCEPTED";
		}
		try {
			callback.sendMessage(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isEnd(Object msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
