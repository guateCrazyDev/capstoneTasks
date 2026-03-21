package com.supportTicket.supportTicket.service;

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

	@Autowired
	private FileService fileService;

	@Override
	public CommentRecord createComm(CommentRecord commR, List<MultipartFile> files, String userName, String placeName) {

		User user = userRepository.findByUsername(userName)
				.orElseThrow(() -> new UserNotFoundException(userName));

		Place place = placeRepo.findByName(placeName)
				.orElseThrow(() -> new PlaceNotFoundException(placeName));

		Comments comment = new Comments();
		comment.setText(commR.text());
		comment.setRate(commR.rate());
		comment.setDate(commR.date());
		comment.setUser(user);
		comment.setPlace(place);

		List<PicturesComments> pictures = new ArrayList<>();

		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {

				String fileName = fileService.uploadSingleImage(file, "comments");

				PicturesComments picture = new PicturesComments();
				picture.setPath(fileName);
				picture.setComment(comment);

				pictures.add(picture);
			}
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

		UserRecord userRecord = new UserRecord(user.getUsername(), user.getImgPath());

		return new CommentRecord(
				comment.getText(),
				comment.getRate(),
				comment.getDate(),
				picsRecord,
				userRecord);
	}

	@Override
	public List<CommentRecord> getCommentsByPlace(String placeName) {

		Place place = placeRepo.findByName(placeName)
				.orElseThrow(() -> new PlaceNotFoundException(placeName));

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
			UserRecord userRecord = new UserRecord(user.getUsername(), user.getImgPath());

			response.add(new CommentRecord(
					comment.getText(),
					comment.getRate(),
					comment.getDate(),
					pics,
					userRecord));
		}

		return response;
	}

	@Override
	public CommentStatsRecord getCommentsStats(String placeName) {

		placeRepo.findByName(placeName)
				.orElseThrow(() -> new PlaceNotFoundException(placeName));

		Object result = commentsRepo.getStatsByPlace(placeName);

		double average = 0.0;
		long count = 0;

		if (result != null) {

			Object[] stats = (Object[]) result;

			if (stats[0] != null) {
				average = ((Number) stats[0]).doubleValue();
			}

			if (stats[1] != null) {
				count = ((Number) stats[1]).longValue();
			}
		}

		return new CommentStatsRecord(average, count);
	}

	@Override
	public void updateComm(CommentRecord comment, CommentRecord commentNew,
			String placeName, String userName, List<MultipartFile> images) {

		Comments commentRes = commentsRepo.findCommentByParams(
				userName, placeName, comment.text(), comment.date());

		if (commentRes == null) {
			throw new RuntimeException("Comment not found");
		}

		List<PicturesComments> newPictures = new ArrayList<>();

		if (images != null && !images.isEmpty()) {
			for (MultipartFile file : images) {

				String fileName = fileService.uploadSingleImage(file, "comments");

				PicturesComments picture = new PicturesComments();
				picture.setPath(fileName);
				picture.setComment(commentRes);

				newPictures.add(picture);
			}
		}

		List<PicturesComments> existing = commentRes.getPicturesComms();

		if (existing == null) {
			existing = new ArrayList<>();
		}

		existing.addAll(newPictures);

		commentRes.setPicturesComms(existing);
		commentRes.setText(commentNew.text());
		commentRes.setRate(commentNew.rate());

		commentsRepo.save(commentRes);
	}

	@Override
	public void delete(CommentRecord comment, String placeName, String userName) {

		Comments commentRes = commentsRepo.findCommentByParams(
				userName, placeName, comment.text(), comment.date());

		if (commentRes == null) {
			throw new RuntimeException("Comment not found");
		}

		commentsRepo.delete(commentRes);
	}
}