package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;

import br.com.joao.fingerprintphotomatcher.enumeration.OperationEnum;
import br.com.joao.fingerprintphotomatcher.enumeration.ResultEnum;
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
public class MatchResponseVO implements Serializable {

	private OperationEnum operation;

	private ResultEnum result;

	private Integer score;

	private String expectedResult;

	private String description;

}
