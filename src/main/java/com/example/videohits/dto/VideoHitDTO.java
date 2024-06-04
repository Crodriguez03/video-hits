package com.example.videohits.dto;

import java.io.Serializable;

public class VideoHitDTO implements Serializable {

	private static final long serialVersionUID = -4451871853557954355L;
	
	private final String videoId;
	private final Integer count;
	
	public VideoHitDTO(String videoId, Integer count) {
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
