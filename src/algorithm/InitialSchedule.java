package algorithm;

import java.util.Arrays;

public class InitialSchedule {
	private Data data = new Data();

	// subjectId defines the subject
	// structure tutorialGroupId = 0 are lectures, tutorialGroupId = 1 are
	// tutorial group 1,
	// tutorialGroupId = 2 are tutorial group 2
	// tutorial = 0 is tutorialSlot 1, ..., tutorial = 2 is tutorialSlot 3
	// subjects[subject][tutorialGroup][tutorial]
	public int[][][] subjects = new int[Data.SUBJECT_COUNT][3][3];

	public static void main(String[] args) {
		InitialSchedule initialSchedule = new InitialSchedule();
		initialSchedule.distributeTutorials();
		System.out.println("result");
		System.out.println(Arrays.deepToString(initialSchedule.subjects));
		System.out.println(initialSchedule.globalConflict());
		for (int slot = 0; slot < Data.SLOT_COUNT; slot++) {
			System.out.println("Slot " + slot);
			for (int subject = 0; subject < initialSchedule.subjects.length; subject++) {
				for (int tutorialGroup = 1; tutorialGroup < initialSchedule.subjects[subject].length; tutorialGroup++) {
					for (int tutorial = 0; tutorial < initialSchedule.subjects[subject][tutorialGroup].length; tutorial++) {
						if (slot == initialSchedule.subjects[subject][tutorialGroup][tutorial]) {
							System.out.println("subject " + subject
									+ " tutorialGroup " + tutorialGroup
									+ " tutorial " + tutorial);
						}
					}
				}
			}
		}
	}

	/**
	 * Init Array with -1
	 */
	private void init() {
		for (int[][] subject : subjects) {
			for (int[] übung : subject) {
				Arrays.fill(übung, -1);
			}
		}
	}

	/** distribute Tutorials */
	private void distributeTutorials() {
		init();
		// System.out.println(Arrays.deepToString(subjects));
		System.out.println("distributeTutorials started");

		data.loadCombinations();

		// for each subject distribute tutorial, start with most populated, it
		// is sorted
		while (data.subjectPopulation.size() > 0) {
			// System.out.println(data.subjectPopulation.size());

			// extract most populated subject
			int maxFachId = data.subjectPopulation.get(0).id;
			System.out.println();
			System.out.println("most populated subject is now:" + maxFachId
					+ " with: " + data.subjectPopulation.get(0).population
					+ " students");
			data.subjectPopulation.remove(0);

			// subjects[maxFachId][1][0] = findBestSlot(maxFachId, 1, 0);
			// subjects[maxFachId][1][1] = findBestSlot(maxFachId, 1, 1);
			// subjects[maxFachId][2][0] = findBestSlot(maxFachId, 2, 0);
			// subjects[maxFachId][2][1] = findBestSlot(maxFachId, 2, 1);
			for (int i = 0; i < subjects[maxFachId][1].length; i++) {

				// distribute both tutorials
				subjects[maxFachId][1][i] = findBestSlot(maxFachId, 1, i);
				subjects[maxFachId][2][i] = findBestSlot(maxFachId, 2, i);
			}
		}
	}

	/**
	 * Find the best slot for a specific tutorial
	 * 
	 * @param subject
	 * @param tutorialGroup
	 * @param tutorial
	 * @return the best time slot for this tutorial
	 */
	private int findBestSlot(int subject, int tutorialGroup, int tutorial) {
		System.out.println("find best slot for subject " + subject
				+ " tutorialGroup " + tutorialGroup + " tutorial " + tutorial);
		int minConflictSlot = -1;
		int minConflictCount = globalConflict();

		// Try each Slot
		for (int i = 0; i < Data.SLOT_COUNT; i++) {

			subjects[subject][tutorialGroup][tutorial] = i;
			int conflictCount = globalConflict();

			// if we have less conflicts than before, remember minConflictSlot
			if (conflictCount <= minConflictCount) {
				// System.out.println("b");
				minConflictSlot = i;
				minConflictCount = conflictCount;
			}
		}
		// reset the slot because we only tested
		subjects[subject][tutorialGroup][tutorial] = -1;

		// debug
		System.out.println("best Slot for subject: " + subject
				+ " tutorialGroup: " + tutorialGroup + "  tutorial: "
				+ tutorial + " is: " + minConflictSlot + " with: "
				+ minConflictCount + " conflicts");

		return minConflictSlot;
	}

	/**
	 * Compute the number of students affected by conflicts
	 * 
	 * @return number of conflicts
	 */
	private int globalConflict() {
		int conflictCount = 0;
		for (int subject1 = 0; subject1 < subjects.length; subject1++) {
			for (int subject2 = 0; subject2 < subjects.length; subject2++) {
				if (subject1 < subject2) {
					// weight conflict by how many peopls want to study this,
					// from combinationmatrix
					conflictCount += tutorialConflictInt(subject1, subject2)
							* data.combinations[subject1][subject2];
				}
			}
		}
		return conflictCount;
	}
	

	/**
	 * Get if there is a tutorial conflict between two subjects
	 * 
	 * @param subject1
	 * @param subject2
	 * @return 1 if there is a tutorial conflict between two subjects, else 0
	 */
	private int tutorialConflictInt(int subject1, int subject2) {
		return (internalConflict(subject1, 1) + internalConflict(subject2, 1))
				+ (hasSameSlotInt(subject1, subject2, 1, 1)
						+ hasSameSlotInt(subject1, subject2, 1, 2)
						+ hasSameSlotInt(subject1, subject2, 2, 1) + hasSameSlotInt(
							subject1, subject2, 2, 2));
	}

	/**
	 * Test if two tutorial use the same slot
	 * 
	 * @param subject1
	 * @param subject2
	 * @param tutorial1
	 * @param tutorial2
	 * @return
	 */
	private int hasSameSlotInt(int subject1, int subject2, int tutorial1,
			int tutorial2) {
		// for each of the 3 slot test if there use the same timeSlot

		int result = 0;
		for (int i = 0; i < subjects[subject1][tutorial1].length; i++) {
			for (int j = 0; j < subjects[subject1][tutorial1].length; j++) {
				if ((subjects[subject1][tutorial1][i] == subjects[subject2][tutorial2][j])) {
					result += 1;
				}
			}
		}
		return result;
	}

	/*
	 * Check conflicts ionside one subject
	 * 
	 */
	private int internalConflict(int subject1, int tutorial1) {
		int result = 0;
		// for each of the 3 slot test if there use the same timeSlot
		for (int i = 0; i < subjects[subject1][tutorial1].length; i++) {
			for (int j = 0; j < subjects[subject1][tutorial1].length; j++) {
				//add conflict inside tutorialGroup 1
				if ((i != j)
						&& (subjects[subject1][1][i] == subjects[subject1][1][j])) {
					result += 1;
				}
				//add conflict inside tutorialGroup 2
				if ((i != j)
						&& (subjects[subject1][2][i] == subjects[subject1][2][j])) {
					result += 1;
				}
				//add conflict between tutorialGroups			
				if ((subjects[subject1][1][i] == subjects[subject1][2][j])) {
					result += 1;
				}

			}
		}
		return result;
	}

}