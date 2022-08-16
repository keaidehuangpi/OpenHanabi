package me.theresa.fontRenderer.font.log;


public class FastTrig {
   
	private static double reduceSinAngle(double radians) {
		double orig = radians;
		radians %= Math.PI * 2.0; // put us in -2PI to +2PI space
		if (Math.abs(radians) > Math.PI) { // put us in -PI to +PI space
			radians = radians - (Math.PI * 2.0);
		}
		if (Math.abs(radians) > Math.PI / 2) {// put us in -PI/2 to +PI/2 space
			radians = Math.PI - radians;
		}

		return radians;
	}
	
	public static double sin(double radians) {
		radians = reduceSinAngle(radians); // limits angle to between -PI/2 and +PI/2
		if (Math.abs(radians) <= Math.PI / 4) {
			return Math.sin(radians);
		} else {
			return Math.cos(Math.PI / 2 - radians);
		}
	}
	
	public static double cos(double radians) {
		return sin(radians + Math.PI / 2);
	}

}
