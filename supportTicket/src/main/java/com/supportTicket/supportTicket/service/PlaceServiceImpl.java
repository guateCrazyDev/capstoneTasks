package com.supportTicket.supportTicket.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.comparators.PlaceLigthNameComparator;
import com.supportTicket.supportTicket.comparators.PlaceNameComparator;
import com.supportTicket.supportTicket.exceptions.ElementAlreadyExistsException;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.model.Comments;
import com.supportTicket.supportTicket.model.PicturesComments;
import com.supportTicket.supportTicket.model.PicturesPlace;
import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.records.PictureCommentsRecord;
import com.supportTicket.supportTicket.records.PicturesPlaceRecord;
import com.supportTicket.supportTicket.records.PlaceLigthRecord;
import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.records.UserRecord;
import com.supportTicket.supportTicket.repository.CategoryRepo;
import com.supportTicket.supportTicket.repository.CommentsRepo;
import com.supportTicket.supportTicket.repository.PicturesCommentsRepo;
import com.supportTicket.supportTicket.repository.PicturesPlaceRepo;
import com.supportTicket.supportTicket.repository.PlaceRepo;

@Service
public class PlaceServiceImpl implements PlaceService{
	@Autowired
	PicturesPlaceRepo picturesPlaceRepo;
	@Autowired
	PlaceRepo placeRepo;
	@Autowired
	CategoryRepo catRepo;
	@Autowired
	PicturesCommentsRepo picComRepo;
	@Autowired
	CommentsRepo commRepo;
	
	private final Path fileStorageLocation;

