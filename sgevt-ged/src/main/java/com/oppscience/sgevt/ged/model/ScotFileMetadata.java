package com.oppscience.sgevt.ged.model;

public class ScotFileMetadata {

//	private Long id;
	private String originalURL;
	private String type;
	private String fileName;
	
	public ScotFileMetadata() {
		// TODO Auto-generated constructor stub
	}
	
    public ScotFileMetadata(String originalURL, String type, String fileName) {
		super();
		this.originalURL = originalURL;
		this.type = type;
		this.fileName = fileName;
	}

	public String getOriginalURL() {
		return originalURL;
	}

	public void setOriginalURL(String originalURL) {
		this.originalURL = originalURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
