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
public class ProcessedFilesReport implements Serializable {

	private int numberOfFiles;
	private int amountOfNfiq1;
	private int amountOfNfiq2;
	private int amountOfNfiq3;
	private int amountOfNfiq4;
	private int amountOfNfiq5;
}
