package com.docstorage.docapp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docstorage.docapp.domain.Document;

public interface DocRepo extends JpaRepository<Document, Long> {
	List<Document> findByAuthorId (Long authorId);

	Document findByNameAndFileType(String name, String fileType);
	List<Document> findByName(String name);
}
