package com.example.videohits.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class VideoHit {
	
	@Id
	private final String videoId;
	private final Integer count;
	
	public VideoHit(String videoId, Integer count) {
		this.videoId = videoId;
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}

	public String getVideoId() {
		return videoId;
	}
}
