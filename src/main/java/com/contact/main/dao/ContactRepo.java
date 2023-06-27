package com.contact.main.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.main.entities.Contact;

import jakarta.transaction.Transactional;

public interface ContactRepo extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);//pagination showing per page 4=n contacts and current page=0
	
	@Modifying
	@Transactional
	@Query(value="delete from Contact c where c.cid=?1")
	void deleteByIdCustom(Integer cId);
}	
