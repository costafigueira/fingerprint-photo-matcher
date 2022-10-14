package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

public class ImageRequestVO implements Serializable {

	private String image;

	public ImageRequestVO() {
	}

	public ImageRequestVO(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
