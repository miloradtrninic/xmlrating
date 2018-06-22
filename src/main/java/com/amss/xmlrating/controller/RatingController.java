package com.amss.xmlrating.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.amss.xmlrating.beans.Accommodation;
import com.amss.xmlrating.beans.UserImpression;
import com.amss.xmlrating.dto.AccommodationCreation;
import com.amss.xmlrating.dto.AccommodationView;
import com.amss.xmlrating.dto.UserImpressionCreation;
import com.amss.xmlrating.dto.UserImpressionView;
import com.amss.xmlrating.repo.AccommodationRepo;
import com.amss.xmlrating.repo.ImpressionRepo;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RatingController {
	@Autowired
	AccommodationRepo accommodationRepo;
	
	@Autowired
	ImpressionRepo impressionRepo;
	
	@Autowired
	ModelMapper mapper;
	
	@GetMapping(value="/accommodation", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> accommodation(@RequestParam("id") Long id) {
		Optional<Accommodation> accommodation = accommodationRepo.findByExternalKey(id);
		if(accommodation.isPresent())
			return new ResponseEntity<AccommodationView>(mapper.map(accommodation.get(), AccommodationView.class), HttpStatus.OK);
		else {
			AccommodationView acc = new AccommodationView();
			acc.setExternalKey(id);
			acc.setRating(0.0);
			acc.setId(0l);
			acc.setUserImpressions(new HashSet<>());
			return new ResponseEntity<AccommodationView>(acc, HttpStatus.OK);
		}
	}
	@GetMapping(value="/accommodation/all", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Page<AccommodationView>> accommodationAll(@NotNull Pageable page) {
		
		return new ResponseEntity<Page<AccommodationView>>(accommodationRepo.findAll(page).map(a -> mapper.map(a, AccommodationView.class)), HttpStatus.OK);
	}
	
	@PostMapping(value="/accommodation/all/in", produces=MediaType.APPLICATION_JSON_UTF8_VALUE) 
	public ResponseEntity<List<AccommodationView>> accommodationAllIn(@RequestBody List<Long> ids) {
		Iterable<Accommodation> accsIter =  accommodationRepo.findByExternalKeyIn(ids);
		ArrayList<AccommodationView> responseList = new ArrayList<>(); 
		accsIter.forEach(a -> responseList.add(mapper.map(a, AccommodationView.class)));
		return new ResponseEntity<List<AccommodationView>>(responseList, HttpStatus.OK);
	}
	
	@PostMapping(value="/accommodation/new", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> accommodationNew(@RequestBody AccommodationCreation newEnt) {
		Accommodation accommodation = new Accommodation();
		accommodation.setId(newEnt.getId());
		accommodation.setRating(newEnt.getRating());
		accommodation = accommodationRepo.save(accommodation);
		return new ResponseEntity<AccommodationView>(mapper.map(accommodation, AccommodationView.class), HttpStatus.OK);
	}
	
	
	
	@PostMapping(value="/impression/new", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> impressionNew(@RequestBody UserImpressionCreation newEnt) {
		Accommodation acc = null;
		Optional<Accommodation> accommodation = accommodationRepo.findByExternalKey(newEnt.getAccommodationId());
		if(!accommodation.isPresent()) {
			acc = new Accommodation();
			acc.setExternalKey(newEnt.getAccommodationId());
			acc.setRating(0.0);
			acc.setUserImpressions(new HashSet<>());
			accommodationRepo.save(acc);
		} else {
			acc = accommodation.get();
		}
		UserImpression userImpression = new UserImpression();
		userImpression.setAccommodation(acc);
		userImpression.setComment(newEnt.getComment());
		userImpression.setRating(newEnt.getRating());
		userImpression.setRegisteredUserUsername(newEnt.getRegisteredUserUsername());
		userImpression = impressionRepo.save(userImpression);
		Integer ratingSum = 0;
		if(acc.getUserImpressions().size() > 0) {
			ratingSum = acc.getUserImpressions().stream().mapToInt(i -> i.getRating()).sum();
		}
		double rating = 0;
    	if(ratingSum > 0) {
    		rating = ratingSum * 1.0 / acc.getUserImpressions().size();
    		acc.setRating(rating);
    		accommodationRepo.save(acc);
    	}
    	UserImpressionView response = mapper.map(userImpression, UserImpressionView.class);
    	response.setAverageRating(rating);
		return new ResponseEntity<UserImpressionView>(response, HttpStatus.OK);
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
