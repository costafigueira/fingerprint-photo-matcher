package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

public class ExternalMatchRequestVO implements Serializable {

	private byte[] template1;
	private byte[] template2;

	public ExternalMatchRequestVO() {
	}

	public ExternalMatchRequestVO(byte[] template1, byte[] template2) {
		this.template1 = template1;
		this.template2 = template2;
	}

	public byte[] getTemplate1() {
		return template1;
	}

	public void setTemplate1(byte[] template1) {
		this.template1 = template1;
	}

	public byte[] getTemplate2() {
		return template2;
	}

	public void setTemplate2(byte[] template2) {
		this.template2 = template2;
	}

}
