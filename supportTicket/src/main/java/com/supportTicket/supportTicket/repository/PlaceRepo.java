package com.supportTicket.supportTicket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.Place;

@Repository
public interface PlaceRepo extends JpaRepository<Place,Long>{
	public Place findByName(String name);
	public List<Place> findByCategory_CategoryName(String categoryName);
	@Query("""
        SELECT p
        FROM Place p
        JOIN p.users u
        WHERE u.username = :username
    """)
    List<Place> findAllByUsername(@Param("username") String username);

}
