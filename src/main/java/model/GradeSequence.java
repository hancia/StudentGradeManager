package model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity
public class GradeSequence{
	@Id
	private String className;
	private int counter = 1;

	public GradeSequence(){}
	public GradeSequence(String className) {

		this.className = className;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
