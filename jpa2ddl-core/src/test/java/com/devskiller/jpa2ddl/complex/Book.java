package com.devskiller.jpa2ddl.complex;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import java.util.HashSet;
import java.util.Set;

@Entity
class Book {

	@Id
	private Long id;

	@ElementCollection
	@JoinColumn
	@OnDelete(action = OnDeleteAction.CASCADE)
	@CollectionTable(indexes = {@Index(name = "fk_book_chapter", columnList = "book_id")})
	public final Set<Chapter> chapters = new HashSet<>();

}
