package com.docstorage.docapp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docstorage.docapp.domain.Document;
import com.docstorage.docapp.domain.SharedDocument;
import com.docstorage.docapp.domain.User;

public interface ShrdDocRepo extends JpaRepository<SharedDocument, Long>{

	List<SharedDocument> findByUserId(Long userId);
	SharedDocument findByUserAndDocument(User user, Document document);
	void deleteByDocumentId(Long documentId);
	List<SharedDocument> findByDocumentNameAndUser(String name, User user);
}
