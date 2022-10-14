package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;

public class ExtractResponseVO implements Serializable {

	private byte[] template;
	private Map<BodyPartEnum, BodyPartDetailVO> bodyParts;
	private byte[] faceToken;
	private String[] icaos;
	private Map<String, byte[]> fingers = new HashMap<>();

	public ExtractResponseVO() {
	}

	public ExtractResponseVO(byte[] template, Map<BodyPartEnum, BodyPartDetailVO> bodyParts, byte[] faceToken,
			String[] icaos,
			Map<String, byte[]> fingers) {
		super();
		this.template = template;
		this.bodyParts = bodyParts;
		this.faceToken = faceToken;
		this.icaos = icaos;
		this.fingers = fingers;
	}

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}

	public Map<BodyPartEnum, BodyPartDetailVO> getBodyParts() {
		return bodyParts;
	}

	public void setBodyParts(Map<BodyPartEnum, BodyPartDetailVO> bodyParts) {
		this.bodyParts = bodyParts;
	}

	public byte[] getFaceToken() {
		return faceToken;
	}

	public void setFaceToken(byte[] faceToken) {
		this.faceToken = faceToken;
	}

	public String[] getIcaos() {
		return icaos;
	}

	public void setIcaos(String[] icaos) {
		this.icaos = icaos;
	}

	public Map<String, byte[]> getFingers() {
		return fingers;
	}

	public void setFingers(Map<String, byte[]> fingers) {
		this.fingers = fingers;
	}

}