    @Autowired
    public PlaceServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de carga.", ex);
        }
    }

    private String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "C:/uploads/images/"+fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Error al guardar el archivo " + fileName, ex);
        }
    }
	
	public PlaceRecord createPlace(PlaceRecord place,List<MultipartFile> files,String catName) {
		if(placeRepo.findByName(place.name()) == null) {
			if(catRepo.findByCategoryName(catName) != null) {
				Place places = new Place();
				Category category = catRepo.findByCategoryName(catName);
				places.setBestTime(place.bestTime());
				places.setLocation(place.location());
				places.setName(place.name());
				places.setCategory(category);
				places = placeRepo.save(places);
				List<PicturesPlace> pics = new ArrayList();
				for(MultipartFile file : files) {
					PicturesPlace pic = new PicturesPlace();
					pic.setPath(storeFile(file));
					pic.setPlace(places);
					pic = picturesPlaceRepo.save(pic);
					pics.add(pic);
				}
				places.setPicturesPlace(pics);
				places = placeRepo.save(places);
				List<Place> placesCat =  category.getPlaces();
				placesCat.add(places);
				category.setPlaces(placesCat);
				catRepo.save(category);
				List<PicturesPlaceRecord> picsRecord = new ArrayList();
				for(PicturesPlace pls : pics) {
					PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
					picsRecord.add(picRec);
				}
				PlaceRecord recRet = new PlaceRecord(
				place.name(),place.bestTime(),place.location(),picsRecord,category.getCategoryName(),place.comments());
				return recRet;
			}else {
				throw new ElementNotFoundException("This Category doenst exists");
			}
		}else {
			throw new ElementAlreadyExistsException("This place already exists");
		}
	}
	
	public List<PlaceRecord> getAllByName(){
		List<Place> places = placeRepo.findAll();
		if(places.size()>0) {
			List<PlaceRecord> placesR = new ArrayList();
			for(Place place : places) {
				List<PicturesPlaceRecord> picsRecord = new ArrayList();
				for(PicturesPlace pls : place.getPicturesPlace()) {
					PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
					picsRecord.add(picRec);
				}
				List<CommentRecord> commsRecord = new ArrayList();
				for(Comments cms : place.getComms()) {
					List<PictureCommentsRecord> commsRecordPic = new ArrayList();
					for(PicturesComments picsCom : cms.getPicturesComms()) {
						PictureCommentsRecord picComRec = new PictureCommentsRecord(picsCom.getPath());
						commsRecordPic.add(picComRec);
					}
					UserRecord userR = new UserRecord(cms.getUser().getUsername()
							,cms.getUser().getRole());
					CommentRecord comRec = new CommentRecord(
							cms.getText(),cms.getRate(),cms.getDate(),commsRecordPic,userR);
					commsRecord.add(comRec);
				}
				PlaceRecord rec = new PlaceRecord(
						place.getName()
						,place.getBestTime()
						,place.getLocation()
						,picsRecord
						,place.getCategory().getCategoryName()
						,commsRecord);
				placesR.add(rec);
			}
			placesR = placesR.stream().sorted(new PlaceNameComparator()).toList();
			return placesR;
		}else {
			throw new ElementNotFoundException("There is no places");
		}
	}
	
	public List<PlaceRecord> getAllByNameCat(String categoryName){
		if(catRepo.findByCategoryName(categoryName)!=null) {
			List<Place> places = placeRepo.findByCategory_CategoryName(categoryName); 
			if(places.size()>0) {
				List<PlaceRecord> placesR = new ArrayList();
				for(Place place : places) {
					List<PicturesPlaceRecord> picsRecord = new ArrayList();
					for(PicturesPlace pls : place.getPicturesPlace()) {
						PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
						picsRecord.add(picRec);
					}
					List<CommentRecord> commsRecord = new ArrayList();
					for(Comments cms : place.getComms()) {
						List<PictureCommentsRecord> commsRecordPic = new ArrayList();
						for(PicturesComments picsCom : cms.getPicturesComms()) {
							PictureCommentsRecord picComRec = new PictureCommentsRecord(picsCom.getPath());
							commsRecordPic.add(picComRec);
						}
						UserRecord userR = new UserRecord(cms.getUser().getUsername()
								,cms.getUser().getRole());
						CommentRecord comRec = new CommentRecord(
								cms.getText(),cms.getRate(),cms.getDate(),commsRecordPic,userR);
						commsRecord.add(comRec);
					}
					PlaceRecord rec = new PlaceRecord(
							place.getName()
							,place.getBestTime()
							,place.getLocation()
							,picsRecord
							,place.getCategory().getCategoryName()
							,commsRecord);
					placesR.add(rec);
				}
				placesR = placesR.stream().sorted(new PlaceNameComparator()).toList();
				return placesR;
			}else {
				throw new ElementNotFoundException("There is no places");
			}
		}else {
			throw new ElementNotFoundException("There Category with that name");
		}
	}
	public PlaceRecord updatePlace (PlaceRecord place,List<MultipartFile> files,String catName,String originalName) {
		if(placeRepo.findByName(originalName) != null) {
			if(placeRepo.findByName(place.name()) == null || place.name().equals(originalName)) {
				if(catRepo.findByCategoryName(catName) != null) {
					Place places = placeRepo.findByName(originalName);
					Category category = catRepo.findByCategoryName(catName);
					places.setBestTime(place.bestTime());
					places.setLocation(place.location());
					places.setName(place.name());
					places.setCategory(category);
					places = placeRepo.save(places);
					List<PicturesPlace> pics = new ArrayList();
					for(MultipartFile file : files) {
						PicturesPlace pic = new PicturesPlace();
						pic.setPath(storeFile(file));
						pic.setPlace(places);
						pic = picturesPlaceRepo.save(pic);
						pics.add(pic);
					}
					places.setPicturesPlace(pics);
					places = placeRepo.save(places);
					List<Place> placesCat =  category.getPlaces();
					placesCat.add(places);
					category.setPlaces(placesCat);
					catRepo.save(category);
					List<PicturesPlaceRecord> picsRecord = new ArrayList();
					for(PicturesPlace pls : pics) {
						PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
						picsRecord.add(picRec);
					}
					PlaceRecord recRet = new PlaceRecord(
					place.name(),place.bestTime(),place.location(),picsRecord,category.getCategoryName(),place.comments());
					return recRet;
				}else {
					throw new ElementNotFoundException("This Category doenst exists");
				}
			}else {
				throw new ElementAlreadyExistsException("This new name of place already exists");
			}
		}else {
			throw new ElementNotFoundException("The place to update doesnt exists");
		}
	}
	
	public void deletePlace(String placeName) {
		if(placeRepo.findByName(placeName) != null) {
			Place place = placeRepo.findByName(placeName);
			placeRepo.delete(place);
		}else {
			throw new ElementNotFoundException("The place to delete doesnt exists");
		}
	}
	
	public List<PlaceLigthRecord> getAllByNameCatLigth(String categoryName){
		if(catRepo.findByCategoryName(categoryName)!=null) {
			List<Place> places = placeRepo.findByCategory_CategoryName(categoryName); 
			if(places.size()>0) {
				List<PlaceLigthRecord> placesR = new ArrayList();
				for(Place place : places) {
					List<PicturesPlaceRecord> picsRecord = new ArrayList();
					for(PicturesPlace pls : place.getPicturesPlace()) {
						PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
						picsRecord.add(picRec);
					}
					PlaceLigthRecord rec = new PlaceLigthRecord(
							place.getName()
							,place.getBestTime()
							,place.getLocation()
							,picsRecord);
					placesR.add(rec);
				}
				placesR = placesR.stream().sorted(new PlaceLigthNameComparator()).toList();
				return placesR;
			}else {
				throw new ElementNotFoundException("There is no places");
			}
		}else {
			throw new ElementNotFoundException("There Category with that name");
		}
	}
	
	public PlaceRecord getPlaceByName(String placeName) {
		if(placeRepo.findByName(placeName) != null) {
			Place place = placeRepo.findByName(placeName);

			List<PicturesPlaceRecord> picsRecord = new ArrayList();
			for(PicturesPlace pls : place.getPicturesPlace()) {
				PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
				picsRecord.add(picRec);
			}
			List<CommentRecord> commsRecord = new ArrayList();
			for(Comments cms : place.getComms()) {
				List<PictureCommentsRecord> commsRecordPic = new ArrayList();
				for(PicturesComments picsCom : cms.getPicturesComms()) {
					PictureCommentsRecord picComRec = new PictureCommentsRecord(picsCom.getPath());
					commsRecordPic.add(picComRec);
				}
				UserRecord userR = new UserRecord(cms.getUser().getUsername()
						,cms.getUser().getRole());
				CommentRecord comRec = new CommentRecord(
						cms.getText(),cms.getRate(),cms.getDate(),commsRecordPic,userR);
				commsRecord.add(comRec);
			}
			PlaceRecord rec = new PlaceRecord(
					place.getName()
					,place.getBestTime()
					,place.getLocation()
					,picsRecord
					,place.getCategory().getCategoryName()
					,commsRecord);
			return rec;
		}else {
			throw new ElementNotFoundException("The place to update doesnt exists");
		}
	}
}
