package handler;

import java.util.StringTokenizer;

public class ParseArgs {

	private String Username, Password, Server, Files;
	public boolean Errors;
	public ParseArgs(String... args){
		parse(args);
	}
	
	private void parse(String... args){
        int i = 0;
        String arg;
        boolean first = false, second = false, third = false;
        
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            if (arg.equals("-u") && (i < args.length)) {
        		if(i == 1){
        			first = true;
        			Username = args[i++];
        		}
            }
            
            else if(arg.equals("-p") && (i < args.length)){
	        	if(i == 3){
	        		Password = args[i++];
	        		second = true;
                }
            }
            
            else if(arg.equals("-server") && (i < args.length)){
            	if(i == 5){
            		Server = args[i++];
            		third = true;
            	} 
            }
            
            else if(arg.equals("-files")){
            	if (i < args.length){
                    Files = args[i++];
                }
            }
        }
        
        if(Files != null){
        	Errors = false;
        }
        else if (checkErrors(first, second, third)){
        	Errors = true;
            System.out.println("Usage: -u user -p password -server server -files file1;file2;file3");
        }
        else{
        	Errors = false;
            System.out.println("Success!");
        }
	}
	
	private boolean checkErrors(boolean first, boolean second, boolean third) {
		if(first == true && second == true 
				&& third == true && Files != null){
			return false;
		}
		return true;
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
