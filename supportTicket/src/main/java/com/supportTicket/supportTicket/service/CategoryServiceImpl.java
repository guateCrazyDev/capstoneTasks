package com.supportTicket.supportTicket.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.comparators.CategoryNameComparator;
import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;

	@Override
	public List<CategoryRecord> getAlls() {

		List<Category> categories = categoryRepo.findAll();

		if (categories.isEmpty()) {
			throw new ElementNotFoundException("No categories found");
		}

		List<CategoryRecord> records = new ArrayList<>();

		for (Category cat : categories) {

			CategoryRecord record = new CategoryRecord(
					cat.getCategoryName(),
					cat.getDescription(),
					cat.getImagePath());

			records.add(record);
		}

		return records.stream()
				.sorted(new CategoryNameComparator())
				.toList();
	}

	@Override
	public CategoryRecord createCategory(CategoryRecord catRec, MultipartFile img) {

		Category existing = categoryRepo.findByCategoryName(catRec.categoryName());

		if (existing != null) {
			throw new ElementAlreadyExistsException("Category already exists: " + catRec.categoryName());
		}

		Category category = new Category();

		category.setCategoryName(catRec.categoryName());
		category.setDescription(catRec.description());

		try {

			if (img != null && !img.isEmpty()) {

				String uploadDir = System.getProperty("user.dir") + "/uploads/categories/";

				String fileName = UUID.randomUUID() + "_" + img.getOriginalFilename();

				Path path = Paths.get(uploadDir + fileName);

				Files.createDirectories(path.getParent());
				Files.write(path, img.getBytes());

				category.setImagePath(fileName);
			}

			categoryRepo.save(category);

			return new CategoryRecord(
					category.getCategoryName(),
					category.getDescription(),
					category.getImagePath());

		} catch (Exception e) {
			throw new RuntimeException("Error saving image");
		}
	}

	@Override
	public void deleteCategory(String name) {

		Category category = categoryRepo.findByCategoryName(name);

		if (category == null) {
			throw new ElementNotFoundException("Category not found: " + name);
		}

		categoryRepo.delete(category);
	}

	@Override
	public CategoryRecord updateCategory(String name, CategoryRecord catRec, MultipartFile img) {

		Category category = categoryRepo.findByCategoryName(name);

		if (category == null) {
			throw new ElementNotFoundException("Category not found: " + name);
		}

		category.setCategoryName(catRec.categoryName());
		category.setDescription(catRec.description());

		try {

			if (img != null && !img.isEmpty()) {

				String uploadDir = System.getProperty("user.dir") + "/uploads/categories/";

				String fileName = UUID.randomUUID() + "_" + img.getOriginalFilename();

				Path path = Paths.get(uploadDir + fileName);

				Files.createDirectories(path.getParent());
				Files.write(path, img.getBytes());

				category.setImagePath(fileName);
			}

			categoryRepo.save(category);

			return new CategoryRecord(
					category.getCategoryName(),
					category.getDescription(),
					category.getImagePath());

		} catch (Exception e) {
			throw new RuntimeException("Error updating image");
		}
	}
}