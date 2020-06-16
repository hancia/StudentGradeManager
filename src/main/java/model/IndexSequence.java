package model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity
public class IndexSequence {
	@Id
	private String className;
	private Long counter = 3L;

	public IndexSequence(){}
	public IndexSequence(String className) {

		this.className = className;
	}

	public Long getCounter() {
		return counter;
	}

	public void setCounter(Long counter) {
		this.counter = counter;
	}
}
