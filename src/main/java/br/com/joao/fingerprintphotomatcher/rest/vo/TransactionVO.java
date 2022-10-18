package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionVO implements Serializable {

	private List<ExtractorBiometricVO> biometrics;
	private String tcn;

	public TransactionVO() {
		this.biometrics = new ArrayList<>();
	}

	public TransactionVO(List<ExtractorBiometricVO> biometrics, String tcn) {
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
