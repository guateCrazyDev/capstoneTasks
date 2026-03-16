package com.supportTicket.supportTicket.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.supportTicket.supportTicket.model.Place;

@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {

	Place findByName(String name);

	List<Place> findByCategory_CategoryName(String categoryName);
}