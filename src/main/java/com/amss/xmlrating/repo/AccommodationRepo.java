package com.amss.xmlrating.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.amss.xmlrating.beans.Accommodation;

@Repository
@Transactional
public interface AccommodationRepo extends PagingAndSortingRepository<Accommodation, Long> {
	List<Accommodation> findByExternalKeyIn(List<Long> externals, Sort sort);
	List<Accommodation> findByExternalKeyIn(List<Long> externals);
}
