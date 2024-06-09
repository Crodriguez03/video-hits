package com.example.videohits.repository;

import java.util.List;

import com.example.videohits.entity.VideoHit;

public interface VideoHitRepository {
	void upsertBulk(List<VideoHit> videosHit);
}
