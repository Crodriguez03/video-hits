package com.example.videohits.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.videohits.entity.VideoHit;

public class VideoHitRepositoryImpl implements VideoHitRepository {

	private MongoTemplate template;

	private static final String ID_FIELD = "_id";
	private static final String COUNT_FIELD = "count";
	
	public VideoHitRepositoryImpl(MongoTemplate template) {
		this.template = template;
	}
	
	@Override
	public void upsert(String videoId, Integer count) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID_FIELD).is(videoId));
		Update update = new Update();
		update.inc(COUNT_FIELD, count);
		
		template.upsert(query, update, VideoHit.class);
	}
}
