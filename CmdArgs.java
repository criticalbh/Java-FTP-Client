import java.util.StringTokenizer;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class CmdArgs {


	public CmdArgs(String... args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
	}

	public String getUsername() {
		/*
		 * One solution for default username and password. 
		 * I could also make overloaded constructors in FTPClient class 
		 * that would accept only files as params.
		 */
		
		if (username == null)
			return "unixmen";
		return username;
	}

	public String getPassword() {
		if (password == null)
			return "unixmen";
		return password;
	}

	public String getServer() {
		if(server == null)
			return "127.0.0.1";
		return server;
	}
	/*
	 * Get list of files in raw format (dat1:dat2)
	 */
	public String getAllFiles(){
		return files;
	}
	
	/* 
	 * Windows use semi-colon ';' as path separator to separate a list
	 * of paths;
	 * while Unixes/Mac use colon ':'.
	 * 
	 * returns string array of file names
	 */
	public String[] getFileNames(){
		/*
		 * Seperate file names by colon (semicolon)
		 */
		if(getAllFiles()==null){
			checkErrors = true;
			return null;
		}
		StringTokenizer tokenizer = 
				new StringTokenizer(getAllFiles(), ":");
		
		/*
		 * Get number of files
		 */
		int numOfFiles = 
				tokenizer.countTokens();
		
		/*
		 * Initialize array with number of fiels
		 */
		String [] fileNames = 
				new String[numOfFiles];
		
		/*
		 * Check if number of files is greater than allowed (5)
		 */
		if (numOfFiles > 5) {
			checkErrors = true;
			System.out.println("Maximum number of allowed files is 5.");
		}else{
			/*
			 * Fill array of strings with file names
			 */
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				fileNames[i++] = tokenizer.nextToken();
			}
		}
		return fileNames;
	}
	
	public boolean hasErrors(){
		return checkErrors;
	}
	
	/***
	 * Initialize argument options
	 */
	@Option(name = "-u", usage = "FTP user name", metaVar = "INPUT")
	private String username;
	
	@Option(name = "-p", usage = "FTP password", metaVar = "INPUT")
	private String password;
	
	@Option(name = "-server", usage = "FTP server", metaVar = "INPUT")
	private String server;
	
	@Option(name = "-files", usage = "FTP server", metaVar = "INPUT", required = true)
	private String files;

	/*
	 * flag for errors
	 */
	private boolean checkErrors = false;
	
}
