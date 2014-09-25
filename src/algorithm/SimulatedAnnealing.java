package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulatedAnnealing {

	public InitialSchedule initialSchedule = new InitialSchedule();

	//variables for chosen subject and tutorial
	private int subject;
	private int tutorialGroup;

	// probability to choose bad solution
	private float p = 1f;
	// cool down decrement
	private float deltaP = 0.1f;
	private float cutOff = 0.2f;
	private int testCount = 10000;

	// test
	public static void main(String[] args) {
		SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing();
		simulatedAnnealing.simulatedAnnealing();
		simulatedAnnealing.initialSchedule.debug();
	}

	/**
	 * Select a subject and a tutorial group
	 */
	private void selectTutorialGroup() {
		subject = (int) Math.random() * initialSchedule.data.subjects.length;
		tutorialGroup = (int) (Math.random() + 1);

	}

	/**
	 * simulated annealing, try to get a better solution
	 */
	public void simulatedAnnealing() {

		while (p > cutOff) {
			List<Operation> operationList = new ArrayList<Operation>();
			// choose between 1 and 3 slots to move/switch
			int slotCount = (int) Math.floor(1 + 2 * Math.random());

			for (int j = 0; j < testCount + 1; j++) {

				Operation operation;

				// randomly choose between move and switch
				if (Math.random() > 0.5) {
					operation = moveTutorial(slotCount);
				} else {
					operation = switchTutorial(slotCount);
				}
				// add operation to list
				operationList.add(operation);
			}

			// sort operations by conflicts
			Collections.sort(operationList);

			// get one of the good operations
			Operation selected = operationList.get((int) (testCount * p));

			// if operation is good
			if ((float) selected.rating <= (float) initialSchedule
					.globalConflictRating() * (1f + p)) {
				// permanently write operation
				selected.write();
			}
			p -= deltaP;
		}
	}

	/**
	 * build a move operation
	 * 
	 * @param slotCount
	 * @return
	 */
	private Operation moveTutorial(int slotCount) {
		selectTutorialGroup();

		Operation operation = new Operation(this);

		operation.count = slotCount;
		operation.mode = 0;
		operation.subject1 = subject;
		operation.tutorialGroup1 = tutorialGroup;

		int start = (int) (Math.random() * 3);
		for (int i = 0; i < slotCount; i++) {

			// Slots anhand von anzahlSlots zufällig wählen
			int newSlot = (int) (Math.random() * Data.SLOT_COUNT);

			int tutorial = (start + i) % 3;

			operation.operation[i][0][0] = tutorial;
			operation.operation[i][0][1] = initialSchedule.data.subjects[subject][tutorialGroup][tutorial];
			operation.operation[i][1][0] = tutorial;
			operation.operation[i][1][1] = newSlot;

		}

		return operation;

	}

	/**
	 * build a switch operation
	 * 
	 * @param slotCount
	 * @return
	 */
	private Operation switchTutorial(int slotCount) {
		selectTutorialGroup();

		// build operation
		Operation operation = new Operation(this);
		operation.count = slotCount;
		operation.mode = 1;
		operation.subject1 = subject;
		operation.tutorialGroup1 = tutorialGroup;

		// get first tutorial to switch
		int start = (int) (Math.random() * 3);
		// choose another subject and tutorialGroup
		int randomSubject = (int) (Math.random() * Data.SUBJECT_COUNT);
		int randomTutorialGroup = (int) (1 + Math.random() * 2);

		operation.subject2 = randomSubject;
		operation.tutorialGroup2 = randomTutorialGroup;

		for (int i = 0; i < slotCount; i++) {

			int randomTutorial = (int) (Math.random() * 3);

			int tutorial = (start + i) % 3;

			// subject1
			// tutorial index
			operation.operation[i][0][0] = tutorial;
			// tutorial value
			operation.operation[i][0][1] = initialSchedule.data.subjects[subject][tutorialGroup][tutorial];
			// subject2
			// tutorial index
			operation.operation[i][1][0] = randomTutorial;
			// tutorial value
			operation.operation[i][1][1] = initialSchedule.data.subjects[randomSubject][randomTutorialGroup][randomTutorial];

		}

		return operation;

	}

}