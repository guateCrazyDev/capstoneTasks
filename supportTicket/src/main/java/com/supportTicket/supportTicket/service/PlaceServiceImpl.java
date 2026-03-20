package com.supportTicket.supportTicket.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.model.PicturesPlace;
import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.PicturesPlaceRecord;
import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;
import com.supportTicket.supportTicket.repository.PicturesPlaceRepo;
import com.supportTicket.supportTicket.repository.PlaceRepo;

@Service
public class PlaceServiceImpl implements PlaceService {

	@Autowired
	private PlaceRepo placeRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private PicturesPlaceRepo picturesPlaceRepo;

	@Override
	public PlaceRecord createPlace(PlaceRecord place, List<MultipartFile> files, String catName) {

		if (placeRepo.findByName(place.name()) != null) {
			throw new ElementAlreadyExistsException("This place already exists");
		}

		Category category = categoryRepo.findByCategoryName(catName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		Place newPlace = new Place();

		newPlace.setName(place.name());
		newPlace.setBestTime(place.bestTime());
		newPlace.setLocation(place.location());
		newPlace.setCategory(category);

		newPlace = placeRepo.save(newPlace);

		try {

			List<PicturesPlace> pics = new ArrayList<>();

			if (files != null && !files.isEmpty()) {

				String uploadDir = System.getProperty("user.dir") + "/uploads/places/";

				for (MultipartFile file : files) {

					String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

					Path path = Paths.get(uploadDir + fileName);

					Files.createDirectories(path.getParent());
					Files.write(path, file.getBytes());

					PicturesPlace pic = new PicturesPlace();
					pic.setPath(fileName);
					pic.setPlace(newPlace);

					picturesPlaceRepo.save(pic);
					pics.add(pic);
				}
			}

			newPlace.setPicturesPlace(pics);
			placeRepo.save(newPlace);

			List<PicturesPlaceRecord> picsRecord = new ArrayList<>();

			for (PicturesPlace p : pics) {
				picsRecord.add(new PicturesPlaceRecord(p.getPath()));
			}

			return new PlaceRecord(
					newPlace.getName(),
					newPlace.getBestTime(),
					newPlace.getLocation(),
					picsRecord,
					category.getCategoryName(),
					false,
					Integer.valueOf(0),
					new ArrayList<>());

		} catch (Exception e) {
			throw new RuntimeException("Error saving image");
		}
	}

	@Override
	public PlaceRecord updatePlace(PlaceRecord place, List<MultipartFile> files, String catName, String originalName) {

		Place existing = placeRepo.findByName(originalName);

		if (existing == null) {
			throw new ElementNotFoundException("Place not found");
		}

		Category category = categoryRepo.findByCategoryName(catName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		// 🔹 actualizar datos
		existing.setName(place.name());
		existing.setBestTime(place.bestTime());
		existing.setLocation(place.location());
		existing.setCategory(category);

		try {

			List<PicturesPlace> pictures = existing.getPicturesPlace();

			if (pictures == null) {
				pictures = new ArrayList<>();
			}

			String uploadDir = System.getProperty("user.dir") + "/uploads/places/";

			// 🔥 agregar nuevas imágenes ()
			if (files != null && !files.isEmpty()) {

				for (MultipartFile file : files) {

					String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

					Path path = Paths.get(uploadDir + fileName);

					Files.createDirectories(path.getParent());
					Files.write(path, file.getBytes());

					PicturesPlace pic = new PicturesPlace();
					pic.setPath(fileName);
					pic.setPlace(existing); // 🔥 CRÍTICO

					picturesPlaceRepo.save(pic);
					pictures.add(pic);
				}
			}

			existing.setPicturesPlace(pictures);

			placeRepo.save(existing);

			// 🔹 convertir a record
			List<PicturesPlaceRecord> picsRecord = new ArrayList<>();

			for (PicturesPlace p : pictures) {
				picsRecord.add(new PicturesPlaceRecord(p.getPath()));
			}

			return new PlaceRecord(
					existing.getName(),
					existing.getBestTime(),
					existing.getLocation(),
					picsRecord,
					category.getCategoryName(),
					false,
					existing.getComms().size(),
					new ArrayList<>());

		} catch (Exception e) {
			throw new RuntimeException("Error updating place");
		}
	}

	@Override
	public List<PlaceRecord> getAllByName() {

		List<Place> places = placeRepo.findAll();

		if (places.isEmpty()) {
			throw new ElementNotFoundException("There are no places");
		}

		List<PlaceRecord> result = new ArrayList<>();

		for (Place place : places) {

			List<PicturesPlaceRecord> pics = new ArrayList<>();

			for (PicturesPlace p : place.getPicturesPlace()) {
				pics.add(new PicturesPlaceRecord(p.getPath()));
			}

			result.add(new PlaceRecord(
					place.getName(),
					place.getBestTime(),
					place.getLocation(),
					pics,
					place.getCategory().getCategoryName(),
					false,
					Integer.valueOf(0),
					new ArrayList<>()));
		}

		return result;
	}

	@Override
	public List<PlaceRecord> getAllByNameCat(String categoryName, String userName) {

		Category category = categoryRepo.findByCategoryName(categoryName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		List<Place> places = placeRepo.findByCategory_CategoryName(categoryName);

		List<PlaceRecord> result = new ArrayList<>();

		for (Place place : places) {

			List<PicturesPlaceRecord> pics = new ArrayList<>();

			for (PicturesPlace p : place.getPicturesPlace()) {
				pics.add(new PicturesPlaceRecord(p.getPath()));
			}

			Set<User> users = place.getUsers();
			Boolean relation = false;

			for (User user : users) {
				if (user.getUsername().equals(userName)) {
					relation = true;
					break;
				}
			}

			result.add(new PlaceRecord(
					place.getName(),
					place.getBestTime(),
					place.getLocation(),
					pics,
					categoryName,
					relation,
					place.getComms().size(),
					new ArrayList<>()));
		}

		return result;
	}

	@Override
	public void deletePlace(String placeName) {

		Place place = placeRepo.findByName(placeName);

		if (place == null) {
			throw new ElementNotFoundException("Place not found");
		}

		placeRepo.delete(place);
	}
}