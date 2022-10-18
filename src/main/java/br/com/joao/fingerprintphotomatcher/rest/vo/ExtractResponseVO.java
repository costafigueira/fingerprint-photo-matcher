package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExtractResponseVO implements Serializable {

	private byte[] template;
	private Map<String, Integer> quality = new HashMap<>();
	private Map<String, byte[]> fingers = new HashMap<>();
	private byte[] faceToken;
	private String[] icaos;

	public ExtractResponseVO() {
	}

	public ExtractResponseVO(byte[] template, Map<String, Integer> quality, byte[] faceToken,
			String[] icaos) {
		super();
		this.template = template;
		this.quality = quality;
		this.fingers = new HashMap<String, byte[]>();
		this.faceToken = faceToken;
		this.icaos = icaos;
	}

	public String[] getIcaos() {
		return icaos;
	}

	public void setIcaos(String[] icaos) {
		this.icaos = icaos;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}

	public byte[] getTemplate() {
		return template;
	}

	public Map<String, Integer> getQuality() {
		return quality;
	}
	public void setQuality(Map<String, Integer> quality) {
		this.quality = quality;
	}

	public byte[] getFaceToken() {
		return faceToken;
	}

	public void setFaceToken(byte[] faceToken) {
		this.faceToken = faceToken;
	}

	public Map<String, byte[]> getFingers() {
		return fingers;
	}

	public void setFingers(Map<String, byte[]> fingers) {
		this.fingers = fingers;
	}

	public void addWsqData(String bodyPart, byte[] data) {
		this.fingers.putIfAbsent(bodyPart, data);
	}

}
