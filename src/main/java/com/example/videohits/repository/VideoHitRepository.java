package com.example.videohits.repository;

public interface VideoHitRepository {

	void upsert(String videoId, Integer count);

}
