package morphy.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import morphy.Morphy;
import morphy.properties.PreferenceKeys;
import morphy.service.PreferenceService;
import morphy.user.UserLevel;
import morphy.utils.MorphyStringTokenizer;
import morphy.utils.MorphyStringUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandContext {
	protected static Log LOG = LogFactory.getLog(CommandContext.class);

	protected String name;
	protected String[] aliases;
	protected String[] seeAlso;
	protected String usage;
	protected String lastModifiedBy;
	protected String lastModifiedDate;
	protected String help;
	protected UserLevel userLevel;

	public CommandContext(String commandFileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Morphy.COMMAND_FILES_DIR
					+ "/" + commandFileName + ".txt"));

			StringBuilder helpContent = new StringBuilder(1200);
			String lineTerminator = PreferenceService.getInstance().getString(
					PreferenceKeys.SocketConnectionLineDelimiter);

			boolean isParsingContent = false;
			String currentLine = null;

			while ((currentLine = reader.readLine()) != null) {
				if (isParsingContent) {
					helpContent.append(currentLine + lineTerminator);
				} else {
					if (StringUtils.startsWithIgnoreCase(currentLine, "Name:")) {
						name = currentLine.substring(5).trim().toLowerCase();
					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"UserLevel:")) {
						userLevel = UserLevel.valueOf(currentLine.substring(10)
								.trim());
					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"Usage:")) {
						usage = currentLine.substring(6).trim();
					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"Aliases:")) {
						String content = currentLine.substring(8).trim();
						MorphyStringTokenizer tok = new MorphyStringTokenizer(
								content, " ");
						List<String> aliasesList = new ArrayList<String>(10);
						while (tok.hasMoreTokens()) {
							aliasesList.add(tok.nextToken());
						}
						aliases = aliasesList.toArray(new String[0]);

					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"SeeAlso:")) {
						String content = currentLine.substring(8).trim();
						MorphyStringTokenizer tok = new MorphyStringTokenizer(
								content, " ");
						List<String> seeAlsoList = new ArrayList<String>(10);
						while (tok.hasMoreTokens()) {
							seeAlsoList.add(tok.nextToken());
						}
						aliases = seeAlsoList.toArray(new String[0]);
					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"LastModifiedBy:")) {
						lastModifiedBy = currentLine.substring(15).trim();
					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"LastModifiedDate:")) {
						lastModifiedDate = currentLine.substring(17).trim();
					} else if (StringUtils.startsWithIgnoreCase(currentLine,
							"Help:")) {
						isParsingContent = true;
					} else {
						LOG
								.warn("Encountered command header without a known keyword "
										+ currentLine);
					}
				}
			}
			help = MorphyStringUtils.replaceNewlines(helpContent.toString());

			if (StringUtils.isBlank(getName())) {
				throw new IllegalArgumentException(
						"Could not find Name: header in command. "
								+ commandFileName);
			}
			if (userLevel == null) {
				throw new IllegalArgumentException(
						"Could not find UserLevel: header in command. "
								+ commandFileName);
			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error reading help file: " + commandFileName, t);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable t) {
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getAliases() {
		return aliases;
	}

	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	public String[] getSeeAlso() {
		return seeAlso;
	}

	public void setSeeAlso(String[] seeAlso) {
		this.seeAlso = seeAlso;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public UserLevel getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(UserLevel userLevel) {
		this.userLevel = userLevel;
	}
}
