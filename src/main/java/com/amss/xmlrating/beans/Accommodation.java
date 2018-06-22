package com.amss.xmlrating.beans;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @NoArgsConstructor @AllArgsConstructor @Setter @Getter
public class Accommodation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Double rating;
	private Long externalKey;
	
	@OneToMany(mappedBy="accommodation", orphanRemoval=true)
	private Set<UserImpression> userImpressions;
}
