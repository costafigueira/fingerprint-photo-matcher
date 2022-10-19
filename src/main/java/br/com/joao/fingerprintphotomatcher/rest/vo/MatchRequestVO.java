package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

public class MatchRequestVO implements Serializable {

	private ExtractRequestVO template1;
	private ExtractRequestVO template2;

	public MatchRequestVO() {
	}

	public MatchRequestVO(ExtractRequestVO template1, ExtractRequestVO template2) {
		this.template1 = template1;
		this.template2 = template2;
	}

	public ExtractRequestVO getTemplate1() {
		return template1;
	}

	public void setTemplate1(ExtractRequestVO template1) {
		this.template1 = template1;
	}

	public ExtractRequestVO getTemplate2() {
		return template2;
	}

	public void setTemplate2(ExtractRequestVO template2) {
		this.template2 = template2;
	}

}
