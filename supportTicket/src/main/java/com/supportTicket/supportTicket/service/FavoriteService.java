package com.supportTicket.supportTicket.service;

import java.util.List;

import com.supportTicket.supportTicket.model.Place;

public interface FavoriteService {
    void addFavorite(Long userId, Long placeId);
    void removeFavorite(Long userId, Long placeId);
    List<Place> getFavorites(Long userId);
}
