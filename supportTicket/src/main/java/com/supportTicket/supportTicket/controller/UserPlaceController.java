package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supportTicket.supportTicket.records.PlaceLigthRecord;
import com.supportTicket.supportTicket.service.UserPlaceService;

@RestController
@RequestMapping("/api")
public class UserPlaceController {
    @Autowired
    UserPlaceService userPService;

    @PutMapping("/user/{user}/place{place}")
    public ResponseEntity<String> createRelationship(@PathVariable String user, @PathVariable String place) {
        userPService.createRelationship(user, place);
        return new ResponseEntity<>("Relation successfully created", HttpStatus.OK);
    }

    @GetMapping("/userplace/ligth/{userName}")
    public ResponseEntity<List<PlaceLigthRecord>> getAllPlacesByCatLigth(@PathVariable String userName) {
        List<PlaceLigthRecord> places = userPService.getAllByUserLigth(userName);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }
}
