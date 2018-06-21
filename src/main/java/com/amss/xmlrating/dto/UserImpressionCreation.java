package com.amss.xmlrating.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Setter @Getter
public class UserImpressionCreation {
	private Long id;
	private Long accommodationId;
	private Integer rating;
	private String comment;
	private String registeredUserUsername;
}
