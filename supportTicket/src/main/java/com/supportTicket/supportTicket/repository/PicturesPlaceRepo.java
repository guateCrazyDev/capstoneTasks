package com.supportTicket.supportTicket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.PicturesPlace;

@Repository
public interface PicturesPlaceRepo extends JpaRepository<PicturesPlace, Long> {

}
