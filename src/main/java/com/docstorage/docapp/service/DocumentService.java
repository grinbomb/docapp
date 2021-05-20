package com.docstorage.docapp.service;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.docstorage.docapp.domain.Document;
import com.docstorage.docapp.domain.SharedDocument;
import com.docstorage.docapp.domain.User;
import com.docstorage.docapp.repos.DocRepo;
import com.docstorage.docapp.repos.ShrdDocRepo;

@Service
@Transactional
public class DocumentService {

	@Value("${upload.path}")
	private String uploadPath;

	@Autowired
	private ShrdDocRepo shrdDocRepo;

	@Autowired
	private DocRepo docRepo;

	public void saveSomeDocs(List<Document> docs, List<User> users, List<Document> readOnlyDocs) {
		for (Document doc : docs) {
			for (User user : users) {
				if (doc != null && user != null) {
					List<SharedDocument> userShrdDocs = shrdDocRepo.findByUserId(user.getId());
					List<Document> userDocs = new ArrayList<Document>();
					for (SharedDocument shrdoc : userShrdDocs) {
						userDocs.add(shrdoc.getDocument());
					}
					boolean readOnlySelect;

					if (readOnlyDocs.contains(doc))
						readOnlySelect = true;
					else
						readOnlySelect = false;

					if (!userDocs.contains(doc))
						shrdDocRepo.save(new SharedDocument(doc, user, readOnlySelect));
					else {
						shrdDocRepo.delete(shrdDocRepo.findByUserAndDocument(user, doc));
						shrdDocRepo.save(new SharedDocument(doc, user, readOnlySelect));
					}

				}
			}
		}
	}

	public String createFile(Document document, User user) {

		StringBuilder sb = new StringBuilder();

		File file = new File(uploadPath + document.getName() + document.getFileType());
		List<SharedDocument> listShd = shrdDocRepo.findByUserId(user.getId());
		boolean canChange = false;
		for (SharedDocument sharedDocument : listShd) {
			if (sharedDocument.getDocument().getName().equals(document.getName())
					&& sharedDocument.getDocument().getFileType().equals(document.getFileType())
					&& !sharedDocument.isReadOnly())
				canChange = true;
		}

		Document docCheck = docRepo.findByNameAndFileType(document.getName(), document.getFileType());
		if (docCheck != null)
			if (docCheck.getAuthor() != null && !docCheck.getAuthor().getId().equals(user.getId()))
				if (file.exists() && !canChange)
					return "";

		if (!document.getFileType().equals(".pdf")) {

			try {

				FileWriter fileWriter = new FileWriter(file, false);

				if (!file.exists())
					file.createNewFile();

				fileWriter.write(document.getBody());
				fileWriter.close();

				DataInputStream input = new DataInputStream(new FileInputStream(file));
				try {
					while (true) {
						sb.append(Integer.toBinaryString(input.readByte()));
					}
				} catch (EOFException eof) {
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					input.close();
				}
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {

			PDDocument doc = new PDDocument();

			if (!file.exists()) {
				PDPage newPage = new PDPage();
				doc.addPage(newPage);
				try {
					doc.save(file);
					doc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			try {
				doc = PDDocument.load(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				PDPage page = doc.getPage(0);
				PDPageContentStream contentStream = new PDPageContentStream(doc, page);
				contentStream.beginText();
				contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
				contentStream.newLineAtOffset(25, 500);
				contentStream.showText(document.getBody());
				contentStream.endText();
				contentStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				doc.save(file);
				doc.close();

				DataInputStream input = new DataInputStream(new FileInputStream(file));
				try {
					while (true) {
						sb.append(Integer.toBinaryString(input.readByte()));
					}
				} catch (EOFException eof) {
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					input.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return sb.toString();
	}

	public List<SharedDocument> findByUserIdShared(User user) {
		return shrdDocRepo.findByUserId(user.getId());
	}

	public List<Document> findByAuthorId(User user) {
		return docRepo.findByAuthorId(user.getId());
	}

	public void save(Document document) {
		docRepo.save(document);

	}

	public Optional<Document> findById(Long id) {
		return docRepo.findById(id);
	}

	public void deleteById(Long id) {
		docRepo.deleteById(id);
	}

	public Document findByNameAndFileType(String name, String fileType) {

		return docRepo.findByNameAndFileType(name, fileType);
	}

	public void deleteByIdShared(Long id) {
		shrdDocRepo.deleteByDocumentId(id);

	}

	public List<Document> findByName(String name) {
		return docRepo.findByName(name);
	}
	
	public List<Document> findByNameAndAuthorId(String name, Long id) {
		return docRepo.findByNameAndAuthorId(name, id);
	}
	
	public List<SharedDocument> findByDocumentNameAndUserShared(String documentName, User user) {
		return shrdDocRepo.findByDocumentNameAndUser(documentName, user);
	}

	public SharedDocument findByUserAndDocumentShared(User user, @Valid Document document) {
		return shrdDocRepo.findByUserAndDocument(user, document);
	}

}
