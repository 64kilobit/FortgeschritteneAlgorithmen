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
	public static final String TAKEN_SLOTS_FILE_NAME = "data/takenSlots.csv";
	public static final String SUBJECTS_FILE_NAME = "data/subjects.csv";

	public List<Subject> subjectPopulation = new ArrayList<Subject>();
	public List<Subject> subjectPopulationUnsorted = new ArrayList<Subject>();
	public int[][] combinations;
	public String[] subjectNames = new String[SUBJECT_COUNT];

	// subjectId defines the subject
	// structure tutorialGroupId = 0 are lectures, tutorialGroupId = 1 are
	// tutorial group 1,
	// tutorialGroupId = 2 are tutorial group 2
	// tutorial = 0 is tutorialSlot 1, ..., tutorial = 2 is tutorialSlot 3
	// subjects[subject][tutorialGroup][tutorial]
	public int[][][] subjects = new int[Data.SUBJECT_COUNT][3][3];

	// for tesing
	public static void main(String[] args) {
		Data data = new Data();
		data.loadCombinations();
		data.debug();
		data.loadTakenSlots();
		System.out.println(Arrays.deepToString(data.subjects));
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
		subjectPopulationUnsorted.addAll(subjectPopulation);
		// sort
		Collections.sort(subjectPopulation, Collections.reverseOrder());

	}

	public void loadTakenSlots() {
		try {
			// load csv from file
			List<CSVRecord> subjectRecords = CSVParser.parse(
					new File(SUBJECTS_FILE_NAME), Charset.defaultCharset(),
					CSVFormat.EXCEL).getRecords();
			List<CSVRecord> takenSlotsRecords = CSVParser.parse(
					new File(TAKEN_SLOTS_FILE_NAME), Charset.defaultCharset(),
					CSVFormat.EXCEL).getRecords();

			// read subject from subjects.csv, all are in one row
			for (int subjectsLine = 0; subjectsLine < subjectRecords.size(); subjectsLine++) {
				// for each subject, so we can lookup the slot numbers
				for (int subject = 0; subject < SUBJECT_COUNT; subject++) {
					String subjectName = subjectRecords.get(subjectsLine)
							.get(subject).trim();
					System.out.println(subjectName);
					subjectNames[subject] = subjectName;
					int slotCounter = 0;
					// traverse slots from takenSlots.csv
					for (int slot = 0; slot < SLOT_COUNT; slot++) {

						for (int slotSubject = 0; slotSubject < takenSlotsRecords
								.get(slot * 3 + 1).size(); slotSubject++) {
							String slotSubjectName = takenSlotsRecords
									.get(slot * 3 + 1).get(slotSubject).trim();

							// our current subject matches the field in the
							// taken slots, so we need this slot
							if (slotSubjectName.equals(subjectName)) {
								System.out.println(subjectName + " found in "
										+ slot + " time: " + slotCounter);
								// !!!!!! GRI is 4 times in the takenSlot.csv,
								// every other element is in there 3 times
								if (slotCounter < 3) {
									subjects[subject][0][slotCounter] = slot;
								}
								slotCounter++;
							}

						}
					}

				}
				System.out.println(subjectRecords.get(subjectsLine));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}