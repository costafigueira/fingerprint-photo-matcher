package br.com.joao.fingerprintphotomatcher.util;

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
public class VerifyReport implements Serializable {

	private int numberOfVerifies;

	private int amountOfNoMatches;
	private int expectedNoMatches;
	private double hitPercentageOfNoMatch;

	private int amountOfMatches;
	private int expectedMatches;
	private double hitPercentageOfMatch;

	private double overallHitPercentage;
}
