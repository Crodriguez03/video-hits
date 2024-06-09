package com.example.videohits.repository;

import java.util.Collection;

import com.example.videohits.entity.VideoHit;

public interface VideoHitRepository {
	void upsertBulk(Collection<VideoHit> videoHits);
}
