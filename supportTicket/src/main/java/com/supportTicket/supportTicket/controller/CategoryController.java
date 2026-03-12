package com.supportTicket.supportTicket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.service.CategoryService;


@RestController
@RequestMapping("/api")
public class CategoryController {
	@Autowired
	CategoryService catService;
	
	@PostMapping("/category")
	public ResponseEntity<CategoryRecord> createCat(@RequestBody CategoryRecord cateRe){
		CategoryRecord response = catService.createCategory(cateRe);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}
}
