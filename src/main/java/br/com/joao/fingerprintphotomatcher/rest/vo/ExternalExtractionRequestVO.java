package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExternalExtractionRequestVO implements Serializable {

	private List<ExtractorBiometricVO> biometrics;
	private String tcn;

	public ExternalExtractionRequestVO() {
		this.biometrics = new ArrayList<>();
	}

	public ExternalExtractionRequestVO(List<ExtractorBiometricVO> biometrics) {
		this.biometrics = biometrics;
	}

	public ExternalExtractionRequestVO(List<ExtractorBiometricVO> biometrics, String tcn) {
		super();
		this.biometrics = biometrics;
		this.tcn = tcn;
	}

	public String getTcn() {
		return tcn;
	}

	public void setTcn(String tcn) {
		this.tcn = tcn;
	}

	public List<ExtractorBiometricVO> getBiometrics() {
		return biometrics;
	}

	public void setBiometrics(List<ExtractorBiometricVO> biometrics) {
		this.biometrics = biometrics;
	}

	public void addBiometric(ExtractorBiometricVO biometricVO) {
		this.biometrics.add(biometricVO);
	}

}
