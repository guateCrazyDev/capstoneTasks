package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.service.PlaceService;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;
	private final ObjectMapper objectMapper;

	public PlaceController(PlaceService placeService, ObjectMapper objectMapper) {
		this.placeService = placeService;
		this.objectMapper = objectMapper;
	}

	// =========================
	// CREATE (JSON + FILES)
	// =========================
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PlaceRecord> createPlace(
			@RequestPart("placeData") String placeJson,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestPart("categoryName") String categoryName) {

		try {
			PlaceRecord placeRecord = objectMapper.readValue(placeJson, PlaceRecord.class);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(placeService.createPlace(placeRecord, files, categoryName));

		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// =========================
	// UPDATE (JSON + FILES opcional)
	// =========================
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PlaceRecord> updatePlace(
			@RequestPart("placeData") String placeJson,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestPart("categoryName") String categoryName,
			@RequestPart("originalName") String originalName) {

		try {
			PlaceRecord placeRecord = objectMapper.readValue(placeJson, PlaceRecord.class);

			return ResponseEntity.ok(
					placeService.updatePlace(placeRecord, files, categoryName, originalName));

		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
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