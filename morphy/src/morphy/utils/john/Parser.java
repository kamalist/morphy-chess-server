package morphy.utils.john;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

import morphy.user.PersonalList;

public class Parser {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("C:\\Users\\John\\Desktop\\AddCensorCommand.java");
		FileReader reader = new FileReader(file);
		
		char[] arr = new char[2048];
		reader.read(arr);
		reader.close();
		String str = new String(arr);
		
		PersonalList[] list = PersonalList.values();
		for(int i=0;i<list.length;i++) {
			String cmd = (""+list[i].name().charAt(0)).toUpperCase() + list[i].name().substring(1);
			String myStr = str.replaceAll("AddChannel","Add" + cmd);
			myStr = myStr.replaceAll("PersonalList.channel","PersonalList." + list[i].name());
			myStr = myStr.replaceAll(" channel "," " + list[i].name() + " ");
			FileWriter writer = new FileWriter(new File("C:\\Users\\John\\Desktop\\Add" + cmd + "Command.java"));
			writer.write(myStr);
			writer.close();
		}
	}
}
