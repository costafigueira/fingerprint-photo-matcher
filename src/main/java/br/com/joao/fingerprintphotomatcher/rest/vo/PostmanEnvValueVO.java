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
public class PostmanEnvValueVO implements Serializable {

	private String key;
	private String value;
	private String type = "default";
	private boolean enabled = true;

}
