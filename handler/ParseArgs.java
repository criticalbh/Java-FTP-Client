package handler;

import java.util.StringTokenizer;

public class ParseArgs {

	private String Username, Password, Server, Files;
	private String ErrorMessage = new String();
	public boolean Errors = false;
	public ParseArgs(String... args){
		parse(args);
	}
	
	private void parse(String... args){
        int i = 0;
        String arg;
        
        
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            if (arg.equals("-u") && (i < args.length)) {
        			Username = args[i++];
            }
            
            else if(arg.equals("-p") && (i < args.length)){
	        		Password = args[i++];
            }
            
            else if(arg.equals("-server") && (i < args.length)){
            		Server = args[i++];
            }
            
            else if(arg.equals("-files")){
            	if (i < args.length){
                    Files = args[i++];
                }
            }
        }
        if(Files == null){
        	ErrorMessage+="Usage: –u ftpuser –p ftppass – server 127.0.0.1 –files file.dat";
        	Errors = true;
        }
	}
	public void printError(){
		System.out.println(ErrorMessage);
		System.exit(0);
	}
	public String getUsername() {
		if (Username == null)
			return "unixmen";
		return Username;
	}

	public String getPassword() {
		if (Password == null)
			return "unixmen";
		return Password;
	}

	public String getServer() {
		if(Server == null)
			return "127.0.0.1";
		return Server;
	}

	public String[] getFileNames(){
		StringTokenizer tokenizer = 
				new StringTokenizer(Files, ":");
		
		// Get number of files
		int numOfFiles = 
				tokenizer.countTokens();
		
		// Initialize array with number of fiels
		String [] fileNames = 
				new String[numOfFiles];
		
		// Fill array of strings with file names
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			fileNames[i++] = tokenizer.nextToken();
		}
		
		return fileNames;
	}
	
}
