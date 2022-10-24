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
public class ExternalExtractionRequestVO implements Serializable {

	private List<ExtractorBiometricVO> biometrics;
	private String tcn;

	public ExternalExtractionRequestVO(List<ExtractorBiometricVO> biometrics) {
		this.biometrics = biometrics;
	}

	public void addBiometric(ExtractorBiometricVO biometricVO) {
		this.biometrics.add(biometricVO);
	}

}
