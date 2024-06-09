package com.example.videohits.repository;

import java.util.Collection;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.example.videohits.entity.VideoHit;

@Repository
public class VideoHitRepositoryImpl implements VideoHitRepository {

	private MongoTemplate template;

	private static final String ID_FIELD = "videoId";
	private static final String COUNT_FIELD = "count";
	
	public VideoHitRepositoryImpl(MongoTemplate template) {
		this.template = template;
	}

	@Override
	public void upsertBulk(Collection<VideoHit> videoHits) {
		BulkOperations bulk = template.bulkOps(BulkMode.UNORDERED, VideoHit.class);
		bulk.upsert(videoHits.stream().map(this::createPair).toList()).execute();
	}
	
	private Pair<Query, Update> createPair(VideoHit videoHit) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID_FIELD).is(videoHit.getVideoId()));
		Update update = new Update();
		update.inc(COUNT_FIELD, videoHit.getCount());
		
		return Pair.of(query, update);		
	}
}
