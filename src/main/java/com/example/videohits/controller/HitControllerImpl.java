package com.example.videohits.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.videohits.dto.HitDTO;
import com.example.videohits.service.HitService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("hit")
public class HitControllerImpl implements HitController {

	private final HitService userService;
	
	private final RestTemplate restTemplate;
	
	public HitControllerImpl(HitService userService, RestTemplate restTemplate) {
		this.userService = userService;
		this.restTemplate = restTemplate;
	}
	
	@Override
	@PostMapping
	public void createHit(@RequestBody HitDTO hit) {
		userService.createHit(hit);
	}
	
	
	
	
	@PostConstruct
	private void init() {
		ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		});
		
		exec.execute(() -> {
			
			// Damos tiempo a que arranque el proyecto antes de empezar a mandar peticiones de prueba
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}			
			
			RandomGenerator random = RandomGenerator.getDefault();

			List<String> videoIds = new ArrayList<>();
			IntStream.range(0, 20).forEach(i -> videoIds.add(ObjectId.get().toString()));

			while (true) {
				IntStream.range(0, 20).parallel().forEach(videoId -> restTemplate
						.postForLocation("http://localhost:8080/hit", new HitDTO(videoIds.get(random.nextInt(0, 20)))));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		
		exec.shutdown();
	}	
}
