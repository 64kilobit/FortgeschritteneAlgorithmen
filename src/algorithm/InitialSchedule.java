package algorithm;

import java.util.Arrays;

public class InitialSchedule {
	private Data data = new Data();

	public int[][][] subjects = new int[Data.SUBJECT_COUNT][3][2];

	public static void main(String[] args) {
		InitialSchedule initialSchedule = new InitialSchedule();
		initialSchedule.verteileUebung();
		System.out.println(Arrays.deepToString(initialSchedule.subjects));

	}

	private void init() {
		// for (int[][] subject : subjects) {
		// for (int[] �bung : subject) {
		// Arrays.fill(�bung, -1);
		// }
		// }

	}

	private void verteileUebung() {
		init();
		System.out.println(Arrays.deepToString(subjects));
		System.out.println("verteile �bungen gestartet");
		data.loadCombinations();
		while (data.subjectPopulation.size() > 0) {
			System.out.println(data.subjectPopulation.size());
			int maxFachId = data.subjectPopulation.get(0).id;
			data.subjectPopulation.remove(0);
			System.out.println("fach " + maxFachId);
			for (int i = 0; i < 2; i++) {
				subjects[maxFachId][1][i] = findBestSlot(maxFachId, 1, i);
				subjects[maxFachId][2][i] = findBestSlot(maxFachId, 2, i);
			}
		}
	}

	private int findBestSlot(int fach, int �bungsGruppe, int �bung) {
		System.out.println("finde besten Slot f�r fach " + fach + "  Gruppe "
				+ �bungsGruppe + " �bung " + �bung);
		int minKonfliktSlot = 3;
		int minKonfliktAnzahl = globalConflict();
		// Teste alle Slot Nummern
		for (int i = 0; i < Data.SLOT_COUNT; i++) {

			subjects[fach][�bungsGruppe][�bung] = i;
			int konfliktAnzahl = globalConflict();
			// wenn Slot weniger Konflikte veraursacht, dann merke Slot
			if (konfliktAnzahl < minKonfliktAnzahl) {
				minKonfliktSlot = i;
				minKonfliktAnzahl = konfliktAnzahl;
				System.out.println(minKonfliktSlot);
			}
		}
		// resette Slot, wir haben ja nur getestet
//		subjects[fach][�bungsGruppe][�bung] = -1;
		System.out.println("bester Slot f�r fach " + fach + "  Gruppe "
				+ �bungsGruppe + " �bung " + �bung + " ist " + minKonfliktSlot
				+ "  mit " + minKonfliktAnzahl + " konflikten");
		return minKonfliktSlot;
	}

	private int globalConflict() {
		int konfliktAnzahl = 0;
		for (int fach1 = 0; fach1 < subjects.length; fach1++) {
			for (int fach2 = 0; fach2 < subjects.length; fach2++) {
				if (fach1 != fach2)
					konfliktAnzahl += uebungsKonflikt(fach1, fach2)
							* data.combinations[fach1][fach2];
			}
		}
		return konfliktAnzahl;
	}

	private int uebungsKonflikt(int fach1, int fach2) {
		// �bungskonflikte
		if (konflikt(fach1, fach2, 1, 1) && konflikt(fach1, fach2, 1, 2)
				&& konflikt(fach1, fach2, 2, 1) && konflikt(fach1, fach2, 2, 2)) {
			// wir haben unausweichlichen konflikt
			return 1;
		} else {
			return 0;
		}
	}

	private boolean konflikt(int fach1, int fach2, int ubungsGruppe1,
			int ubungsGruppe2) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (subjects[fach1][ubungsGruppe1][i] == subjects[fach2][ubungsGruppe2][j])
					return true;
			}
		}
		return false;
	}
}