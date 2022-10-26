package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class ExtractResponseVO implements Serializable {

	private List<BiometricDetailsVO> biometrics = new ArrayList<>();
	private byte[] template;

	public ExtractResponseVO(byte[] template) {
		this.template = template;
	}

}
