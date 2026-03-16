package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CategoryRecord;

public interface CategoryService {

	CategoryRecord createCategory(CategoryRecord catRec, MultipartFile img);

	List<CategoryRecord> getAlls();

	public void deleteCategory(String name);

	CategoryRecord updateCategory(String name, CategoryRecord catRec, MultipartFile img);

}