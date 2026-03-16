package com.supportTicket.supportTicket.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.service.CategoryService;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	CategoryService catService;

	@PostMapping("/category")
	public ResponseEntity<CategoryRecord> createCat(
			@RequestParam("categoryData") String categoryDataJson,
			@RequestParam("img") MultipartFile img) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		CategoryRecord cateRe = objectMapper.readValue(categoryDataJson, CategoryRecord.class);

		CategoryRecord response = catService.createCategory(cateRe, img);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/category")
	public ResponseEntity<List<CategoryRecord>> getAllCategories() {

		List<CategoryRecord> response = catService.getAlls();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}