package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulatedAnnealing {

	public InitialSchedule initialSchedule = new InitialSchedule();

	// variables for chosen subject and tutorial
	private int subject;
	private int tutorialGroup;

	// tempareture, probability to choose bad solution
	private float temperature = 1f;
	// cool down factor
	private float coolingFactor = 0.99f;
	// stop temperature
	private float cutOff = 0.001f;
	private int testPopulationCount = 1000;
	// 0 for pick best,
	// (0,1] for pick (pickFactor * temperature * testPopulationCount)
	private float pickFactor = 0.1f;

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
	 * Simulated annealing, try to get a better solution, complexity
	 * O(ln(cutOff) / ln(coolingFactor) * ( testPopulationCount *( 1 +
	 * Time(moveTutorial) + ln(testPopulationCount) +
	 * Time(globalConflictRating)))
	 */
	public void simulatedAnnealing() {
		int globalConflicts = -1;
		int iteration = 0;

		// stop if cold enough or zero conflict solution is found
		while (temperature > cutOff && !(globalConflicts == 0)) {
			List<Operation> operationList = new ArrayList<Operation>();
			// choose between 1 and 3 slots to move/switch
			int slotCount = (int) Math.floor(1 + 2 * Math.random());

			for (int j = 0; j < testPopulationCount + 1; j++) {

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
			Operation selected = operationList.get((int) (pickFactor
					* temperature * (float) testPopulationCount));
			// if operation is good, write it
			if ((float) selected.rating <= (float) initialSchedule
					.globalConflictRating() * (1f + temperature)) {
				// permanently write operation
				selected.write();
			}
			globalConflicts = initialSchedule.globalConflict();
			System.out.format("Iteration %d, temperature: %f, conflicts: %d%n",
					iteration, temperature, globalConflicts);
			iteration++;
			// cooling
			temperature *= coolingFactor;

		}
	}

	/**
	 * Build a move operation, complexity O(SLOT_COUNT)
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

			// pick a random slot
			int newSlot = (int) (Math.random() * Data.SLOT_COUNT);
			int tutorial = (start + i) % 3;
			// tutorial index
			operation.operation[i][0][0] = tutorial;
			// tutorial value
			operation.operation[i][0][1] = initialSchedule.data.subjects[subject][tutorialGroup][tutorial];
			// tutorial index
			operation.operation[i][1][0] = tutorial;
			// tutorial new slot
			operation.operation[i][1][1] = newSlot;

		}
		operation.computeRating();
		return operation;

	}

	/**
	 * Build a switch operation, complexity O(SLOT_COUNT)
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
		operation.computeRating();
		return operation;

	}

}