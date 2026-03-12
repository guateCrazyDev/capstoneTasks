package com.supportTicket.supportTicket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.PicturesComments;

@Repository
public interface PicturesCommentsRepo extends JpaRepository<PicturesComments, Long>{

}
