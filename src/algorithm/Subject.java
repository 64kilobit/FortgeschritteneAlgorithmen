package algorithm;

/*
 * Represents one Subject, with SubjectId and Population */
public class Subject implements Comparable<Subject> {
	public int id;
	public int population;

	@Override
	public String toString() {
		return "SubjectId: " + id + ", Poulation:" + population;
	}

	public Subject(int id, int population) {
		super();
		this.id = id;
		this.population = population;
	}

	@Override
	public int compareTo(Subject o) {
		return population - o.population;
	}

}
