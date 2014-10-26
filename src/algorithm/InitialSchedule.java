package algorithm;

import java.util.Arrays;

/**
 * Class for distributing subjects in an initial schedule.
 * 
 * The Rating Function are for expressing how bad a solution is.
 */
public class InitialSchedule {

	public Data data = new Data();

	public static void main(String[] args) {
		new InitialSchedule();
	}

	public InitialSchedule() {
		distributeTutorials();
		debug();
	}

	public void debug() {
		System.out.println();
		System.out.println("Result");
		System.out.println(Arrays.deepToString(data.subjects));

		// for all slots print the tutotrials using this slot
		for (int slot = 0; slot < Data.SLOT_COUNT; slot++) {
			System.out.println();
			System.out.println("Slot " + slot);
			for (int subject = 0; subject < Data.SUBJECT_COUNT; subject++) {
				for (int tutorialGroup = 0; tutorialGroup < Data.TUTORIAL_GROUP_COUNT; tutorialGroup++) {
					for (int tutorial = 0; tutorial < Data.TUTORIAL_COUNT; tutorial++) {
						if (slot == data.subjects[subject][tutorialGroup][tutorial]) {
							System.out.println("subject " + subject + " "
									+ data.subjectNames[subject]
									+ " tutorialGroup " + tutorialGroup
									+ " tutorial " + tutorial);
						}
					}
				}
			}
		}

		// For each subject print other subjects we have conflict with
		System.out.println();
		System.out.println("Conflicts by subject");
		for (int subject1 = 0; subject1 < Data.SUBJECT_COUNT; subject1++) {
			System.out.println();
			System.out.println("Subject: " + subject1 + " "
					+ data.subjectNames[subject1] + " ("
					+ data.subjectPopulationUnsorted.get(subject1).population
					+ " students) has conflict with:");
			for (int subject2 = 0; subject2 < Data.SUBJECT_COUNT; subject2++) {
				if (subject1 < subject2) {
					if (tutorialConflict(subject1, subject2) == 1) {
						System.out
								.println("> subject "
										+ subject2
										+ " "
										+ data.subjectNames[subject2]
										+ " ("
										+ data.subjectPopulationUnsorted
												.get(subject2).population
										+ " students) students affected: "
										+ data.combinations[subject1][subject2]);
						System.out.println("> "
								+ Arrays.deepToString(data.subjects[subject1]));
						System.out.println("> "
								+ Arrays.deepToString(data.subjects[subject2]));
					}
				}
			}
		}

		System.out.println();
		System.out.println("Conflicts");
		System.out.println(globalConflict());
		System.out.println();

	}

	/**
	 * Distribute Tutorials, complexity O(SUBJECT_COUNT * Time(findBestSlot))
	 * 
	 * */
	private void distributeTutorials() {
		System.out.println("distributeTutorials started");

		// for each subject distribute tutorial, start with most populated, it
		// is sorted
		while (data.subjectPopulationSorted.size() > 0) {

			// extract most populated subject
			int maxFachId = data.subjectPopulationSorted.get(0).id;
			System.out.println();
			System.out.println("most populated subject is now:" + maxFachId
					+ " with: "
					+ data.subjectPopulationSorted.get(0).population
					+ " students");
			data.subjectPopulationSorted.remove(0);

			for (int i = 0; i < Data.TUTORIAL_COUNT; i++) {

				// distribute both tutorials
				data.subjects[maxFachId][1][i] = findBestSlot(maxFachId, 1, i);
				data.subjects[maxFachId][2][i] = findBestSlot(maxFachId, 2, i);
			}
		}
	}

