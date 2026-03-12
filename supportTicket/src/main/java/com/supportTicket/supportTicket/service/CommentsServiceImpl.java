package com.supportTicket.supportTicket.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.comparators.CommentDateComparator;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.model.Comments;
import com.supportTicket.supportTicket.model.PicturesComments;
import com.supportTicket.supportTicket.model.PicturesPlace;
import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.repository.CommentsRepo;
import com.supportTicket.supportTicket.repository.PicturesCommentsRepo;
import com.supportTicket.supportTicket.repository.PlaceRepo;
import com.supportTicket.supportTicket.repository.UserRepository;

@Service
public class CommentsServiceImpl implements CommentsService {
	@Autowired
	PlaceRepo placeRepo;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PicturesCommentsRepo picsCommsRepo;
	@Autowired
	CommentsRepo commsRepo;

	private final Path fileStorageLocation;

	@Autowired
	public CommentsServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
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
			return "C:/uploads/images/" + fileName;
		} catch (IOException ex) {
			throw new RuntimeException("Error al guardar el archivo " + fileName, ex);
		}
	}

	public void createComm(CommentRecord commR, List<MultipartFile> files, String userName, String placeName) {
		if (userRepository.findByUsername(userName) != null && placeRepo.findByName(placeName) != null) {
			Comments comment = new Comments();
			Optional<User> userOp = userRepository.findByUsername(userName);
			User user = userOp.get();
			Place place = placeRepo.findByName(placeName);
			comment.setText(commR.text());
			comment.setDate(commR.date());
			comment.setRate(commR.rate());
			comment.setUser(user);
			comment.setPlace(place);
			comment = commsRepo.save(comment);
			List<PicturesComments> pics = new ArrayList();
			for (MultipartFile file : files) {
				PicturesComments pic = new PicturesComments();
				pic.setPath(storeFile(file));
				pic.setComment(comment);
				pic = picsCommsRepo.save(pic);
				pics.add(pic);
			}
			comment.setPicturesComms(pics);
			comment = commsRepo.save(comment);
			List<Comments> comments = place.getComms();
			comments.add(comment);
			comments.sort(new CommentDateComparator());
			placeRepo.save(place);
		} else {
			throw new ElementNotFoundException("Place or User doenst exists");
		}
	}
}
