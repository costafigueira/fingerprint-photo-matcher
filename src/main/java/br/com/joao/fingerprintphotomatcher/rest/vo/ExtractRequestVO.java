package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExtractRequestVO implements Serializable {

	private List<BiometricVO> biometrics;

	public ExtractRequestVO() {
		this.biometrics = new ArrayList<>();
	}

	public ExtractRequestVO(List<BiometricVO> biometrics) {
		super();
		this.biometrics = biometrics;
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
