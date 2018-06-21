package com.amss.xmlrating.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.amss.xmlrating.beans.Accommodation;
import com.amss.xmlrating.beans.UserImpression;
import com.amss.xmlrating.dto.AccommodationCreation;
import com.amss.xmlrating.dto.AccommodationView;
import com.amss.xmlrating.dto.SortRequest;
import com.amss.xmlrating.dto.SortResponse;
import com.amss.xmlrating.dto.UserImpressionCreation;
import com.amss.xmlrating.dto.UserImpressionView;
import com.amss.xmlrating.repo.AccommodationRepo;
import com.amss.xmlrating.repo.ImpressionRepo;

@RestController
public class RatingController {
	@Autowired
	AccommodationRepo accommodationRepo;
	
	@Autowired
	ImpressionRepo impressionRepo;
	
	@Autowired
	ModelMapper mapper;
	
	@GetMapping(value="/accommodation", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> accommodation(@RequestParam("id") Long id) {
		Optional<Accommodation> accommodation = accommodationRepo.findById(id);
		if(accommodation.isPresent())
			return new ResponseEntity<AccommodationView>(mapper.map(accommodation.get(), AccommodationView.class), HttpStatus.OK);
		else 
			return new ResponseEntity<AccommodationView>(HttpStatus.BAD_REQUEST);
	}
	@GetMapping(value="/accommodation/all", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Page<AccommodationView>> accommodationAll(@NotNull Pageable page) {
		return new ResponseEntity<Page<AccommodationView>>(accommodationRepo.findAll(page).map(a -> mapper.map(a, AccommodationView.class)), HttpStatus.OK);
	}
	
	@PostMapping(value="/accommodation/all/in", produces=MediaType.APPLICATION_JSON_UTF8_VALUE) 
	public ResponseEntity<List<AccommodationView>> accommodationAllIn(List<Long> ids) {
		return new ResponseEntity<List<AccommodationView>>(
				accommodationRepo
					.findByExternalKeyIn(ids)
					.stream()
					.map(a -> mapper.map(a, AccommodationView.class))
					.collect(Collectors.toList()), HttpStatus.OK);
	}
	
	@PostMapping(value="/accommodation/new", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> accommodationNew(AccommodationCreation newEnt) {
		Accommodation accommodation = new Accommodation();
		accommodation.setId(newEnt.getId());
		accommodation.setRating(newEnt.getRating());
		accommodation = accommodationRepo.save(accommodation);
		return new ResponseEntity<AccommodationView>(mapper.map(accommodation, AccommodationView.class), HttpStatus.OK);
	}
	@PostMapping(value="/accommodation/sort",
			consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> sortById(SortRequest sortRequest) {
		if(!CollectionUtils.isEmpty(sortRequest.getIds())) {
			List<Accommodation> accs = accommodationRepo.findByExternalKeyIn(sortRequest.getIds(),
					new Sort(sortRequest.getDirection(), sortRequest.getProperty()));
			List<SortResponse> sorts = new ArrayList<>();
			for(int i = 0; i < accs.size(); i++) {
				sorts.add(new SortResponse(i, accs.get(i).getExternalKey()));
			}
			return new ResponseEntity<List<SortResponse>>(sorts, HttpStatus.OK);
		}
		return ResponseEntity.badRequest().build();
	}
	
	
	@PostMapping(value="/impression/new", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> impressionNew(UserImpressionCreation newEnt) {
		Optional<Accommodation> accommodation = accommodationRepo.findById(newEnt.getAccommodationId());
		if(accommodation.isPresent()) {
			UserImpression userImpression = new UserImpression();
			userImpression.setAccommodation(accommodation.get());
			userImpression.setComment(newEnt.getComment());
			userImpression.setRating(newEnt.getRating());
			userImpression.setRegisteredUserUsername(newEnt.getRegisteredUserUsername());
			userImpression = impressionRepo.save(userImpression);
			Integer ratingSum = accommodation.get().getUserImpressions().stream().mapToInt(i -> i.getRating()).sum();
	    	if(ratingSum > 0) {
	    		accommodation.get().setRating(ratingSum * 1.0 / accommodation.get().getUserImpressions().size());
	    		accommodationRepo.save(accommodation.get());
	    	}
			return new ResponseEntity<UserImpressionView>(mapper.map(userImpression, UserImpressionView.class), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<UserImpressionView>(HttpStatus.BAD_REQUEST);
		}
	}
	
	

	
	/**
	 * (Optional) App Engine health check endpoint mapping.
	 * @see <a href="https://cloud.google.com/appengine/docs/flexible/java/how-instances-are-managed#health_checking"></a>
	 * If your app does not handle health checks, a HTTP 404 response is interpreted
	 *     as a successful reply.
	 */
	@RequestMapping("/_ah/health")
	public String insertRating() {
		// Message body required though ignored
		return "Still surviving.";
	}

}
