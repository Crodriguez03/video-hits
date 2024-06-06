package com.example.videohits.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.videohits.dto.HitDTO;
import com.example.videohits.dto.VideoHitDTO;
import com.example.videohits.repository.VideoHitRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class HitServiceImpl implements HitService {
	
	@Value("${rabbitmq.exchange.video-hit}")
	private String exchangeVideoHit;
	
	private static final Deque<HitDTO> hitsDeque = new ArrayDeque<>();
	
	private final RabbitTemplate rabbitTemplate;
	
	private final VideoHitRepository videoHitRepository;
	
	private boolean flagDaemon = true;
	
	public HitServiceImpl(VideoHitRepository videoHitRepository, RabbitTemplate rabbitTemplate) {
		this.videoHitRepository = videoHitRepository;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void createHit(HitDTO hit) {
		hitsDeque.addLast(hit);
	}
	
	@PostConstruct
	private void init() {
		ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		});
		
		exec.execute(() -> {
			while (flagDaemon) {
				consumeHitsDeque();
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		
		exec.shutdown();
	}
	
	private void consumeHitsDeque() {
		if (hitsDeque.isEmpty()) {
			return;
		}
		
		Map<String, Integer> videoHits = new HashMap<>();
		final HitDTO lastHit = hitsDeque.getLast();
		HitDTO hitAux;
		do {
			hitAux = hitsDeque.poll();
			videoHits.compute(hitAux.getVideoId(), (k, v) -> v != null ? v + 1: 1);
		} while (lastHit != hitAux);
		
		videoHits.forEach(this::sendQueueVideoHits);
	}
	
	private void sendQueueVideoHits(String videoId, Integer hits) {
		rabbitTemplate.convertAndSend(exchangeVideoHit, "#", new VideoHitDTO(videoId, hits));
	}
	
	// Consumimos los hits que quedan en la cola antes de apagar
	@PreDestroy
	private void preDestroy() {
		flagDaemon = false;
		consumeHitsDeque();		
	}

	@Override
	public void saveVideoHit(VideoHitDTO videoHit) {
		videoHitRepository.upsert(videoHit.getVideoId(), videoHit.getCount());
	}
	
}
