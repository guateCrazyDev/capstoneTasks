package com.supportTicket.supportTicket.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.comparators.PlaceNameComparator;
import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.model.*;
import com.supportTicket.supportTicket.records.*;
import com.supportTicket.supportTicket.repository.*;

@Service
public class PlaceServiceImpl implements PlaceService {

	@Autowired
	private PicturesPlaceRepo picturesPlaceRepo;

	@Autowired
	private PlaceRepo placeRepo;

	@Autowired
	private CategoryRepo catRepo;

	private final Path fileStorageLocation;

	@Autowired
	public PlaceServiceImpl(@Value("${file.upload-dir}") String uploadDir) {

		this.fileStorageLocation = Paths.get(uploadDir)
				.toAbsolutePath()
				.normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new RuntimeException("No se pudo crear el directorio de carga", ex);
		}
	}

	private String storeFile(MultipartFile file) {

		String originalName = StringUtils.cleanPath(file.getOriginalFilename());
		String fileName = UUID.randomUUID() + "_" + originalName;

		try {

			Path targetLocation = this.fileStorageLocation.resolve(fileName);

			Files.copy(
					file.getInputStream(),
					targetLocation,
					StandardCopyOption.REPLACE_EXISTING);

			return targetLocation.toString();

		} catch (IOException ex) {

			throw new RuntimeException("Error guardando archivo " + fileName);

		}
	}

	@Override
	public PlaceRecord createPlace(PlaceRecord place, List<MultipartFile> files, String catName) {

		if (placeRepo.findByName(place.name()) != null) {
			throw new ElementAlreadyExistsException("This place already exists");
		}

		Category category = catRepo.findByCategoryName(catName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		Place newPlace = new Place();
		newPlace.setName(place.name());
		newPlace.setBestTime(place.bestTime());
		newPlace.setLocation(place.location());
		newPlace.setCategory(category);

		newPlace = placeRepo.save(newPlace);

		List<PicturesPlace> pics = new ArrayList<>();

		for (MultipartFile file : files) {

			PicturesPlace pic = new PicturesPlace();

			pic.setPath(storeFile(file));
			pic.setPlace(newPlace);

			pic = picturesPlaceRepo.save(pic);

			pics.add(pic);
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
				place.comments());
	}

	@Override
	public List<PlaceRecord> getAllByName() {

		List<Place> places = placeRepo.findAll();

		if (places.isEmpty()) {
			throw new ElementNotFoundException("There are no places");
		}

		List<PlaceRecord> result = new ArrayList<>();

		for (Place place : places) {

			List<PicturesPlaceRecord> picsRecord = new ArrayList<>();

			for (PicturesPlace pic : place.getPicturesPlace()) {
				picsRecord.add(new PicturesPlaceRecord(pic.getPath()));
			}

			List<CommentRecord> commsRecord = new ArrayList<>();

			for (Comments cms : place.getComms()) {

				List<PictureCommentsRecord> picsCom = new ArrayList<>();

				for (PicturesComments pc : cms.getPicturesComms()) {
					picsCom.add(new PictureCommentsRecord(pc.getPath()));
				}

				UserRecord userR = new UserRecord(
						cms.getUser().getUsername(),
						cms.getUser().getRole());

				CommentRecord comRec = new CommentRecord(
						cms.getText(),
						cms.getRate(),
						cms.getDate(),
						picsCom,
						userR);

				commsRecord.add(comRec);
			}

			PlaceRecord rec = new PlaceRecord(
					place.getName(),
					place.getBestTime(),
					place.getLocation(),
					picsRecord,
					place.getCategory().getCategoryName(),
					commsRecord);

			result.add(rec);
		}

		return result.stream()
				.sorted(new PlaceNameComparator())
				.toList();
	}

	@Override
	public List<PlaceRecord> getAllByNameCat(String categoryName) {

		Category category = catRepo.findByCategoryName(categoryName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		List<Place> places = placeRepo.findByCategory_CategoryName(categoryName);

		if (places.isEmpty()) {
			throw new ElementNotFoundException("No places found");
		}

		List<PlaceRecord> result = new ArrayList<>();

		for (Place place : places) {

			List<PicturesPlaceRecord> picsRecord = new ArrayList<>();

			for (PicturesPlace pic : place.getPicturesPlace()) {
				picsRecord.add(new PicturesPlaceRecord(pic.getPath()));
			}

			PlaceRecord rec = new PlaceRecord(
					place.getName(),
					place.getBestTime(),
					place.getLocation(),
					picsRecord,
					place.getCategory().getCategoryName(),
					new ArrayList<>());

			result.add(rec);
		}

		return result.stream()
				.sorted(new PlaceNameComparator())
				.toList();
	}

	@Override
	public PlaceRecord updatePlace(PlaceRecord place, List<MultipartFile> files, String catName, String originalName) {

		Place existing = placeRepo.findByName(originalName);

		if (existing == null) {
			throw new ElementNotFoundException("Place not found");
		}

		Category category = catRepo.findByCategoryName(catName);

		if (category == null) {
			throw new ElementNotFoundException("Category not found");
		}

		existing.setName(place.name());
		existing.setBestTime(place.bestTime());
		existing.setLocation(place.location());
		existing.setCategory(category);

		existing = placeRepo.save(existing);

		List<PicturesPlace> pics = new ArrayList<>();

		for (MultipartFile file : files) {

			PicturesPlace pic = new PicturesPlace();

			pic.setPath(storeFile(file));
			pic.setPlace(existing);

			pic = picturesPlaceRepo.save(pic);

			pics.add(pic);
		}

		existing.setPicturesPlace(pics);

		placeRepo.save(existing);

		List<PicturesPlaceRecord> picsRecord = new ArrayList<>();

		for (PicturesPlace p : pics) {
			picsRecord.add(new PicturesPlaceRecord(p.getPath()));
		}

		return new PlaceRecord(
				existing.getName(),
				existing.getBestTime(),
				existing.getLocation(),
				picsRecord,
				category.getCategoryName(),
				place.comments());
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