package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import br.com.joao.fingerprintphotomatcher.enumeration.OperationEnum;
import br.com.joao.fingerprintphotomatcher.enumeration.ResultEnum;

public class MatchResponseVO implements Serializable {

	private String uuid;

	private ZonedDateTime datetime;

	private String description;

	private OperationEnum operation;

	private ResultEnum result;

	private Integer score;

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public ZonedDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(ZonedDateTime datetime) {
		this.datetime = datetime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OperationEnum getOperation() {
		return operation;
	}

	public void setOperation(OperationEnum operation) {
		this.operation = operation;
	}

	public ResultEnum getResult() {
		return result;
	}

	public void setResult(ResultEnum result) {
		this.result = result;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}
