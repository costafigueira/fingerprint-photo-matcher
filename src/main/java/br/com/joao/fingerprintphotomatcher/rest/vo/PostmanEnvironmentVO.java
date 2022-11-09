package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class PostmanEnvironmentVO implements Serializable {

	private String id = "808ac490-9b32-4be5-853d-d6d023f38dc9";
	private String name = "Fingerprint Photo Match Generated";
	private List<PostmanEnvValueVO> values = new ArrayList<>();
	private String _postman_variable_scope = "environment";

}
