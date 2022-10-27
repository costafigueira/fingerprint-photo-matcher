package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

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
public class BiometricDetailsVO implements Serializable {

	private String bodyPart;
	private Integer nfiq;
	private byte[] data;
	// private Integer minutiae;

}
