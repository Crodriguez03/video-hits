package com.example.videohits.service;

import com.example.videohits.dto.HitDTO;
import com.example.videohits.dto.VideoHitDTO;

public interface HitService {

	void createHit(HitDTO hit);

	void saveVideoHit(VideoHitDTO videoHit);
}
