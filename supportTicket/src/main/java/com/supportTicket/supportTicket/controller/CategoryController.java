package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.service.CategoryService;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:5173")
public class CategoryController {
	@Autowired
	CategoryService catService;

	@PostMapping("/category")
	public ResponseEntity<CategoryRecord> createCat(@RequestPart("categoryData") CategoryRecord cateRe,
			@RequestPart("img") MultipartFile img) {
		CategoryRecord response = catService.createCategory(cateRe, img);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/category")
	public ResponseEntity<List<CategoryRecord>> getAllCategories() {
		List<CategoryRecord> response = catService.getAlls();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryName}")
	public ResponseEntity<CategoryRecord> getCategory(@PathVariable String categoryName) {
		CategoryRecord response = catService.getCategory(categoryName);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
