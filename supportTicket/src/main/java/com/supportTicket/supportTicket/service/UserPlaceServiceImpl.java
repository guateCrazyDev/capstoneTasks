package com.supportTicket.supportTicket.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.supportTicket.supportTicket.comparators.PlaceLigthNameComparator;
import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
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

    public UserPlaceServiceImpl(UserRepository userRepository, PlaceRepo placeRepo) {
        this.userRepository = userRepository;
        this.placeRepo = placeRepo;
    }

    public void createRelationship(String user, String place) {

        Place placeI = Optional.ofNullable(placeRepo.findByName(place))
                .orElseThrow(() -> new ElementNotFoundException("Place not found"));

        User userIns = userRepository.findByUsername(user)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));

        Set<User> users = placeI.getUsers();
        users.add(userIns);
        placeI.setUsers(users);

        placeRepo.save(placeI);
    }

    public List<PlaceLigthRecord> getAllByUserLigth(String userName) {

        userRepository.findByUsername(userName)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));

        List<Place> places = placeRepo.findAllByUsername(userName);

        if (places.isEmpty()) {
            throw new ElementNotFoundException("There are no places");
        }

        List<PlaceLigthRecord> placesR = places.stream().map(place -> {

            List<PicturesPlaceRecord> pics = place.getPicturesPlace().stream()
                    .map(p -> new PicturesPlaceRecord(p.getPath()))
                    .toList();

            return new PlaceLigthRecord(
                    place.getName(),
                    place.getBestTime(),
                    place.getLocation(),
                    pics);

        }).sorted(new PlaceLigthNameComparator()).toList();

        return placesR;
    }

    public void deleteRelationship(String user, String place) {

        Place placeI = Optional.ofNullable(placeRepo.findByName(place))
                .orElseThrow(() -> new ElementNotFoundException("Place not found"));

        User userIns = userRepository.findByUsername(user)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));

        Set<User> users = placeI.getUsers();
        users.remove(userIns);
        placeI.setUsers(users);

        placeRepo.save(placeI);
    }
}