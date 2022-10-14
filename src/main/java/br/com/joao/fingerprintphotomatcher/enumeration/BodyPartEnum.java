package br.com.joao.fingerprintphotomatcher.enumeration;

public enum BodyPartEnum {
	RIGHT_HAND_INDEX(2),
	RIGHT_HAND_MIDDLE(3),
	RIGHT_HAND_RING(4),
	RIGHT_HAND_PINKY(5),
	RIGHT_HAND_THUMB(1),
	LEFT_HAND_INDEX(7),
	LEFT_HAND_MIDDLE(8),
	LEFT_HAND_RING(9),
	LEFT_HAND_PINKY(10),
	LEFT_HAND_THUMB(6),
	FACE(null);

	Integer frictionRidgePosition;

	private BodyPartEnum(Integer frictionRidgePosition) {
		this.frictionRidgePosition = frictionRidgePosition;

	}

	public Integer getFRPCode() {
		return frictionRidgePosition;
	}

	public boolean isFinger() {
		return this != FACE;
	}
}
