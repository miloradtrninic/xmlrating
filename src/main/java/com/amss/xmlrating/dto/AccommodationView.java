package com.amss.xmlrating.dto;

import java.util.Set;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Setter @Getter
public class AccommodationView {
	private Long id;
	private Long externalKey;
	private Double rating;
	private Set<UserImpressionView> userImpressions;
}
