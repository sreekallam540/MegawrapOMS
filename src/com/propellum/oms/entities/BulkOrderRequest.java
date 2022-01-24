package com.propellum.oms.entities;

import org.springframework.web.multipart.MultipartFile;

public class BulkOrderRequest {

	private MultipartFile multiPartFile;

	public MultipartFile getMultiPartFile() {
		return multiPartFile;
	}

	public void setMultiPartFile(MultipartFile multiPartFile) {
		this.multiPartFile = multiPartFile;
	}
	
	
	
}
