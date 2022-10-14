package br.com.joao.fingerprintphotomatcher.rest.vo;

import java.util.Optional;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;

public class BodyPartDetailVO {
	// private NBiometricStatus status;
	private BodyPartEnum bodyPart;
	private Optional<Integer> quality = Optional.of(0);
	private Optional<Integer> minutiate = Optional.of(0);

	public BodyPartDetailVO(BodyPartEnum bodyPart) {
		this.bodyPart = bodyPart;
	}

	// public void setStatus(NBiometricStatus status) {
	// 	this.status = status;
	// }

	// public NBiometricStatus getStatus() {
	// 	return status;
	// }

	// public boolean isOk() {
	// 	return status == NBiometricStatus.OK;
	// }

	// public boolean isNotOk() {
	// 	return !isOk();
	// }

	public void setQuality(Integer quality) {
		this.quality = Optional.ofNullable(quality);
	}

	public BodyPartEnum getBodyPart() {
		return bodyPart;
	}

	public Optional<Integer> getQuality() {
		return quality;
	}

	public Optional<Integer> getMinutiate() {
		return minutiate;
	}

	public void setMinutiate(Optional<Integer> minutiate) {
		this.minutiate = minutiate;
	}

	@Override
	public String toString() {
		return "BodyPartDetails [bodyPart=" + bodyPart + ", quality=" + quality + ", minutiate=" + minutiate + "]";
	}

}
