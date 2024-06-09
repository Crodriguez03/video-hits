package com.example.videohits.queue;

import java.util.Set;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.videohits.dto.VideoHitDTO;
import com.example.videohits.service.HitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HitQueueListener {

	private final HitService hitService;
	
	private final ObjectMapper objectMapper;
	
	public HitQueueListener(HitService hitService, ObjectMapper objectMapper) {
		this.hitService = hitService;
		this.objectMapper = objectMapper;
	}
	
	@RabbitListener(queues = "${rabbitmq.queue.video-hit}", containerFactory = "prefetchRabbitListenerContainerFactory")
	public void handleEventUser(Message message) throws JsonProcessingException {
		
		String body = new String(message.getBody());		
		Set<VideoHitDTO> videoHits = objectMapper.readValue(body, new TypeReference<Set<VideoHitDTO>>(){});
		hitService.saveVideoHits(videoHits);
	}
}
