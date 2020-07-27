// testing
package TestSuiteServiceLog;

import java.io.File;
import java.io.FileNotFoundException; // Import this class to handle errors
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class TestElinvarLog {

	public static void main(String[] args) {

		int arrTempReqIdTimeCnt[] = { 0, 0, 0 };

		int arrChkTempReqIdTime[] = { 0, 0, 0 };

		HashMap<String, int[]> hmapLogDetails = new HashMap<String, int[]>();
		// TreeMap<String, int[]> hmapLogDetails = new TreeMap<String, int[]>();

		String LogDataLn;
		String strServStart;
		String strServNameReqID;
		String[] arrServNameReqID;
		String[] StartTimeStamp;
		String strServiceName;

		// int intReqestID, intServCallCnt;

		try {

			File myLogfileObj = new File(
					"C:\\Users\\Admin\\eclipse-workspace\\Test_ElinVar\\src\\test\\resources\\test.log");
			Scanner myLogReader = new Scanner(myLogfileObj);

			// While loop to iterate through file upto end

			while (myLogReader.hasNextLine()) {
				LogDataLn = myLogReader.nextLine();

				if (LogDataLn.contains("entry")) {
					// Extracting Service Name and unique Req ID combination
					strServNameReqID = LogDataLn.substring(LogDataLn.indexOf('(') + 1, LogDataLn.indexOf(')'));

					// Separating Service Name and Requst ID
					arrServNameReqID = strServNameReqID.split(":");
					strServiceName = arrServNameReqID[0];

					// Extracting the Start DateTime for the Request
					StartTimeStamp = LogDataLn.split(" TRACE");
					strServStart = StartTimeStamp[0];
					strServStart = strServStart.trim();

					int arrReqIdDuration[] = TestLog.getReqIdDuration(strServStart, strServNameReqID);

					// Adding Service log Elements to Hashmap
					if (hmapLogDetails.containsKey(strServiceName)) {

						// Getting requst ID And time for existing record
						arrTempReqIdTimeCnt = hmapLogDetails.get(strServiceName);

						// Checking if existing execution time Less
						if (arrTempReqIdTimeCnt[1] < arrReqIdDuration[1]) {
							int arrNewReqIdTime[] = { 0, 0, 0 };
							arrNewReqIdTime[0] = arrReqIdDuration[0];
							arrNewReqIdTime[1] = arrReqIdDuration[1];
							arrNewReqIdTime[2] = arrTempReqIdTimeCnt[2] + 1;

							// Replacing record for Servce added earlier
							hmapLogDetails.replace(strServiceName, arrNewReqIdTime);

						} else {
							// Replacing record for just to increase the count
							int arrNewReqIdTime[] = { 0, 0, 0 };
							arrNewReqIdTime[0] = arrTempReqIdTimeCnt[0];
							arrNewReqIdTime[1] = arrTempReqIdTimeCnt[1];
							arrNewReqIdTime[2] = arrTempReqIdTimeCnt[2] + 1;
							hmapLogDetails.replace(strServiceName, arrTempReqIdTimeCnt, arrNewReqIdTime);
						}

					} else {

						// creating record for Service if not present in Hashmap
						int arrReqIdTimeCnt[] = { 0, 0, 0 };
						arrReqIdTimeCnt[0] = arrReqIdDuration[0];
						arrReqIdTimeCnt[1] = arrReqIdDuration[1];
						arrReqIdTimeCnt[2] = 1;
						hmapLogDetails.put(strServiceName, arrReqIdTimeCnt);

					} // End of else to Add new Record in Hashmap

				} // End of If loop to check Request Entry

			} // End of While loop for 1 iteration to create Hashmap

			myLogReader.close();

			// Printing Hashmap
			System.out.println("========== Log file analysis output ========");
			for (String key : hmapLogDetails.keySet()) {
				arrChkTempReqIdTime = hmapLogDetails.get(key);

				System.out.println("Service Name : " + key);
				System.out.println("Request ID : " + arrChkTempReqIdTime[0]);
				System.out.println("Max Reqest Time : " + arrChkTempReqIdTime[1]);
				System.out.println("Count for Request : " + arrChkTempReqIdTime[2]);
				System.out.println();

			}

			System.out.println("==============================");

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred while reading file : file not found");
			e.printStackTrace();
		}
	} // End of Main method

	// method to get Request execution duration
	public static int[] getReqIdDuration(String strChkStartdate, String strChkEndReqIDName) {
		int arrChkReqIdDuration[] = { 0, 0 };
		String chkExitLogLn;
		String strServEnd;
		String[] EndTimeStamp;
		String[] arrChkServNameReqID;
		String strChkRequestID;
		int intChkReqestID;
		Date dtServStart = null;
		Date dtServEnd = null;
		int intServDuration;
		long lngServDuration;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS"); // Custom date format

		try {

			// Creating new File and Scanner object to iterate and find end time
			File chkLogfile = new File(
					"C:\\Users\\Admin\\eclipse-workspace\\Test_ElinVar\\src\\test\\resources\\test.log");
			Scanner chkLogReader = new Scanner(chkLogfile);

			while (chkLogReader.hasNextLine()) {

				chkExitLogLn = chkLogReader.nextLine();

				// Calculating the Duration
				if (chkExitLogLn.contains("exit") && chkExitLogLn.contains(strChkEndReqIDName)) {

					// Extracting Service Name and Requst ID
					arrChkServNameReqID = strChkEndReqIDName.split(":");
					strChkRequestID = arrChkServNameReqID[1];

					// Extracting End Date from Log line
					EndTimeStamp = chkExitLogLn.split(" TRACE");
					strServEnd = EndTimeStamp[0];
					strServEnd = strServEnd.trim();

					// Extracting Service duration for Request ID
					try {
						dtServStart = sdf.parse(strChkStartdate);
						dtServEnd = sdf.parse(strServEnd);

						// Get mSec from each, and subtract.
						lngServDuration = dtServEnd.getTime() - dtServStart.getTime();
						intServDuration = (int) lngServDuration;

						// Converting RequestID to integer
						intChkReqestID = Integer.valueOf(strChkRequestID);

						// Creating Array to return to main Method
						arrChkReqIdDuration[0] = intChkReqestID;
						arrChkReqIdDuration[1] = intServDuration;

					} catch (ParseException e) {
						e.printStackTrace();
					}

				} // End of if check

			} // End of While loop

			chkLogReader.close();

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred while reading file : file not found");
			e.printStackTrace();
		}

		return arrChkReqIdDuration;

	}// End of Method getDuration

}// End of Class
