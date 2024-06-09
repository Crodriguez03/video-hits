package com.example.videohits.service;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.videohits.dto.HitDTO;
import com.example.videohits.dto.VideoHitDTO;
import com.example.videohits.entity.VideoHit;
import com.example.videohits.repository.VideoHitRepository;
import com.example.videohits.utils.ListUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class HitServiceImpl implements HitService {
	
	@Value("${rabbitmq.exchange.video-hit}")
	private String exchangeVideoHit;
	
	private static final Deque<HitDTO> hitsDeque = new ConcurrentLinkedDeque<>();
	
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
		
		Map<String, Integer> mapVideoHits = new HashMap<>();
		final HitDTO lastHit = hitsDeque.getLast();
		HitDTO hitAux;
		do {
			hitAux = hitsDeque.poll();
			mapVideoHits.compute(hitAux.getVideoId(), (k, v) -> v != null ? v + 1: 1);
		} while (lastHit != hitAux);
		
		sendQueueVideoHits(mapVideoHits.entrySet().stream().map(entry -> new VideoHitDTO(entry.getKey(), entry.getValue())).toList());
		
	}
	
	
	private void sendQueueVideoHits(List<VideoHitDTO> videoHits) {
		
		// Si la lista es muy grande la dividimos en listas más pequeñas para no exceder 
		// el máximo de tamaño de un mensaje en rabbitmq
		if (videoHits.size() > 50) {
			List<List<VideoHitDTO>> subLists = ListUtils.chopped(videoHits, 50);
			subLists.stream().forEach(subList -> rabbitTemplate.convertAndSend(exchangeVideoHit, "#", subList));
		} else {
			rabbitTemplate.convertAndSend(exchangeVideoHit, "#", videoHits);
		}
	}
	
	// Consumimos los hits que quedan en la cola antes de apagar
	@PreDestroy
	private void preDestroy() {
		flagDaemon = false;
		consumeHitsDeque();		
	}

	@Override
	public void saveVideoHits(Collection<VideoHitDTO> videoHitsDTO) {
		List<VideoHit> videoHits = videoHitsDTO.stream().map(videoHit -> new VideoHit(videoHit.getVideoId(), videoHit.getCount())).toList();
		videoHitRepository.upsertBulk(videoHits);
	}
	
}
