package com.example.videohits.entity;

public class VideoHit {
	
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
