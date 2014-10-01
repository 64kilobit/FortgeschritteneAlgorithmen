package algorithm;

/**
 * Class for representation of a move or switch operation
 *
 */
public class Operation implements Comparable<Operation> {

	// subjct 1
	public int subject1;
	public int tutorialGroup1;

	// subject 2 to switch with
	public int subject2;
	public int tutorialGroup2;

	// operation
	// operation[0..2=operiation][0=tutorial1, 1=tutorial2][0=tutorialIndex,
	// 1=slotValue]
	public int[][][] operation = new int[3][2][2];

	// rating for this operation
	public int rating;

	public SimulatedAnnealing simulatedAnnealing;

	// how much elements do we want to move
	int count;
	// 0 for move, 1 for switch
	int mode;

	public Operation(SimulatedAnnealing simulatedAnnealing) {
		super();
		this.simulatedAnnealing = simulatedAnnealing;
	}

	/**
	 * write an operation
	 */
	public void write() {
		for (int i = 0; i < count; i++) {
			if (mode == 0) {
				// move
				simulatedAnnealing.initialSchedule.data.subjects[subject1][tutorialGroup1][operation[i][0][0]] = operation[i][1][1];

			} else {
				// switch
				simulatedAnnealing.initialSchedule.data.subjects[subject1][tutorialGroup1][operation[i][0][0]] = operation[i][1][1];
				simulatedAnnealing.initialSchedule.data.subjects[subject2][tutorialGroup2][operation[i][1][0]] = operation[i][0][1];
			}
		}
	}

	/**
	 * undo an operation
	 */
	public void undo() {
		for (int i = 0; i < count; i++) {
			if (mode == 0) {
				// move back
				simulatedAnnealing.initialSchedule.data.subjects[subject1][tutorialGroup1][operation[i][0][0]] = operation[i][0][1];
			} else {
				// switch back
				simulatedAnnealing.initialSchedule.data.subjects[subject1][tutorialGroup1][operation[i][0][0]] = operation[i][0][1];
				simulatedAnnealing.initialSchedule.data.subjects[subject2][tutorialGroup2][operation[i][1][0]] = operation[i][1][1];
			}
		}
	}

	public void computeRating() {
		write();
		rating = simulatedAnnealing.initialSchedule.globalConflictRating();
		undo();
	}

	@Override
	public int compareTo(Operation o) {
		return rating - o.rating;
	}

	@Override
	public String toString() {
		return "rating: " + rating;
	}
}
