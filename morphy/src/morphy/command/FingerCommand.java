package morphy.command;

import java.util.ArrayList;
import java.util.List;

import morphy.service.UserService;
import morphy.user.PlayerTitle;
import morphy.user.UserInfoList;
import morphy.user.UserSession;
import morphy.utils.MorphyStringUtils;

public class FingerCommand extends AbstractCommand {

	public FingerCommand() {
		super("Finger");
	}
	
	public void process(String arguments, UserSession userSession) {
		int pos = arguments.indexOf(" ");
		if (arguments.equals("")) {
			process(userSession.getUser().getUserName(),userSession);
			return;
		}
		
		
		String user = arguments.substring(0,((pos == -1) ? arguments.length() : pos));
		if (pos != -1) {
		String flags = arguments.substring(pos);
			//finger [user] [/[b][s][l][w][B][S]] [r][n]
			if (flags.contains("r")) { } // don't show notes
			if (flags.contains("n")) { } // don't show ratings
		}
		
		StringBuilder str = new StringBuilder(200);
		UserSession query = UserService.getInstance().getUserSession(user);
		
		str.append("Finger of " + query.getUser().getUserName() + PlayerTitle.toString(query.getUser().getTitles()) + ":\n\n");
		
		long loggedInMillis = System.currentTimeMillis() - query.getLoginTime();
		long idleTimeMillis = query.getIdleTimeMillis();
		str.append("On for: "
				+ MorphyStringUtils.formatTime(loggedInMillis, !false)
				+ "\tIdle: "
				//+ MorphyStringUtils.formatTime(System.currentTimeMillis()
						//- query.getIdleTimeMillis(), true));
				+ ((idleTimeMillis == 0) ? (idleTimeMillis + " secs") : MorphyStringUtils.formatTime(idleTimeMillis, true)));
		str.append("\n");
		str.append(String.format("%15s %7s %7s %7s %7s %7s %7s","rating","RD","win","loss","draw","total","best") + "\n");
		
		// variants, ratings
		
		// total time online, etc		
		
		str.append("\n\n");
		str.append("Timeseal: On\n\n");
		List<String> notes = query.getUser().getUserInfoLists().get(UserInfoList.notes);
		if (notes == null) {
			notes = new ArrayList<String>(UserInfoList.MAX_NOTES);
			userSession.getUser().getUserInfoLists().put(UserInfoList.notes,notes);
		}
		for(int i=0;i< (notes.size()) ;i++) {
			str.append(format(i) + ": " + notes.get(i));
		}
		
		userSession.send(str.toString());
	}

	private String format(int x) {
		if (x < 10) 
		 return " " + x; 
		else 
		 return "" + x;
	}
}
