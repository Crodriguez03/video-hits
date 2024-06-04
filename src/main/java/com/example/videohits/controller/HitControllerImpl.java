package com.example.videohits.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.videohits.dto.HitDTO;
import com.example.videohits.service.HitService;

@RestController
@RequestMapping("hit")
public class HitControllerImpl implements HitController {

	private HitService userService;
	
	public HitControllerImpl(HitService userService) {
		this.userService = userService;
	}
	
	@Override
	@PostMapping
	public void createHit(@RequestBody HitDTO hit) {
		userService.createHit(hit);
	}
	
}
