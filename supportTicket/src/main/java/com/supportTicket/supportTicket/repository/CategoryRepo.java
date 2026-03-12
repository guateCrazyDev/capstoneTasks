package com.supportTicket.supportTicket.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supportTicket.supportTicket.model.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long>{
	public Category findByCategoryName (String categoryName);
}
