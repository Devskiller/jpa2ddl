package com.devskiller.hbm2ddl.sample;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
class User {

	@Id
	private long id;

	private Date date;

	private String email;

}
