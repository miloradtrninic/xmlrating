package com.amss.xmlrating.dto;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Setter @Getter
public class UserImpressionView {
	private Long id;
	private Long accommodationId;
	private Integer rating;
	private String comment;
	private String registeredUserUsername;
}
