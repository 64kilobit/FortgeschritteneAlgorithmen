package algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Data {

	public static final int SUBJECT_COUNT = 19;
	public static final int SLOT_COUNT = 25;
	public static final String COMBINATIONS_FILE_NAME = "data/combinations.csv";

	public List<Subject> subjectPopulation = new ArrayList<Subject>();
	public int[][] combinations;

	// for tesing
	public static void main(String[] args) {
		Data data = new Data();
		data.loadCombinations();
		data.debug();
	}

	public void debug() {
		// print combination matrix
		System.out.println("Printing Combination Matrix");
		for (int i = 0; i < combinations.length; i++) {
			System.out.println(Arrays.toString(combinations[i]));
		}
		// print subject population
		System.out.println("Printing Subject Population");
		System.out.println(Arrays.toString(subjectPopulation.toArray()));
	}

	public void loadCombinations() {
		// init arrays
		combinations = new int[SUBJECT_COUNT][SUBJECT_COUNT];

		try {
			// load csv from file
			File csvData = new File(COMBINATIONS_FILE_NAME);
			List<CSVRecord> records = CSVParser.parse(csvData,
					Charset.defaultCharset(), CSVFormat.EXCEL).getRecords();

			// read csv into array
			for (int i = 0; i < records.size(); i++) {
				CSVRecord csvRecord = records.get(i);
				// if we are in last line, read sums to array
				if (i == SUBJECT_COUNT) {
					for (int j = 0; j < csvRecord.size(); j++) {
						subjectPopulation.add(new Subject(j, Integer
								.parseInt(csvRecord.get(j).trim())));
					}
				} else {
					// load combination values
					for (int j = 0; j < csvRecord.size(); j++) {
						int value = Integer.parseInt(csvRecord.get(j).trim());
						// symmetric matrix
						combinations[i][j] = value;
						combinations[j][i] = value;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//sort
		Collections.sort(subjectPopulation, Collections.reverseOrder());

	}
}