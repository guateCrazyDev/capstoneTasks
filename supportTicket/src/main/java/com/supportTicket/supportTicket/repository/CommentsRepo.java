package com.supportTicket.supportTicket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.Comments;

@Repository
public interface CommentsRepo extends JpaRepository<Comments, Long> {
    @Query("SELECT AVG(c.rate), COUNT(c) FROM Comments c WHERE c.place.name = :placeName")
    Object getStatsByPlace(@Param("placeName") String placeName);
}
