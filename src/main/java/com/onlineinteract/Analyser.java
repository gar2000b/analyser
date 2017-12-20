package com.onlineinteract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.onlineinteract.enumeration.Status;

public class Analyser {

	private static final int SAMPLE_LENGTH = 3045859;
	private String[][] samples;
	private double previousClose = 0.0;
	private double origValue = 0.0;
	private double lowestPercentageDip = 0.0;
	Status status = Status.SAME;
	private String origSummary;
	private double lowestClose;
	private String lowestDate;
	private long lowestTimestamp;
	private List<Double> allDips;
	private List<Double> dips0to1Percent;
	private List<Double> dips1to2Percent;
	private List<Double> dips2to3Percent;
	private List<Double> dips3to4Percent;
	private List<Double> dips4to5Percent;
	private List<Double> dips5to6Percent;
	private List<Double> dips6to7Percent;
	private List<Double> dips7to8Percent;
	private List<Double> dips8to9Percent;
	private List<Double> dips9to10Percent;
	private List<Double> dips10to11Percent;
	private List<Double> dips11to12Percent;
	private List<Double> dips12to13Percent;
	private List<Double> dips13to14Percent;
	private List<Double> dips14to15Percent;
	private List<Double> dips15to16Percent;
	private List<Double> dips16to17Percent;
	private List<Double> dips17to18Percent;
	private List<Double> dips18to19Percent;
	private List<Double> dips19to20Percent;
	private List<Double> dips20to30Percent;

