package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.repository.PlaceRepo;
import com.supportTicket.supportTicket.repository.UserRepository;

import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;


@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PlaceRepo placeRepo;

    @Override
    public void addFavorite(Long userId, Long placeId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));

        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new ElementNotFoundException("Place not found"));

        if (!user.getFavoritePlaces().contains(place)) {
            user.getFavoritePlaces().add(place);
            userRepo.save(user);
        }
    }

    @Override
    public void removeFavorite(Long userId, Long placeId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));

        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new ElementNotFoundException("Place not found"));

        user.getFavoritePlaces().remove(place);
        userRepo.save(user);
    }

    @Override
    public List<Place> getFavorites(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));

        return user.getFavoritePlaces();
    }
}
