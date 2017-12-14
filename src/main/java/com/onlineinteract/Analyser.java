package com.onlineinteract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.onlineinteract.enumeration.Status;

public class Analyser {

	private static final int SAMPLE_LENGTH = 3045859;
	private String[][] samples;
	private double previousClose = 0.0;
	private double origFallingValue = 0.0;
	private double lowestPercentageDip = 0.0;
	Status status = Status.SAME;

	public Analyser() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		samples = new String[SAMPLE_LENGTH][8];
		int counter = 0;

		ClassLoader classLoader = getClass().getClassLoader();
		File csvFile = new File(classLoader.getResource("samples.csv").getFile());

		try {
			System.out.println("Reading in CSV sample");
			br = new BufferedReader(new FileReader(csvFile));
			for (int i = 0; i < 3045859; i++) {
				counter = i;
				if ((line = br.readLine()) != null) {
					samples[i] = line.split(cvsSplitBy);
				} else {
					break;
				}
			}
			System.out.println("i is: " + counter);
			System.out.println("File read complete...");
			System.out.println(samples[0][0] + ", " + samples[0][1] + ", " + samples[0][2] + ", "
					+ samples[0][3] + ", " + samples[0][4] + ", " + samples[0][5] + ", "
					+ samples[0][6] + ", " + samples[0][7]);
			System.out.println(samples[1][0] + ", " + samples[1][1] + ", " + samples[1][2] + ", "
					+ samples[1][3] + ", " + samples[1][4] + ", " + samples[1][5] + ", "
					+ samples[1][6] + ", " + samples[1][7]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void detect4PercentSpiral() {
		for(int i = 1; i < samples.length - 1; i++) {
			double currentClose = Double.valueOf(samples[i][4]);
			if(currentClose < previousClose) {
				// System.out.println("Falling");
				if(origFallingValue == 0.0) {
					origFallingValue = currentClose;
				}
				// work out percentage dip from orig
				double diff = origFallingValue - currentClose;
				//System.out.println("Diff is: " + diff);
				double percentage = (100/origFallingValue) * diff;
				//System.out.println("Percentage is: " + percentage);
				//System.out.println("lowestPercentageDip is: " + lowestPercentageDip);
				// determine if lowest percentage dip during spiral
				if (percentage > lowestPercentageDip) {
					lowestPercentageDip = percentage;
				}
				
				status = Status.FALLING;
			} else  if (currentClose == previousClose) {
				// System.out.println("Same");
				status = Status.SAME;
			} else if (currentClose > previousClose) {
				// System.out.println("Rising");
				// end spiral is current = orig
				if (currentClose >= origFallingValue && origFallingValue != 0.0 && lowestPercentageDip != 0.0) {
					origFallingValue = 0.0;
					System.out.println("Lowest percentage dip = " + lowestPercentageDip);
					lowestPercentageDip = 0.0;
				}
				status = Status.RISING;
			}
			previousClose = currentClose;
//			System.out.println(currentClose);
		}
	}
	
	public static void main(String[] args) {
		Analyser analyser = new Analyser();
		analyser.detect4PercentSpiral();
	}
}