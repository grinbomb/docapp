package com.docstorage.docapp.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.docstorage.docapp.domain.Document;
import com.docstorage.docapp.domain.User;
import com.docstorage.docapp.repos.DocRepo;
import com.docstorage.docapp.repos.ShrdDocRepo;
import com.docstorage.docapp.repos.UserRepo;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class DocumentServiceTest {

	@MockBean
	private ShrdDocRepo shrdDocRepo;
	
	@MockBean
	private UserRepo userRepo;
	
	@MockBean
	private DocRepo docRepo;

	@Autowired
	private DocumentService docService;

	
	@Test
	public void saveSomeDocsTest() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedDate = dateFormat.parse(dateFormat.format(new Date()));
		Timestamp todayDate = new Timestamp(parsedDate.getTime());

		String arr = new String("1111111");

		User user = new User();
		user.setId(1L);
		User user2 = new User();
		user2.setId(2L);
		
		Document doc1 = new Document("file1", "txt", todayDate, "123123", arr, user);
		Document doc2 = new Document("file1", "txt", todayDate, "123123", arr, user);
		
		List<Document> docs = new ArrayList<Document>();
		docs.add(doc1);
		docs.add(doc2);
		
		List<User> users = new ArrayList<User>();
		users.add(user);
		users.add(user2);
		
		userRepo.save(user);
		userRepo.save(user2);
		
		docRepo.save(doc1);
		docRepo.save(doc2);
		
		docService.saveSomeDocs(docs, users, docs);
		
		Mockito.verify(shrdDocRepo, Mockito.times(4)).save(ArgumentMatchers.any());
		Mockito.verify(shrdDocRepo, Mockito.times(4)).findByUserId(ArgumentMatchers.any());
		
	}

}
