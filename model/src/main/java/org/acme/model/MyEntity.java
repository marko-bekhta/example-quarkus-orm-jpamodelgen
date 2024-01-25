package org.acme.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class MyEntity {
	@Id
	public Long id;
	public String string;
	public int integer;
	public LocalDateTime dateTime;

	public MyEntity() {
	}

	public MyEntity(Long id, String string, int integer, LocalDateTime dateTime) {
		this.id = id;
		this.string = string;
		this.integer = integer;
		this.dateTime = dateTime;
	}
}
