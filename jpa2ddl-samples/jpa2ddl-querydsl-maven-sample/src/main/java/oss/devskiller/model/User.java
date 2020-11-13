package oss.devskiller.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
class User {

	@Id
	private Long id;

	private String name;

	private String email;

	private int age;

}
