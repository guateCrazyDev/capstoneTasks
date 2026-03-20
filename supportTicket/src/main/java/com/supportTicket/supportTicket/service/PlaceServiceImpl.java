package com.supportTicket.supportTicket.service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.exceptions.ImageFileException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.model.PicturesPlace;
import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.records.PicturesPlaceRecord;
import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;
import com.supportTicket.supportTicket.repository.PlaceRepo;

@Service
public class PlaceServiceImpl implements PlaceService {

	@Autowired
	private PlaceRepo placeRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private FileService fileService;

	// =========================
	// CREATE
	// =========================
	@Override
	@Transactional
	public PlaceRecord createPlace(PlaceRecord place, List<MultipartFile> files, String catName) {

		if (placeRepo.findByName(place.name()).isPresent()) {
			throw new ElementAlreadyExistsException("This place already exists");
		}

		Category category = categoryRepo.findByCategoryName(catName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		validateFiles(files);

		Place newPlace = new Place();
		newPlace.setName(place.name());
		newPlace.setBestTime(place.bestTime());
		newPlace.setLocation(place.location());
		newPlace.setCategory(category);
		newPlace.setPicturesPlace(new ArrayList<>());

		newPlace = placeRepo.save(newPlace);

		List<PicturesPlace> pics = saveFiles(files, newPlace);
		newPlace.getPicturesPlace().addAll(pics);

		return new PlaceRecord(
				newPlace.getName(),
				newPlace.getBestTime(),
				newPlace.getLocation(),
				mapPics(newPlace.getPicturesPlace()),
				category.getCategoryName(),
				false,
				0,
				new ArrayList<>());
	}

	// =========================
	// UPDATE
	// =========================
	@Override
	@Transactional
	public PlaceRecord updatePlace(PlaceRecord place, List<MultipartFile> files, String catName, String originalName) {

		Place existing = placeRepo.findByName(originalName)
				.orElseThrow(() -> new ElementNotFoundException("Place not found"));

		var other = placeRepo.findByName(place.name());

		if (other.isPresent() && !other.get().getName().equals(originalName)) {
			throw new ElementAlreadyExistsException("Place name already in use");
		}

		Category category = categoryRepo.findByCategoryName(catName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		validateFiles(files);

		existing.setName(place.name());
		existing.setBestTime(place.bestTime());
		existing.setLocation(place.location());
		existing.setCategory(category);

		if (existing.getPicturesPlace() == null) {
			existing.setPicturesPlace(new ArrayList<>());
		}

		if (files != null && !files.isEmpty()) {
			existing.getPicturesPlace().clear();
			List<PicturesPlace> newPics = saveFiles(files, existing);
			existing.getPicturesPlace().addAll(newPics);
		}

		placeRepo.save(existing);

		return new PlaceRecord(
				existing.getName(),
				existing.getBestTime(),
				existing.getLocation(),
				mapPics(existing.getPicturesPlace()),
				category.getCategoryName(),
				false,
				existing.getComms() != null ? existing.getComms().size() : 0,
				new ArrayList<>());
	}

	// =========================
	// GET ALL
	// =========================
	@Override
	public List<PlaceRecord> getAllByName() {

		List<Place> places = placeRepo.findAll();

		if (places.isEmpty()) {
			throw new ElementNotFoundException("There are no places");
		}

		List<PlaceRecord> result = new ArrayList<>();

		for (Place place : places) {
			result.add(new PlaceRecord(
					place.getName(),
					place.getBestTime(),
					place.getLocation(),
					mapPics(place.getPicturesPlace()),
					place.getCategory().getCategoryName(),
					false,
					0,
					new ArrayList<>()));
		}

		return result;
	}

	// =========================
	// GET BY CATEGORY
	// =========================
	@Override
	public List<PlaceRecord> getAllByNameCat(String categoryName, String userName) {

		Category category = categoryRepo.findByCategoryName(categoryName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		List<Place> places = placeRepo.findByCategory_CategoryName(categoryName);

		List<PlaceRecord> result = new ArrayList<>();

		for (Place place : places) {

			boolean relation = place.getUsers() != null &&
					place.getUsers().stream().anyMatch(u -> u.getUsername().equals(userName));

			result.add(new PlaceRecord(
					place.getName(),
					place.getBestTime(),
					place.getLocation(),
					mapPics(place.getPicturesPlace()),
					categoryName,
					relation,
					place.getComms() != null ? place.getComms().size() : 0,
					new ArrayList<>()));
		}

		return result;
	}

	// =========================
	// DELETE
	// =========================
	@Override
	@Transactional
	public void deletePlace(String placeName) {

		Place place = placeRepo.findByName(placeName)
				.orElseThrow(() -> new ElementNotFoundException("Place not found"));

		placeRepo.delete(place);
	}

	// =========================
	// VALIDATE FILES
	// =========================
	private void validateFiles(List<MultipartFile> files) {

		if (files == null || files.isEmpty())
			return;

		if (files.size() > 5) {
			throw new ImageFileException("Max files allowed: 5");
		}

		for (MultipartFile file : files) {
			fileService.validateImage(file);
		}
	}

	// =========================
	// SAVE FILES
	// =========================
	private List<PicturesPlace> saveFiles(List<MultipartFile> files, Place place) {

		List<PicturesPlace> pics = new ArrayList<>();

		if (files == null || files.isEmpty())
			return pics;

		for (MultipartFile file : files) {
			try {

				String fileName = fileService.uploadSingleImage(file, "places");

				PicturesPlace pic = new PicturesPlace();
				pic.setPath(fileName); // 🔥 SOLO EL NOMBRE
				pic.setPlace(place);

				pics.add(pic);

			} catch (Exception e) {
				throw new ImageFileException("Error saving file: " + e.getMessage());
			}
		}

		return pics;
	}

	// =========================
	// MAP PICS
	// =========================
	private List<PicturesPlaceRecord> mapPics(List<PicturesPlace> pics) {

		List<PicturesPlaceRecord> result = new ArrayList<>();

		if (pics != null) {
			for (PicturesPlace p : pics) {
				result.add(new PicturesPlaceRecord(p.getPath()));
			}
		}

		return result;
	}
}