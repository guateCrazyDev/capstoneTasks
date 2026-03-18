package com.supportTicket.supportTicket.service;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

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
				category.setDescription(catRec.description());
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
				CategoryRecord catR = new CategoryRecord(cat.getCategoryName(),cat.getDescription(),cat.getImg());
				catsRec.add(catR);
			}
			return catsRec.stream().sorted(new CategoryNameComparator()).toList();
		}else {
			throw new ElementNotFoundException("There is no Categories");
		}
	}
	
	public CategoryRecord getCategory(String categoryName) {
		Category category = categoryRepo.findByCategoryName(categoryName);
		if(category != null) {
			return new CategoryRecord(category.getCategoryName(),category.getDescription(),category.getImg());
		}else {
			throw new ElementNotFoundException("This category not exists");
		}
	}
	
	public void deleteCat(String cat) {
		if(categoryRepo.findByCategoryName(cat) != null) {
			categoryRepo.delete(categoryRepo.findByCategoryName(cat));
		}else {
			throw new ElementNotFoundException("This category not exists");
		}
	}
	
	public void updateCat(CategoryRecord category,MultipartFile file,String prevName) {
		if(categoryRepo.findByCategoryName(prevName) != null) {
			if(file == null) {
				Category cat = categoryRepo.findByCategoryName(prevName);
				cat.setCategoryName(category.categoryName());
				cat.setDescription(category.description());
				categoryRepo.save(cat);
			}else {
				try {
					Category cat = categoryRepo.findByCategoryName(prevName);
					cat.setCategoryName(category.categoryName());
					cat.setDescription(category.description());
					cat.setImg(file.getBytes());
					categoryRepo.save(cat);
				}catch(Exception e) {
					throw new RuntimeErrorException(null, e.getMessage());
				}
			}
		}else {
			throw new ElementNotFoundException("This category not exists");
		}
	}
}
