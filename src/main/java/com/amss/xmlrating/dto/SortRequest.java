package com.amss.xmlrating.dto;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Getter @Setter
public class SortRequest {
	private List<Long> ids;
	private String property;
	private Direction direction;
}
