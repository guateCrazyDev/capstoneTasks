package com.supportTicket.supportTicket.repository;

import java.sql.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.supportTicket.supportTicket.model.Comments;

@Repository
public interface CommentsRepo extends JpaRepository<Comments, Long> {
	@Query("SELECT AVG(c.rate), COUNT(c) FROM Comments c WHERE c.place.name = :placeName")
	Object getStatsByPlace(@Param("placeName") String placeName);

	@Query("""
			    SELECT c
			    FROM Comments c
			    JOIN c.user u
			    JOIN c.place p
			    WHERE u.username = :username
			  		AND p.name = :placename
			  		AND c.text = :text
			  		AND c.date = :Date
			""")

	Optional<Comments> findCommentByParams(@Param("username") String username,
			@Param("placename") String placename,
			@Param("text") String text,
			@Param("Date") Date date);
}
