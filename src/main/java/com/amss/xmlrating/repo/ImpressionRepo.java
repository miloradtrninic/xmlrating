package com.amss.xmlrating.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.amss.xmlrating.beans.UserImpression;

@Repository
@Transactional
public interface ImpressionRepo extends PagingAndSortingRepository<UserImpression, Long> {
	List<UserImpression> findAllByRating(Integer rating);
}
