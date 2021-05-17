package com.docstorage.docapp.controller;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docstorage.docapp.domain.Document;
import com.docstorage.docapp.domain.SharedDocument;
import com.docstorage.docapp.domain.User;
import com.docstorage.docapp.service.DocumentService;
import com.docstorage.docapp.service.UserService;

@Controller
public class MainDocumentController {

	@Value("${upload.path}")
	private String uploadPath;

	@Autowired
	private UserService userService;

	@Autowired
	private DocumentService docService;

	@GetMapping("/")
	public String main(@RequestParam(required = false) String filtertag, @ModelAttribute("docName") String docName,
			@ModelAttribute("docType") String docType, @ModelAttribute("docBody") String docBody,
			@AuthenticationPrincipal User user, Model model) throws UnsupportedEncodingException {

		model.addAttribute("setTXT", "");
		model.addAttribute("setDOC", "");
		model.addAttribute("setPDF", "");

		if (!docName.isEmpty() && !docType.isEmpty() && !docBody.isEmpty()) {
			Document docForChange = new Document(docName, docType, null, docBody, null, user);
			model.addAttribute("document", docForChange);

			if (docType.equals(".txt"))
				model.addAttribute("setTXT", "selected");
			else if (docType.equals(".docx"))
				model.addAttribute("setDOC", "selected");
			else if (docType.equals(".pdf"))
				model.addAttribute("setPDF", "selected");
		}

		if (user != null) {

			List<Document> docs = docService.findByAuthorId(user);
			List<User> users = userService.findAll();
			List<SharedDocument> shareddocs = docService.findByUserIdShared(user);

			if (filtertag != null && filtertag != "") {
				model.addAttribute("filtertag", filtertag);
				docs = docService.findByNameAndAuthorId(filtertag, user.getId());
				shareddocs = docService.findByDocumentNameAndUserShared(filtertag, user);
			}else {
				model.addAttribute("filtertag", "");
			}

			if (!users.isEmpty())
				users.remove(userService.findById(user.getId()).get());

			docs.sort(Comparator.comparing(Document::getDate));

			if (!docs.isEmpty()) {
				model.addAttribute("docs", docs);
				model.addAttribute("users", users);
			}

			model.addAttribute("name", user.getUsername());

			if (!shareddocs.isEmpty()) {
				model.addAttribute("shareddocs", shareddocs);
			}

			return "main";
		} else
			return "redirect:/login";
	}

	@PostMapping("/")
	public String createdoc(@AuthenticationPrincipal User user, @Valid Document document, BindingResult bindingResult,
			Model model) throws ParseException {

		model.addAttribute("setTXT", "");
		model.addAttribute("setDOC", "");
		model.addAttribute("setPDF", "");

		if (document.getFileType().equals(".txt"))
			model.addAttribute("setTXT", "selected");
		else if (document.getFileType().equals(".docx"))
			model.addAttribute("setDOC", "selected");
		else if (document.getFileType().equals(".pdf"))
			model.addAttribute("setPDF", "selected");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedDate = dateFormat.parse(dateFormat.format(new Date()));
		Timestamp todayDate = new Timestamp(parsedDate.getTime());

		String fileBinary = docService.createFile(document, user);

		Document docForDB = docService.findByNameAndFileType(document.getName(), document.getFileType());

		List<SharedDocument> listShd = docService.findByUserIdShared(user);
		boolean canChange = false;
		for (SharedDocument sharedDocument : listShd) {
			if (sharedDocument.getDocument().getName().equals(document.getName())
					&& sharedDocument.getDocument().getFileType().equals(document.getFileType())
					&& !sharedDocument.isReadOnly())
				canChange = true;
		}
		
		if (docForDB != null) {
			docForDB.setBody(document.getBody());

			if (docForDB.getAuthor() == null)
				docForDB.setAuthor(user);
			else if(docForDB.getAuthor().getId().equals(user.getId())||canChange)
				model.addAttribute("messageDoc", "The document successfully modified.");
			else
				model.addAttribute("errorChangeFile",
					"The file already exists and you do not have permission to modify it");

		} else {
			model.addAttribute("messageDoc", "The document successfully created.");
			document.setAuthor(user);
			docForDB = document;
		}

		docForDB.setDate(todayDate);
		docForDB.setBinaryFile(fileBinary.toString().getBytes());

		if (bindingResult.hasErrors()) {
			model.addAttribute("messageDoc", null);
			Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
			model.mergeAttributes(errors);
		} else {
			if(docForDB.getAuthor().getId().equals(user.getId())||canChange)
				docService.save(docForDB);
		}

		List<Document> docs = docService.findByAuthorId(user);
		List<User> users = userService.findAll();

		users.remove(userService.findById(user.getId()).get());

		if (docs != null) {
			model.addAttribute("docs", docs);
			model.addAttribute("users", users);
		}

		List<SharedDocument> shareddocs = docService.findByUserIdShared(user);

		if (!shareddocs.isEmpty()) {
			model.addAttribute("shareddocs", shareddocs);
		}

		return "main";
	}