	/**
	 * Find the best slot for a specific tutorial, complexity O(SLOT_COUNT *
	 * Time(globalConflictRating))
	 * 
	 * @param subject
	 * @param tutorialGroup
	 * @param tutorial
	 * @return the best time slot for this tutorial
	 */
	private int findBestSlot(int subject, int tutorialGroup, int tutorial) {
		int minConflictSlot = -1;
		int minConflictCount = Integer.MAX_VALUE;

		// try each Slot
		for (int i = 0; i < Data.SLOT_COUNT; i++) {

			data.subjects[subject][tutorialGroup][tutorial] = i;
			int conflictCount = globalConflictRating();

			// if we have less conflicts than before, remember minConflictSlot
			if (conflictCount <= minConflictCount) {
				minConflictSlot = i;
				minConflictCount = conflictCount;
			}
		}
		// reset the slot because we only tested
		data.subjects[subject][tutorialGroup][tutorial] = -1;

		// debug
		System.out.println("best Slot for subject: " + subject
				+ " tutorialGroup: " + tutorialGroup + "  tutorial: "
				+ tutorial + " is: " + minConflictSlot + " with: "
				+ minConflictCount + " conflicts");

		return minConflictSlot;
	}

	/**
	 * Compute the number of students affected by conflicts, rating, complexity
	 * O(SUBJECT_COUNT^2 * Time(tutorialConflictRating))
	 * 
	 * @return number of conflicts
	 */
	public int globalConflictRating() {
		int conflictCount = 0;
		for (int subject1 = 0; subject1 < Data.SUBJECT_COUNT; subject1++) {
			for (int subject2 = 0; subject2 < Data.SUBJECT_COUNT; subject2++) {
				if (subject1 < subject2) {
					// weight conflict by how many peopls want to study this,
					// from combinationmatrix
					conflictCount += tutorialConflictRating(subject1, subject2)
							* data.combinations[subject1][subject2];
				}
			}
		}
		return conflictCount;
	}

	/**
	 * Compute the number of students affected by conflicts, complexity
	 * O(SUBJECT_COUNT^2 * Time(tutorialConflict))
	 * 
	 * @return number of conflicts
	 */
	public int globalConflict() {
		int conflictCount = 0;
		for (int subject1 = 0; subject1 < Data.SUBJECT_COUNT; subject1++) {
			for (int subject2 = 0; subject2 < Data.SUBJECT_COUNT; subject2++) {
				if (subject1 < subject2) {
					// weight conflict by how many peopls want to study this,
					// from combinationmatrix
					conflictCount += tutorialConflict(subject1, subject2)
							* data.combinations[subject1][subject2];
				}
			}
		}
		return conflictCount;
	}

	/**
	 * Get if there is a tutorial conflict between two subjects, rating,
	 * complexity O(2*Time(internalConflictRating)+8*Time(hasSameSlotRating))
	 * 
	 * @param subject1
	 * @param subject2
	 * @return 1 if there is a tutorial conflict between two subjects, else 0
	 */
	private int tutorialConflictRating(int subject1, int subject2) {
		return (internalConflictRating(subject1) + internalConflictRating(subject2))
				+ Math.min(Math.min(hasSameSlotRating(subject1, subject2, 1, 1)
						+ hasSameSlotRating(subject1, subject2, 1, 0)
						+ hasSameSlotRating(subject1, subject2, 0, 1),
						hasSameSlotRating(subject1, subject2, 1, 2)
								+ hasSameSlotRating(subject1, subject2, 1, 0)
								+ hasSameSlotRating(subject1, subject2, 0, 2)),
						Math.min(
								+hasSameSlotRating(subject1, subject2, 2, 1)
										+ hasSameSlotRating(subject1, subject2,
												2, 0)
										+ hasSameSlotRating(subject1, subject2,
												0, 1),
								hasSameSlotRating(subject1, subject2, 2, 2)
										+ hasSameSlotRating(subject1, subject2,
												2, 0)
										+ hasSameSlotRating(subject1, subject2,
												0, 2)));

	}

