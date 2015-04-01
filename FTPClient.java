import handler.FileIO;
import handler.ParseArgs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ftpUpload.FTPUpload;
import ftpUpload.Reports;

public class FTPClient {

	public static void startThreads(int numOfFiles, final String server,
			final String username, final String password, ArrayList<File> files) {

		// Thread pool
		ExecutorService executor = Executors.newFixedThreadPool(numOfFiles);

		
		// Array of threads, length -> number of files
		Thread[] tds = new Thread[numOfFiles];

		int i = 0;
		for (final File file : files) {
			tds[i] = new Thread() {
				public void run() {
					try {
						FTPUpload.sendFile(server, username, password, file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			executor.execute(tds[i++]);
		}

		executor.shutdown();
	
		// Wait for execution of all threads
		while (!executor.isTerminated()) {

		}
	}
	
	public static void main(String[] args) throws IOException {

		// Parse arguments
	
		ParseArgs arguments = new ParseArgs(args);
		
		if(arguments.Errors == false){
			String username = arguments.getUsername();
			String password = arguments.getPassword();
			String server = arguments.getServer();
			String [] fileNames = arguments.getFileNames();
			
			FileIO files = new FileIO(fileNames);
		    if(files.getErrors() || files.getFilesToUpload().isEmpty()){
		    	System.exit(0);
		    }
		    
		    int numOfFiles = files.getNumOfElements();	    
			startThreads(numOfFiles, server, username, password, files.getFilesToUpload());
			Reports.showStatistics();
		}else{
			arguments.printError();
		}
	}
}
