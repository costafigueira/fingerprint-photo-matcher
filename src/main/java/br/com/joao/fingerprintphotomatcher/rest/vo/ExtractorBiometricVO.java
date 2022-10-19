package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;

public class ExtractorBiometricVO implements Serializable {

	private BodyPartEnum bodyPart;
	private String data;

	public ExtractorBiometricVO() {
	}

	public ExtractorBiometricVO(BodyPartEnum bodyPart, String data) {
		super();
		this.bodyPart = bodyPart;
		this.data = data;
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

}
