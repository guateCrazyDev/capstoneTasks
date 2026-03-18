package com.supportTicket.supportTicket.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.supportTicket.supportTicket.comparators.PlaceLigthNameComparator;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.model.PicturesPlace;
import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.PicturesPlaceRecord;
import com.supportTicket.supportTicket.records.PlaceLigthRecord;
import com.supportTicket.supportTicket.repository.PlaceRepo;
import com.supportTicket.supportTicket.repository.UserRepository;

@Service
public class UserPlaceServiceImpl implements UserPlaceService {
    private final UserRepository userRepository;
    private final PlaceRepo placeRepo;

    public UserPlaceServiceImpl(UserRepository userRepository,
            PlaceRepo placeRepo) {
        this.userRepository = userRepository;
        this.placeRepo = placeRepo;
    }

    public void createRelationship(String user, String place) {
        if (placeRepo.findByName(place) != null &&
                userRepository.findByUsername(user) != null) {
            Place placeI = placeRepo.findByName(place);
            Optional<User> userI = userRepository.findByUsername(user);
            User userIns = userI.get();
            Set<User> users = placeI.getUsers();
            users.add(userIns);
            placeI.setUsers(users);
            placeRepo.save(placeI);
        } else {
            throw new ElementNotFoundException("User or place dont exists");
        }
    }

    public List<PlaceLigthRecord> getAllByUserLigth(String userName) {
        if (userRepository.findByUsername(userName) != null) {
            List<Place> places = placeRepo.findAllByUsername(userName);
            if (places.size() > 0) {
                List<PlaceLigthRecord> placesR = new ArrayList();
                for (Place place : places) {
                    List<PicturesPlaceRecord> picsRecord = new ArrayList();
                    for (PicturesPlace pls : place.getPicturesPlace()) {
                        PicturesPlaceRecord picRec = new PicturesPlaceRecord(pls.getPath());
                        picsRecord.add(picRec);
                    }
                    PlaceLigthRecord rec = new PlaceLigthRecord(
                            place.getName(), place.getBestTime(), place.getLocation(), picsRecord);
                    placesR.add(rec);
                }
                placesR = placesR.stream().sorted(new PlaceLigthNameComparator()).toList();
                return placesR;
            } else {
                throw new ElementNotFoundException("There is no places");
            }
        } else {
            throw new ElementNotFoundException("There Category with that name");
        }
    }
    
    public void deleteRelationship(String user, String place) {
        if (placeRepo.findByName(place) != null &&
                userRepository.findByUsername(user) != null) {
            Place placeI = placeRepo.findByName(place);
            Optional<User> userI = userRepository.findByUsername(user);
            User userIns = userI.get();
            Set<User> users = placeI.getUsers();
            users.remove(userIns);
            placeI.setUsers(users);
            placeRepo.save(placeI);
        } else {
            throw new ElementNotFoundException("User or place dont exists");
        }
    }

}
