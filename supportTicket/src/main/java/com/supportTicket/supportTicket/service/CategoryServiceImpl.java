package com.supportTicket.supportTicket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	CategoryRepo categoryRepo;
	
	public CategoryRecord createCategory(CategoryRecord catRec) {
		if(categoryRepo.findByCategoryName(catRec.categoryName()) == null) {
			Category category = new Category();
			category.setCategoryName(catRec.categoryName());
			categoryRepo.save(category);
			return catRec;
		}else {
			throw new ElementAlreadyExistsException("This category already exists");
		}
	}
}