	@PostMapping("/share")
	public String shareDocs(@RequestParam(required = false) String[] checkeddocs,
			@RequestParam(required = false) String[] checkedusers, @RequestParam(required = false) String[] readOnly) {
		if (checkeddocs != null && checkedusers != null) {
			List<Document> docs = new ArrayList<Document>();
			if (checkeddocs.length == 1)
				docs.add(docService.findById(Long.parseLong(checkeddocs[0])).get());
			else {
				for (int i = 0; i < checkeddocs.length; i++) {
					docs.add(docService.findById(Long.parseLong(checkeddocs[i])).get());
				}
			}

			List<User> users = new ArrayList<User>();
			if (checkedusers.length == 1)
				users.add(userService.findById(Long.parseLong(checkedusers[0])).get());
			else {
				for (int i = 0; i < checkedusers.length; i++) {
					users.add(userService.findById(Long.parseLong(checkedusers[i])).get());
				}
			}

			List<Document> readOnlyDocs = new ArrayList<Document>();
			if (readOnly != null) {
				if (readOnly.length == 1)
					readOnlyDocs.add(docService.findById(Long.parseLong(readOnly[0])).get());
				else {
					for (int i = 0; i < readOnly.length; i++) {
						readOnlyDocs.add(docService.findById(Long.parseLong(readOnly[i])).get());
					}
				}
			}

			docService.saveSomeDocs(docs, users, readOnlyDocs);

		}
		return "redirect:/";

	}

	@GetMapping("/changefile/{id}")
	public String changeDoc(RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user,
			@PathVariable("id") Document doc, Model model) {

		redirectAttributes.addFlashAttribute("docName", doc.getName());
		redirectAttributes.addFlashAttribute("docType", doc.getFileType());
		redirectAttributes.addFlashAttribute("docBody", doc.getBody());

		return "redirect:/";

	}

	@GetMapping("/deletefile/{id}")
	public String deleteDoc(@AuthenticationPrincipal User user, @PathVariable("id") Document doc, Model model) {

		if (user.getId().equals(doc.getAuthor().getId())) {
			File file = new File(uploadPath + doc.getName() + doc.getFileType());
			file.delete();
			docService.deleteByIdShared(doc.getId());
			docService.deleteById(doc.getId());
		}
		return "redirect:/";

	}

	@GetMapping("/openfile/{id}")
	public ResponseEntity<byte[]> getPDF1(@PathVariable("id") Document document) {

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
		String filename = document.getName() + document.getFileType();

		headers.add("content-disposition", "inline;filename=" + filename);

		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

		StringBuilder sb = new StringBuilder();
		File file = new File(uploadPath + document.getName() + document.getFileType());
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
		String fileBinary = sb.toString();

		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(fileBinary.toString().getBytes(), headers,
				HttpStatus.OK);
		return response;
	}

}