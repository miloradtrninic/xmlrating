package com.amss.xmlrating.beans;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @NoArgsConstructor @Setter @Getter
public class Accommodation {
	@Id
	private Long id;
	private Double rating;
	private Long externalKey;
	
	@OneToMany(mappedBy="accommodation", orphanRemoval=true)
	private Set<UserImpression> userImpressions;
}
