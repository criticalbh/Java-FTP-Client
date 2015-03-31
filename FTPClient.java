import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import fileHandler.FileIO;
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
		CmdArgs cmd = new CmdArgs(args);

		final String server = cmd.getServer();
		final String username = cmd.getUsername();
		final String password = cmd.getPassword();
		String[] fileNames = cmd.getFileNames();
				
		
		// check for errors in args
		if (cmd.hasErrors() == true) {
			System.exit(0);
		}
		
		// check for errors in files
	    FileIO files = new FileIO(fileNames);
	    if(files.getErrors() || files.getFilesToUpload().isEmpty()){
	    	System.exit(0);
	    }
	    
	    int numOfFiles = files.getNumOfElements();	    
		startThreads(numOfFiles, server, username, password, files.getFilesToUpload());
		Reports.showStatistics();		
	}
}
