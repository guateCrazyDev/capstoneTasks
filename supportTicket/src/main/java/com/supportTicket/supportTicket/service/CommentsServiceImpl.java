package com.supportTicket.supportTicket.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	private CommentsRepo commentsRepo;

	@Autowired
	private PlaceRepo placeRepo;

	@Autowired
	private UserRepository userRepository;

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

		try {

			if (files != null && !files.isEmpty()) {

				String uploadDir = System.getProperty("user.dir") + "/uploads/comments/";

				for (MultipartFile file : files) {

					String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

					Path path = Paths.get(uploadDir + fileName);

					Files.createDirectories(path.getParent());
					Files.write(path, file.getBytes());

					PicturesComments picture = new PicturesComments();

					picture.setPath(fileName);
					picture.setComment(comment);

					pictures.add(picture);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Error saving image");
		}

		comment.setPicturesComms(pictures);

		comment = commentsRepo.save(comment);

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

	@Override
	public List<CommentRecord> getCommentsByPlace(String placeName) {

		Place place = placeRepo.findByName(placeName);

		if (place == null) {
			throw new PlaceNotFoundException(placeName);
		}

		List<Comments> comments = place.getComms();

		if (comments == null) {
			return new ArrayList<>();
		}

		comments.sort(new CommentDateComparator());

		List<CommentRecord> response = new ArrayList<>();

		for (Comments comment : comments) {

			List<PictureCommentsRecord> pics = new ArrayList<>();

			if (comment.getPicturesComms() != null) {
				for (PicturesComments pic : comment.getPicturesComms()) {
					pics.add(new PictureCommentsRecord(pic.getPath()));
				}
			}

			User user = comment.getUser();
			UserRecord userRecord = new UserRecord(user.getUsername(), user.getRole());

			response.add(new CommentRecord(
					comment.getText(),
					comment.getRate(),
					comment.getDate(),
					pics,
					userRecord));
		}

		return response;
	}
}