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
public class MatchRequestVO implements Serializable {

	private ExtractRequestVO template1;
	private ExtractRequestVO template2;

}
