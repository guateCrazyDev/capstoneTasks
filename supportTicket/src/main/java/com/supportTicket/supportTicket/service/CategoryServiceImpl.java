package com.supportTicket.supportTicket.service;

import java.io.IOException;
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
import com.supportTicket.supportTicket.exceptions.ImageStorageException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;

	private final String uploadDir = System.getProperty("user.dir") + "/uploads/categories/";

	private String storeImage(MultipartFile img) {

		if (img == null || img.isEmpty()) {
			throw new ImageStorageException("Image file is empty");
		}

		String fileName = UUID.randomUUID() + "_" + img.getOriginalFilename();

		try {

			Path path = Paths.get(uploadDir + fileName);

			Files.createDirectories(path.getParent());

			Files.write(path, img.getBytes());

			return uploadDir + fileName;

		} catch (IOException e) {

			throw new ImageStorageException("Failed to store image: " + fileName);
		}
	}

	@Override
	public CategoryRecord createCategory(CategoryRecord catRec, MultipartFile img) {

		Category existing = categoryRepo.findByCategoryName(catRec.categoryName());

		if (existing != null) {
			throw new ElementAlreadyExistsException("Category already exists: " + catRec.categoryName());
		}

		String imagePath = storeImage(img);

		Category category = new Category();

		category.setCategoryName(catRec.categoryName());
		category.setDescription(catRec.description());
		category.setImagePath(imagePath);

		categoryRepo.save(category);

		return new CategoryRecord(
				category.getCategoryName(),
				category.getDescription(),
				category.getImagePath());
	}

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
}