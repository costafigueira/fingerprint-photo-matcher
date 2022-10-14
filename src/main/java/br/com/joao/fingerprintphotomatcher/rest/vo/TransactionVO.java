package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionVO implements Serializable {

	private List<BiometricVO> biometrics;
	private String tcn;

	public TransactionVO() {
		this.biometrics = new ArrayList<>();
	}

	public TransactionVO(List<BiometricVO> biometrics, String tcn) {
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

	public List<BiometricVO> getBiometrics() {
		return biometrics;
	}

	public void setBiometrics(List<BiometricVO> biometrics) {
		this.biometrics = biometrics;
	}

	public void addBiometric(BiometricVO biometricVO) {
		this.biometrics.add(biometricVO);
	}

}
