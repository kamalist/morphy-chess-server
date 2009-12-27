package morphy.utils.john;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import morphy.user.PersonalList;

public class HelpFileGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		PersonalList[] list = PersonalList.values();

		for (int i = 0; i < list.length; i++) {
			String helpFileName = "Add"
					+ (("" + list[i].name().charAt(0)).toUpperCase() + list[i]
							.name().substring(1)) + ".txt";

			String param = "username";
			StringBuilder str = new StringBuilder("Name: +" + list[i].name());
			str.append("\r\nUsage: +" + list[i].name() + " " + param);
			str.append("\r\nAliases: N/A");
			str.append("\r\nSee Also: N/A");
			str.append("\r\nLastModifiedBy: " + "johnthegreat");
			str.append("\r\nLastModifiedDate: " + "2009/12/25");
			str.append("\r\nUserLevel: " + "Guest");
			str.append("\r\nHelp: ");
			str.append("Adds the specified " + param + " to the "
					+ list[i].name() + " list.");

			FileWriter writer = new FileWriter(new File(
					"C:\\Users\\John\\Desktop\\stuff\\" + helpFileName));
			writer.write(str.toString());
			writer.close();
		}
	}

}
