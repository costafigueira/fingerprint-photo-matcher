package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;

public class BiometricVO implements Serializable {

	private BodyPartEnum bodyPart;
	private String data;
	private boolean processImage;

	public BiometricVO() {
	}

	public BiometricVO(BodyPartEnum bodyPart, String data, boolean processImage) {
		super();
		this.bodyPart = bodyPart;
		this.data = data;
		this.processImage = processImage;
	}

	public BodyPartEnum getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(BodyPartEnum bodyPart) {
		this.bodyPart = bodyPart;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isFace() {
		return bodyPart == BodyPartEnum.FACE;
	}

	public boolean isProcessImage() {
		return processImage;
	}

	public void setProcessImage(boolean processImage) {
		this.processImage = processImage;
	}

}