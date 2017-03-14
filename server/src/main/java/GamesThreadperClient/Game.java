package GamesThreadperClient;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import threadperClientprotocol.ProtocolCallback;

public abstract class Game {
	String gameName;

	public Game(String gameName) {
		this.gameName = gameName;
	}

	public abstract void intiliaze();

	public abstract void startGame();

	public abstract void Txtresp(String response, String player);

	public abstract void Selectresp(String Choice, String player);
    public abstract boolean HaveChoice(int Choice);
	public abstract void addPlayers(LinkedList<String> linkedList,
			ConcurrentHashMap<String, ProtocolCallback> CallPlayers);
	public abstract void Quit(String name) ;
	
	public abstract boolean isEndGame();
}
