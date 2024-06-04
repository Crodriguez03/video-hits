package com.example.videohits.dto;

public class HitDTO {

	private final String videoId;

	public HitDTO() {
		videoId = null;
	}
	
	public HitDTO(String videoId) {
		this.videoId = videoId;
	}
	
	public String getVideoId() {
		return videoId;
	}
}
