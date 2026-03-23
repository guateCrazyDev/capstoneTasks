package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.service.CategoryService;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping(value = "/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CategoryRecord> createCat(
			@RequestPart("categoryData") CategoryRecord category,
			@RequestPart("img") MultipartFile img) {

		CategoryRecord response = categoryService.createCategory(category, img);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/category")
	public ResponseEntity<List<CategoryRecord>> getAllCategories() {

		List<CategoryRecord> response = categoryService.getAlls();

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/category/{name}")
	public ResponseEntity<Void> deleteCategory(@PathVariable String name) {

		categoryService.deleteCategory(name);

		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = "/category/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CategoryRecord> updateCategory(
			@PathVariable String name,
			@RequestPart("categoryData") CategoryRecord category,
			@RequestPart(value = "img", required = false) MultipartFile img) {

		CategoryRecord response = categoryService.updateCategory(name, category, img);

		return ResponseEntity.ok(response);
	}
}