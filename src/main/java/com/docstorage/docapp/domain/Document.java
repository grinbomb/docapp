package com.docstorage.docapp.domain;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Entity
public class Document {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank(message = "File type cannot be empty")
	private String fileType;
	
	@NotBlank(message = "Name cannot be empty")
	private String name;
	
	private Timestamp date;
	
	@NotBlank(message = "Description cannot be empty")
	private String body;

	private String binaryFile;
	
	@ManyToOne
	@JoinColumn(name = "authorId")
	private User author;
	
	public Document() {
	}

	public Document(String name, String filetype, Timestamp date, String body, String binaryFile, User author) {
		if(!filetype.isEmpty())
			this.fileType = "."+filetype;
		else
			this.fileType = "";
		this.name = name;
		this.date = date;
		this.body = body;
		this.binaryFile = binaryFile;
		this.author = author;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		if(!fileType.isEmpty())
			this.fileType = "."+fileType;
		else
			this.fileType = "";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
			this.name = name;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBinaryFile() {
		return binaryFile;
	}
	public void setBinaryFile(String binaryFile) {
		this.binaryFile = binaryFile;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
}
