public class Command {

	public final static String ENCODING = "UTF-8";
	private String command_;

	private Command(String msg) {
		this.command_ = msg;
	}

	public static Command ASKTXT = new Command("ASKTXT");
	public static Command ASKCHOICES = new Command("ASKCHOICES");
	public static Command SYSMSG = new Command("SYSMSG");
	public static Command GAMEMSG = new Command("GAMEMSG");
	public static Command USRMSG = new Command("USRMSG");
	public static Command NICK = new Command("NICK");
	public static Command JOIN = new Command("JOIN");
	public static Command MSG = new Command("MSG");
	public static Command LISTGAMES = new Command("LISTGAMES");
	public static Command STARTGAME = new Command("STARTGAME");
	public static Command TXTRESP = new Command("TXTRESP");
	public static Command SELECTRESP = new Command("SELECTRESP");
	public static Command QUIT = new Command("QUIT");

	public static Command valueOf(String v) {
		v = v.trim();
		if (v.equals("ASKTXT"))
			return Command.ASKTXT;
		else if (v.contains("GAMEMSG"))
			return Command.GAMEMSG;
		else if (v.contains("MSG"))
			return Command.MSG;
		else if (v.contains("SYSMSG"))
			return Command.SYSMSG;
		else if (v.contains("ASKCHOICES"))
			return Command.ASKCHOICES;
		else if (v.contains("USRMSG"))
			return Command.USRMSG;
		else if (v.contains("NICK"))
			return Command.NICK;
		else if (v.contains("JOIN"))
			return Command.JOIN;
		else if (v.contains("LISTGAMES"))
			return Command.LISTGAMES;

		else if (v.contains("STARTGAME"))
			return Command.STARTGAME;

		else if (v.contains("TXTRESP"))
			return Command.TXTRESP;

		else if (v.contains("SELECTRESP"))
			return Command.SELECTRESP;

		else if (v.contains("QUIT"))
			return Command.QUIT;

		return null;

	}

	public String toString() {
		return this.command_;
	}
}
