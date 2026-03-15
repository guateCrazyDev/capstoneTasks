package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.service.PlaceService;

@RestController
@RequestMapping("/api")
public class PlaceController {
	@Autowired
	PlaceService placeService;

	@PostMapping(value = "/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PlaceRecord> createPlace(
			@RequestPart("placeData") PlaceRecord placeRecord, @RequestPart("files") List<MultipartFile> files,
			@RequestPart("categoryName") String categoryName) {
		PlaceRecord response = placeService.createPlace(placeRecord, files, categoryName);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/place")
	public ResponseEntity<List<PlaceRecord>> getAllPlaces() {
		List<PlaceRecord> places = placeService.getAllByName();
		return new ResponseEntity<>(places, HttpStatus.OK);
	}

	@GetMapping("/place/{categoryName}")
	public ResponseEntity<List<PlaceRecord>> getAllPlacesByCat(@PathVariable String categoryName) {
		List<PlaceRecord> places = placeService.getAllByNameCat(categoryName);
		return new ResponseEntity<>(places, HttpStatus.OK);
	}

	@PutMapping(value = "/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PlaceRecord> actualizaPlace(
			@RequestPart("placeData") PlaceRecord placeRecord, @RequestPart("files") List<MultipartFile> files,
			@RequestPart("categoryName") String categoryName, @RequestPart("originalName") String originalName) {
		PlaceRecord response = placeService.updatePlace(placeRecord, files, categoryName, originalName);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/place/{placeName}")
	public ResponseEntity<String> eliminaPlace(@PathVariable String placeName) {
		placeService.deletePlace(placeName);
		return new ResponseEntity<>("Delete successfully!", HttpStatus.OK);
	}
}
