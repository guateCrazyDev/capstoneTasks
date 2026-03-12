package com.supportTicket.supportTicket.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.comparators.CategoryNameComparator;
import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.exceptions.ImageNotFoundException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	CategoryRepo categoryRepo;
	
	public CategoryRecord createCategory(CategoryRecord catRec,MultipartFile img) {
		if(categoryRepo.findByCategoryName(catRec.categoryName()) == null) {
			try {
				Category category = new Category();
				category.setCategoryName(catRec.categoryName());
				category.setImg(img.getBytes());
				categoryRepo.save(category);
				return catRec;
			}catch(Exception e) {
				throw new ImageNotFoundException("It was not possible to process the image");
			}
		}else {
			throw new ElementAlreadyExistsException("This category already exists");
		}
	}
	
	public List<CategoryRecord> getAlls(){
		List<Category> categories = categoryRepo.findAll();
		if(categories.size() > 0) {
			List<CategoryRecord> catsRec = new ArrayList();
			for(Category cat : categories) {
				CategoryRecord catR = new CategoryRecord(cat.getCategoryName(),cat.getImg());
				catsRec.add(catR);
			}
			return catsRec.stream().sorted(new CategoryNameComparator()).toList();
		}else {
			throw new ElementNotFoundException("There is no Categories");
		}
	}
}
