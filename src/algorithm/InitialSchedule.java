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
	public int[][][] subjects = new int[Data.SUBJECT_COUNT][3][2];

	public static void main(String[] args) {
		InitialSchedule initialSchedule = new InitialSchedule();
		initialSchedule.distributeTutorials();
		System.out.println(Arrays.deepToString(initialSchedule.subjects));

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
			System.out.println("most populated subject is now:" + maxFachId
					+ " with: " + data.subjectPopulation.get(0).population
					+ " students");
			data.subjectPopulation.remove(0);

			// distribute all 3 slots
			for (int i = 0; i < 2; i++) {

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
				+ "  tutorialGroup " + tutorialGroup + " tutorial " + tutorial);
		int minConflictSlot = 0;
		int minConflictCount = globalConflict();

		// Try each Slot
		for (int i = 0; i < Data.SLOT_COUNT; i++) {

			subjects[subject][tutorialGroup][tutorial] = i;
			int conflictCount = globalConflict();

			// if we have less conflicts than before, remember minConflictSlot
			if (conflictCount < minConflictCount) {
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
		for (int fach1 = 0; fach1 < subjects.length; fach1++) {
			for (int fach2 = 0; fach2 < subjects.length; fach2++) {
				if (fach1 != fach2)
					// weight conflict by how many peopls want to stufy this,
					// from combinationmatrix
					conflictCount += tutorialConflikt(fach1, fach2)
							* data.combinations[fach1][fach2];
			}
		}
		return conflictCount;
	}

	/**
	 * Get if there is a tutorial conflikt between two subjects
	 * 
	 * @param subject1
	 * @param subject2
	 * @return 1 if there is a tutorial conflict between two subjects, else 0
	 */
	private int tutorialConflikt(int subject1, int subject2) {
		if (hasSameSlot(subject1, subject2, 1, 1)
				&& hasSameSlot(subject1, subject2, 1, 2)
				&& hasSameSlot(subject1, subject2, 2, 1)
				&& hasSameSlot(subject1, subject2, 2, 2)) {
			// there is a conflict
			return 1;
		} else {
			return 0;
		}
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
	private boolean hasSameSlot(int subject1, int subject2, int tutorial1,
			int tutorial2) {
		// for each of the 3 slot test if there use the same timeSlot
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (subjects[subject1][tutorial1][i] == subjects[subject2][tutorial2][j])
					return true;
			}
		}
		return false;
	}
}