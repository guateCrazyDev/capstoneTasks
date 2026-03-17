package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supportTicket.supportTicket.model.Place;
import com.supportTicket.supportTicket.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/{userId}/{placeId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long userId, @PathVariable Long placeId) {
        favoriteService.addFavorite(userId, placeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{placeId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId, @PathVariable Long placeId) {
        favoriteService.removeFavorite(userId, placeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Place>> getFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }
}
