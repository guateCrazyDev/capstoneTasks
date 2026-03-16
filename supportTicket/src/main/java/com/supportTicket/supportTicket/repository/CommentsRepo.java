package com.supportTicket.supportTicket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.Comments;

@Repository
public interface CommentsRepo extends JpaRepository<Comments, Long>{
	public List<Comments> findByUserNameComent(String userNameComent);
}
