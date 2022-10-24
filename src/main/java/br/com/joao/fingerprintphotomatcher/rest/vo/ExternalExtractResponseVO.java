package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ExternalExtractResponseVO implements Serializable {

	private byte[] template;
	private Map<String, Integer> quality = new HashMap<>();
	private Map<String, byte[]> fingers = new HashMap<>();
	private byte[] faceToken;
	private String[] icaos;

	public ExternalExtractResponseVO(byte[] template, Map<String, Integer> quality, byte[] faceToken,
			String[] icaos) {
		super();
		this.template = template;
		this.quality = quality;
		this.fingers = new HashMap<String, byte[]>();
		this.faceToken = faceToken;
		this.icaos = icaos;
	}


	public void addWsqData(String bodyPart, byte[] data) {
		this.fingers.putIfAbsent(bodyPart, data);
	}

}
