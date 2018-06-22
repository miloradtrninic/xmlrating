package com.amss.xmlrating.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.amss.xmlrating.beans.Accommodation;

@Repository
@Transactional
public interface AccommodationRepo extends PagingAndSortingRepository<Accommodation, Long> {
	Iterable<Accommodation> findByExternalKeyIn(List<Long> externals, Sort sort);
	Iterable<Accommodation> findByExternalKeyIn(List<Long> externals);
	Optional<Accommodation> findByExternalKey(Long externalKey);
}
