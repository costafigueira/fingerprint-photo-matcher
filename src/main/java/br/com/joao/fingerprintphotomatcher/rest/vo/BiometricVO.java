package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;
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
public class BiometricVO implements Serializable {

	private BodyPartEnum bodyPart;
	private String data;
	private boolean processImage;

}