	public Analyser() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		samples = new String[SAMPLE_LENGTH][8];
		allDips = new ArrayList<>();
		dips0to1Percent = new ArrayList<>();
		dips1to2Percent = new ArrayList<>();
		dips2to3Percent = new ArrayList<>();
		dips3to4Percent = new ArrayList<>();
		dips4to5Percent = new ArrayList<>();
		dips5to6Percent = new ArrayList<>();
		dips6to7Percent = new ArrayList<>();
		dips7to8Percent = new ArrayList<>();
		dips8to9Percent = new ArrayList<>();
		dips9to10Percent = new ArrayList<>();
		dips10to11Percent = new ArrayList<>();
		dips11to12Percent = new ArrayList<>();
		dips12to13Percent = new ArrayList<>();
		dips13to14Percent = new ArrayList<>();
		dips14to15Percent = new ArrayList<>();
		dips15to16Percent = new ArrayList<>();
		dips16to17Percent = new ArrayList<>();
		dips17to18Percent = new ArrayList<>();
		dips18to19Percent = new ArrayList<>();
		dips19to20Percent = new ArrayList<>();
		dips20to30Percent = new ArrayList<>();
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
			System.out.println(samples[0][0] + ", " + samples[0][1] + ", " + samples[0][2] + ", " + samples[0][3] + ", "
					+ samples[0][4] + ", " + samples[0][5] + ", " + samples[0][6] + ", " + samples[0][7]);
			System.out.println(samples[1][0] + ", " + samples[1][1] + ", " + samples[1][2] + ", " + samples[1][3] + ", "
					+ samples[1][4] + ", " + samples[1][5] + ", " + samples[1][6] + ", " + samples[1][7]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void detect4PercentSpiral() {
		for (int i = 1; i < samples.length - 1; i++) {
			double currentClose = Double.valueOf(samples[i][4]);
			if (currentClose < previousClose) {
				// System.out.println("Falling");
				if (origValue == 0.0) {
					origValue = previousClose;
					long timestamp = 1000 * Long.valueOf(samples[i][0]);
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy @ HH:mm:ss");
					String date = sdf.format(new Date(timestamp));
					origSummary = "\nSpiral Date: " + date + " - " + samples[i][0] + " - CurrentClose: " + currentClose
							+ " - OrigValue: " + origValue;
				}
				// work out percentage dip from orig
				double diff = origValue - currentClose;
				// System.out.println("Diff is: " + diff);
				double percentage = (100 / origValue) * diff;
				// System.out.println("Percentage is: " + percentage);
				// System.out.println("lowestPercentageDip is: " +
				// lowestPercentageDip);
				// determine if lowest percentage dip during spiral
				if (percentage > lowestPercentageDip) {
					lowestPercentageDip = percentage;
					lowestClose = currentClose;
					lowestTimestamp = 1000 * Long.valueOf(samples[i][0]);
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy @ HH:mm:ss");
					lowestDate = sdf.format(new Date(lowestTimestamp));
				}

				status = Status.FALLING;
			} else if (currentClose == previousClose) {
				// System.out.println("Same");
				status = Status.SAME;
			} else if (currentClose > previousClose) {
				// System.out.println("Rising");
				// end spiral is current = orig
				if (currentClose >= origValue && origValue != 0.0 && lowestPercentageDip != 0.0) {
					if (lowestPercentageDip > 1) {
						allDips.add(lowestPercentageDip);
						System.out.println(origSummary);
						System.out.println(
								"Lowest Date: " + lowestDate + " - " + lowestTimestamp/1000 + " - Lowest percentage dip = " + lowestPercentageDip + " - Lowest Close: " + lowestClose);
						long timestamp = 1000 * Long.valueOf(samples[i][0]);
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy @ HH:mm:ss");
						String date = sdf.format(new Date(timestamp));
						System.out.println("Recovery Date: " + date + " - " + samples[i][0] + " - CurrentClose: "
								+ currentClose + " - PreviousClose: " + previousClose + " - OrigValue: " + origValue);
					}
					origValue = 0.0;
					lowestPercentageDip = 0.0;
				}
				status = Status.RISING;
			}
			previousClose = currentClose;
			// System.out.println(currentClose);
		}
	}
	
	public void sortAllDips() {
		Collections.sort(allDips);
	}
	
	public void renderAllDips() {
		System.out.println("\n");
		for (double dip : allDips) {
			System.out.println("* Dip = " + dip);
		}
	}
	
	public void howManyDipsTotal() {
		System.out.println("\n* Total no of dips: " + allDips.size());
	}
	
	public void renderDips(List<Double> dips) {
		System.out.println("\n");
		for (double dip : dips) {
			System.out.println("* Dip = " + dip);
		}
	}
	
	public void howManyDips0to1Percent() {
		for (Double dip : allDips) {
			if(dip >= 0 && dip < 1) {
				dips0to1Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 0-1% dips: " + dips0to1Percent.size());
	}
	
	public void howManyDips1to2Percent() {
		for (Double dip : allDips) {
			if(dip >= 1 && dip < 2) {
				dips1to2Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 1-2% dips: " + dips1to2Percent.size());
	}
	
	public void howManyDips2to3Percent() {
		for (Double dip : allDips) {
			if(dip >= 2 && dip < 3) {
				dips2to3Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 2-3% dips: " + dips2to3Percent.size());
	}
	
	public void howManyDips3to4Percent() {
		for (Double dip : allDips) {
			if(dip >= 3 && dip < 4) {
				dips3to4Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 3-4% dips: " + dips3to4Percent.size());
	}
	
	public void howManyDips4to5Percent() {
		for (Double dip : allDips) {
			if(dip >= 4 && dip < 5) {
				dips4to5Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 4-5% dips: " + dips4to5Percent.size());
	}
	
	public void howManyDips5to6Percent() {
		for (Double dip : allDips) {
			if(dip >= 5 && dip < 6) {
				dips5to6Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 5-6% dips: " + dips5to6Percent.size());
	}
	
	public void howManyDips6to7Percent() {
		for (Double dip : allDips) {
			if(dip >= 6 && dip < 7) {
				dips6to7Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 6-7% dips: " + dips6to7Percent.size());
	}
	
	public void howManyDips7to8Percent() {
		for (Double dip : allDips) {
			if(dip >= 7 && dip < 8) {
				dips7to8Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 7-8% dips: " + dips7to8Percent.size());
	}
	
	public void howManyDips8to9Percent() {
		for (Double dip : allDips) {
			if(dip >= 8 && dip < 9) {
				dips8to9Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 8-9% dips: " + dips8to9Percent.size());
	}
	
	public void howManyDips9to10Percent() {
		for (Double dip : allDips) {
			if(dip >= 9 && dip < 10) {
				dips9to10Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 9-10% dips: " + dips9to10Percent.size());
	}
	
	public void howManyDips10to11Percent() {
		for (Double dip : allDips) {
			if(dip >= 10 && dip < 11) {
				dips10to11Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 10-11% dips: " + dips10to11Percent.size());
	}
	
	public void howManyDips11to12Percent() {
		for (Double dip : allDips) {
			if(dip >= 11 && dip < 12) {
				dips11to12Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 11-12% dips: " + dips11to12Percent.size());
	}
	
	public void howManyDips12to13Percent() {
		for (Double dip : allDips) {
			if(dip >= 12 && dip < 13) {
				dips12to13Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 12-13% dips: " + dips12to13Percent.size());
	}
	
	public void howManyDips13to14Percent() {
		for (Double dip : allDips) {
			if(dip >= 13 && dip < 14) {
				dips14to15Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 13-14% dips: " + dips13to14Percent.size());
	}
	
	public void howManyDips14to15Percent() {
		for (Double dip : allDips) {
			if(dip >= 14 && dip < 15) {
				dips14to15Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 14-15% dips: " + dips14to15Percent.size());
	}
	
	public void howManyDips15to16Percent() {
		for (Double dip : allDips) {
			if(dip >= 15 && dip < 16) {
				dips15to16Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 15-16% dips: " + dips15to16Percent.size());
	}
	
	public void howManyDips16to17Percent() {
		for (Double dip : allDips) {
			if(dip >= 16 && dip < 17) {
				dips16to17Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 16-17% dips: " + dips16to17Percent.size());
	}
	
	public void howManyDips17to18Percent() {
		for (Double dip : allDips) {
			if(dip >= 17 && dip < 18) {
				dips17to18Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 17-18% dips: " + dips17to18Percent.size());
	}
	
	public void howManyDips18to19Percent() {
		for (Double dip : allDips) {
			if(dip >= 18 && dip < 19) {
				dips18to19Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 18-19% dips: " + dips18to19Percent.size());
	}
	
	public void howManyDips19to20Percent() {
		for (Double dip : allDips) {
			if(dip >= 19 && dip < 20) {
				dips19to20Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 19-20% dips: " + dips19to20Percent.size());
	}
	
	public void howManyDips20to30Percent() {
		for (Double dip : allDips) {
			if(dip >= 20 && dip < 30) {
				dips20to30Percent.add(dip);
			}
		}
		System.out.println("\n* Total no of 20-30% dips: " + dips20to30Percent.size());
	}

	public static void main(String[] args) {
		Analyser analyser = new Analyser();
		analyser.detect4PercentSpiral();
		analyser.sortAllDips();
		analyser.renderAllDips();
		analyser.howManyDipsTotal();
		analyser.howManyDips0to1Percent();
		analyser.howManyDips1to2Percent();
		analyser.howManyDips2to3Percent();
		analyser.howManyDips3to4Percent();
		analyser.howManyDips4to5Percent();
		analyser.howManyDips5to6Percent();
		analyser.howManyDips6to7Percent();
		analyser.howManyDips7to8Percent();
		analyser.howManyDips8to9Percent();
		analyser.howManyDips9to10Percent();
		analyser.howManyDips10to11Percent();
		analyser.howManyDips11to12Percent();
		analyser.howManyDips12to13Percent();
		analyser.howManyDips13to14Percent();
		analyser.howManyDips14to15Percent();
		analyser.howManyDips15to16Percent();
		analyser.howManyDips16to17Percent();
		analyser.howManyDips17to18Percent();
		analyser.howManyDips18to19Percent();
		analyser.howManyDips19to20Percent();
		analyser.howManyDips20to30Percent();
		
		// Question: picking a range - what are the changes that it keeps dipping:
		// If we pick 1-2%, which has 214 dips from a possible 418 dips (1% - Max%)
		// Then there is a 100 - ((100/418) * 214) = 48.80% chance that it keeps dipping.
		
		// If we pick 2-3%, which has 72 dips from a possible 418-214=204 dips (2% - Max%)
		// Then there is a 100 - ((100/204) * 72) = 64.71% chance that it keeps dipping.
		
		// If we pick 3-4%, which has 42 dips from a possible 418-214-72=132 dips (3% - Max%)
		// Then there is a 100 - ((100/132) * 42) = 68.18% chance that it keeps dipping.
		
		// If we pick 4-5%, which has 15 dips from a possible 418-214-72-42=90 dips (4% - Max%)
		// Then there is a 100 - ((100/90) * 15) = 83.33% chance that it keeps dipping.
		
		// If we pick 5-6%, which has 16 dips from a possible 418-214-72-42-15=75 dips (5% - Max%)
		// Then there is a 100 - ((100/75) * 16) = 78.67% chance that it keeps dipping.
		
		// If we pick 6-7%, which has 9 dips from a possible 418-214-72-42-15-16=59 dips (6% - Max%)
		// Then there is a 100 - ((100/59) * 9) = 84.75% chance that it keeps dipping.
		
		// If we pick 7-8%, which has 7 dips from a possible 418-214-72-42-15-16-9=50 dips (7% - Max%)
		// Then there is a 100 - ((100/50) * 7) = 86% chance that it keeps dipping.
		
		// If we pick 8-9%, which has 4 dips from a possible 418-214-72-42-15-16-9-7=43 dips (8% - Max%)
		// Then there is a 100 - ((100/43) * 4) = 90.70% chance that it keeps dipping.
		
		// If we pick 9-10%, which has 4 dips from a possible 418-214-72-42-15-16-9-7-4=39 dips (9% - Max%)
		// Then there is a 100 - ((100/39) * 4) = 89.74% chance that it keeps dipping.
		
		// If we pick 10-11%, which has 4 dips from a possible 418-214-72-42-15-16-9-7-4-4=35 dips (10% - Max%)
		// Then there is a 100 - ((100/35) * 4) = 88.57% chance that it keeps dipping.
		
		// If we pick 11-12%, which has 0 dips from a possible 418-214-72-42-15-16-9-7-4-4-4=31 dips (11% - Max%)
		// Then there is a 100 - ((100/31) * 0) = 100% chance that it keeps dipping.
		
		// If we pick 12-13%, which has 6 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0=31 dips (12% - Max%)
		// Then there is a 100 - ((100/31) * 6) = 80.65% chance that it keeps dipping.
		
		// If we pick 13-14%, which has 0 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6=25 dips (13% - Max%)
		// Then there is a 100 - ((100/25) * 0) = 100% chance that it keeps dipping.
		
		// If we pick 14-15%, which has 8 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6-0=25 dips (14% - Max%)
		// Then there is a 100 - ((100/25) * 8) = 68% chance that it keeps dipping.
		
		// If we pick 15-16%, which has 1 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6-0-8=17 dips (15% - Max%)
		// Then there is a 100 - ((100/17) * 1) = 94.12% chance that it keeps dipping.
		
		// If we pick 16-17%, which has 1 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6-0-8-1=16 dips (16% - Max%)
		// Then there is a 100 - ((100/16) * 1) = 93.75% chance that it keeps dipping.
		
		// If we pick 17-18%, which has 1 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6-0-8-1-1=15 dips (17% - Max%)
		// Then there is a 100 - ((100/15) * 1) = 93.33% chance that it keeps dipping.
		
		// If we pick 18-19%, which has 0 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6-0-1-1-1-1=14 dips (18% - Max%)
		// Then there is a 100 - ((100/14) * 0) = 100% chance that it keeps dipping.		
		
		// If we pick 19-20%, which has 1 dips from a possible 418-214-72-42-15-16-9-7-4-4-4-0-6-0-1-1-1-1-0=14 dips (19% - Max%)
		// Then there is a 100 - ((100/14) * 1) = 92.86% chance that it keeps dipping.
		
		// The determination is that one should sell @ 4%.
	}
}