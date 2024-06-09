package com.example.videohits.service;

import java.util.Collection;

import com.example.videohits.dto.HitDTO;
import com.example.videohits.dto.VideoHitDTO;

public interface HitService {

	void createHit(HitDTO hit);

	void saveVideoHits(Collection<VideoHitDTO> videoHits);
}
