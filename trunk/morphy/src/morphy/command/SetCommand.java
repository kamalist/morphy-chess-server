package morphy.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import morphy.user.UserInfoList;
import morphy.user.UserSession;

public class SetCommand extends AbstractCommand {
	public SetCommand() {
		super("Set");
	}

	public void process(String arguments, UserSession userSession) {
		final int pos = arguments.indexOf(" ");
		if (pos == -1) { userSession.send(getContext().getUsage()); return; }
		
		String setWhat = arguments.substring(0,pos).trim();
		String message = arguments.substring(pos);
		
		// finger notes
		if (StringUtils.isNumeric(setWhat)) {
			int val = Integer.parseInt(setWhat);
			
			if (val >= 1 && val <= UserInfoList.MAX_NOTES) {
				List<String> notes = userSession.getUser().getUserInfoLists().get(UserInfoList.notes);
				if (notes == null) {
					notes = new ArrayList<String>(UserInfoList.MAX_NOTES);
					userSession.getUser().getUserInfoLists().put(UserInfoList.notes,notes);
				}
				//notes.add(val,message);
				//notes.set(val,message);
				//notes.add(message);
				userSession.send("Plan line " + val + " changed to '" + message + "'.");
			}
		}
	}
}
