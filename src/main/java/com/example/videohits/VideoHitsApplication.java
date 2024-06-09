package com.example.videohits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoHitsApplication {

	public static void main(String[] args) {
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","20");
		SpringApplication.run(VideoHitsApplication.class, args);
	}

}
