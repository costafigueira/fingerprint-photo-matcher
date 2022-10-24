package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
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
public class ExtractRequestVO implements Serializable {

	private List<BiometricVO> biometrics;
	private boolean evaluateQuality;

	public ExtractRequestVO(List<BiometricVO> biometrics) {
		super();
		this.biometrics = biometrics;
		this.evaluateQuality = true;
	}

	public void addBiometric(BiometricVO biometricVO) {
		this.biometrics.add(biometricVO);
	}
}
