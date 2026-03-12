package com.supportTicket.supportTicket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.Place;

@Repository
public interface PlaceRepo extends JpaRepository<Place,Long>{
	public Place findByName(String name);
}
