package ftpUpload;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Reports {
	private static ArrayList<String> Names = new ArrayList<String>();
	private static ArrayList<Double> Speed = new ArrayList<Double>();
	private static ArrayList<Double> Time = new ArrayList<Double>();
	
	/*
	 * Synchronized because in previous version sometimes error occured
	 * during writing values to ArrayList and later I discovered that
	 * it was caused by thread concurrency.
	 */
	public synchronized static void addStatistics(String name,double speed,double time) {
		Names.add(name);
		Speed.add(speed);
		Time.add(time);
	}
	
	/*
	 * Display statistics for each file, and overall
	 */
	public static void showStatistics() {
		
		int numOfFiles = Names.size();
		
		double totalSpeed = 0;
		double maxTime = 0;
		
		String output = new String();
		DecimalFormat df = new DecimalFormat("0.00");
		

		for (int k = 0; k < numOfFiles; k++) {			
			String name = 
					Names.get(k) != null ? Names.get(k) : "File";
			double speed = 
					Speed.get(k) != 0 ? Speed.get(k) : 0;
			double timenum = 
					Time.get(k) != 0 ? Time.get(k) : 0;

			// Since You said in one or two rows I had to do it like this
			output += name + " at " + df.format(speed)
					+ " KB/s, in " + df.format(timenum) + " secs. ";

			
			// Check maximum time of upload
			totalSpeed += speed;
			if (maxTime < timenum) {
				maxTime = timenum;
			}
		}
		
		String overall = "Average speed for all files: "
				+ df.format(totalSpeed / numOfFiles) 
				+ " KBs. All finished in : " 
				+ df.format(maxTime);
		
		displayReport(output, overall);
	}
	
	private static void displayReport(String output, String overall){
		System.out.println(output + "\n"+overall);
	}
}
