package com.supportTicket.supportTicket.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.comparators.CommentDateComparator;
import com.supportTicket.supportTicket.exceptions.PlaceNotFoundException;
import com.supportTicket.supportTicket.exceptions.UserNotFoundException;
import com.supportTicket.supportTicket.model.*;
import com.supportTicket.supportTicket.records.*;
import com.supportTicket.supportTicket.repository.*;

@Service
public class CommentsServiceImpl implements CommentsService {

	@Autowired
	private CommentsRepo commsRepo;

	@Autowired
	private PlaceRepo placeRepo;

	@Autowired
	private UserRepository userRepository;

	private final Path fileStorageLocation;

	@Autowired
	public CommentsServiceImpl(@Value("${file.upload-dir}") String uploadDir) {

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

		String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

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
	public CommentRecord createComm(CommentRecord commR, List<MultipartFile> files, String userName, String placeName) {

		Optional<User> userOp = userRepository.findByUsername(userName);

		if (userOp.isEmpty()) {
			throw new UserNotFoundException(userName);
		}

		Place place = placeRepo.findByName(placeName);

		if (place == null) {
			throw new PlaceNotFoundException(placeName);
		}

		User user = userOp.get();

		Comments comment = new Comments();

		comment.setText(commR.text());
		comment.setRate(commR.rate());
		comment.setDate(commR.date());
		comment.setUser(user);
		comment.setPlace(place);

		List<PicturesComments> pictures = new ArrayList<>();

		if (files != null && !files.isEmpty()) {

			for (MultipartFile file : files) {

				PicturesComments picture = new PicturesComments();

				picture.setPath(storeFile(file));
				picture.setComment(comment);

				pictures.add(picture);
			}
		}

		comment.setPicturesComms(pictures);

		comment = commsRepo.save(comment);

		if (place.getComms() == null) {
			place.setComms(new ArrayList<>());
		}

		List<Comments> comments = place.getComms();

		comments.add(comment);

		comments.sort(new CommentDateComparator());

		placeRepo.save(place);

		List<PictureCommentsRecord> picsRecord = new ArrayList<>();

		for (PicturesComments pic : pictures) {
			picsRecord.add(new PictureCommentsRecord(pic.getPath()));
		}

		UserRecord userRecord = new UserRecord(user.getUsername(), user.getRole());

		return new CommentRecord(
				comment.getText(),
				comment.getRate(),
				comment.getDate(),
				picsRecord,
				userRecord);
	}
}