	/**
	 * Get if there is a tutorial conflict between two subjects, rating,
	 * complexity O(4*Time(internalConflict)+12*Time(hasSameSlot))
	 * 
	 * @param subject1
	 * @param subject2
	 * @return 1 if there is a tutorial conflict between two subjects, else 0
	 */
	private int tutorialConflict(int subject1, int subject2) {
		return (((hasSameSlot(subject1, subject2, 1, 1)
				|| hasSameSlot(subject1, subject2, 0, 1)
				|| hasSameSlot(subject1, subject2, 1, 0)
				|| internalConflict(subject1, 1) || internalConflict(subject2,
					1)))
				&& (hasSameSlot(subject1, subject2, 1, 2)
						|| hasSameSlot(subject1, subject2, 0, 2)
						|| hasSameSlot(subject1, subject2, 1, 0)
						|| internalConflict(subject1, 1) || internalConflict(
							subject2, 2))
				&& (hasSameSlot(subject1, subject2, 2, 1)
						|| hasSameSlot(subject1, subject2, 2, 0)
						|| hasSameSlot(subject1, subject2, 0, 1)
						|| internalConflict(subject1, 2) || internalConflict(
							subject2, 1))
				&& (hasSameSlot(subject1, subject2, 2, 2)
						|| hasSameSlot(subject1, subject2, 0, 2)
						|| hasSameSlot(subject1, subject2, 2, 0)
						|| internalConflict(subject1, 2) || internalConflict(
							subject2, 2)) ? 1 : 0);
	}

	/**
	 * Test if two tutorial use the same slot, rating function, Complexity
	 * O(TUTORIAL_COUNT^2)
	 * 
	 * @param subject1
	 * @param subject2
	 * @param tutorial1
	 * @param tutorial2
	 * @return
	 */
	private int hasSameSlotRating(int subject1, int subject2, int tutorial1,
			int tutorial2) {
		// for each of the 3 slot test if there use the same timeSlot

		int result = 0;
		for (int i = 0; i < Data.TUTORIAL_COUNT; i++) {
			for (int j = 0; j < Data.TUTORIAL_COUNT; j++) {
				if ((data.subjects[subject1][tutorial1][i] == data.subjects[subject2][tutorial2][j])) {
					result += 1;
				}
			}
		}
		return result;
	}

	/**
	 * Test if two tutorial use the same slot, rating function, Complexity
	 * O(TUTORIAL_COUNT^2)
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
		for (int i = 0; i < Data.TUTORIAL_COUNT; i++) {
			for (int j = 0; j < Data.TUTORIAL_COUNT; j++) {
				if ((data.subjects[subject1][tutorial1][i] == data.subjects[subject2][tutorial2][j])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check conflicts inside one subject, Complexity O(TUTORIAL_COUNT^2 *
	 * TUTORIAL_GROUP_COUNT^2)
	 * 
	 * @param subject1
	 * @return
	 */
	private int internalConflictRating(int subject1) {
		int result = 0;
		// for each of the 3 slot test if there use the same timeSlot
		for (int tutorialGroup1 = 0; tutorialGroup1 < Data.TUTORIAL_GROUP_COUNT; tutorialGroup1++) {
			for (int tutorialGroup2 = 0; tutorialGroup2 < Data.TUTORIAL_GROUP_COUNT; tutorialGroup2++) {
				for (int i = 0; i < Data.TUTORIAL_COUNT; i++) {
					for (int j = 0; j < Data.TUTORIAL_COUNT; j++) {
						// add conflict inside tutorialGroup 1
						if (((i != j) || (tutorialGroup1 != tutorialGroup2))
								&& (data.subjects[subject1][tutorialGroup1][i] == data.subjects[subject1][tutorialGroup2][j])) {
							result += 1;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Check conflicts inside one tutorialGroup subject, Complexity
	 * O(TUTORIAL_COUNT^2)
	 * 
	 * @param subject1
	 * @param tutorialGroup1
	 * @return
	 */
	private boolean internalConflict(int subject1, int tutorialGroup1) {
		boolean result = false;
		// for each of the 3 slot test if there use the same timeSlot
		for (int i = 0; i < Data.TUTORIAL_COUNT; i++) {
			for (int j = 0; j < Data.TUTORIAL_COUNT; j++) {
				// add conflict inside tutorialGroup 1
				if ((i != j)
						&& (data.subjects[subject1][tutorialGroup1][i] == data.subjects[subject1][tutorialGroup1][j])) {
					result = true;
				}
			}
		}
		return result;
	}

}