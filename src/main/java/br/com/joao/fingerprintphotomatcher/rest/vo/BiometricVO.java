package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;

public class BiometricVO implements Serializable {

	private BodyPartEnum bodyPart;
	private String data;
	private ZonedDateTime captureDate;

	public BiometricVO() {
	}

	public BiometricVO(BodyPartEnum bodyPart, String data, ZonedDateTime captureDate) {
		super();
		this.bodyPart = bodyPart;
		this.data = data;
		this.captureDate = captureDate;
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

	public ZonedDateTime getCaptureDate() {
		return captureDate;
	}

	public void setCapturaDate(ZonedDateTime captureDate) {
		this.captureDate = captureDate;
	}

}
