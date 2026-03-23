package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;

	public PlaceController(PlaceService placeService) {
		this.placeService = placeService;
	}

	// =========================
	// CREATE (JSON + FILES)
	// =========================
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PlaceRecord> createPlace(
			@RequestPart("placeData") PlaceRecord place,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestParam String categoryName) {

		PlaceRecord response = placeService.createPlace(place, files, categoryName);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// =========================
	// UPDATE (JSON + FILES opcional)
	// =========================
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PlaceRecord> updatePlace(
			@RequestPart("placeData") PlaceRecord place,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestParam String categoryName,
			@RequestParam String originalName) {

		PlaceRecord response = placeService.updatePlace(place, files, categoryName, originalName);

		return ResponseEntity.ok(response);
	}

	// =========================
	// GET ALL
	// =========================
	@GetMapping
	public ResponseEntity<List<PlaceRecord>> getAllPlaces() {
		return ResponseEntity.ok(placeService.getAllByName());
	}

	// =========================
	// GET BY CATEGORY + USER
	// =========================
	@GetMapping("/category/{categoryName}/user/{userName}")
	public ResponseEntity<List<PlaceRecord>> getAllPlacesByCat(
			@PathVariable String categoryName,
			@PathVariable String userName) {

		return ResponseEntity.ok(
				placeService.getAllByNameCat(categoryName, userName));
	}

	// =========================
	// DELETE
	// =========================
	@DeleteMapping("/{placeName}")
	public ResponseEntity<String> deletePlace(@PathVariable String placeName) {

		placeService.deletePlace(placeName);

		return ResponseEntity.ok("Delete successfully!");
	}
}
