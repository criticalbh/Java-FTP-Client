package handler;
import java.io.File;
import java.util.ArrayList;


public class FileIO 
 {
	/********************
	 * Variable members
	 *******************/
	// Const, max number of allowed files at once is 5
	final private int MAX_FILES = 5;
	
	private ArrayList<File> filesToUpoad;
	private File[] fileList;	
	private boolean Errors = false;
	private String ErrorMessage = new String();
	
	/********************
	 * Constructor
	 *******************/
	public FileIO(String[] fileNames){
		int numFiles = fileNames.length;
		if(numFiles <= MAX_FILES){
			fileList = new File[fileNames.length]; 
			initFiles(fileNames);
			filesToUpoad = new ArrayList<File>();
			fileSetUpload();
		}
		else{
			Errors=true;
			ErrorMessage += "Number of maximum files exceeded. (5)\n";
		}
	}
	
	/********************
	 * Function members
	 *******************/
	private void initFiles(String[] fileNames){
		for (int i = 0; i < fileNames.length; i++) {
			fileList[i] = new File(fileNames[i]); 
		}
	}
	
	private boolean isFile(File fileName){		
		return 	fileName.canRead() 
				&& fileName.isFile();		
	}
	
	private void fileSetUpload(){
		String filesToUpload = "Files to be uploaded: ";
		for (int i = 0; i < fileList.length; i++){
			if(!isFile(fileList[i])){
				System.out.println(
						fileList[i].getName() 
						+ " is not file.");
			}else{
				filesToUpoad.add(fileList[i]);	
				filesToUpload += fileList[i].getName() + ";";
			}
		}
		System.out.println(filesToUpload);
	}
	
	public ArrayList<File> getFilesToUpload(){
		return filesToUpoad;
	}
	
	public int getNumOfElements(){
		return filesToUpoad.size();
	}
	
	public boolean getErrors(){
		return Errors;
	}	
	
	public void printErrors(){
		System.out.println(ErrorMessage);
		System.exit(0);
	}
}
