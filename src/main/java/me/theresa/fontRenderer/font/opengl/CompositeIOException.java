package me.theresa.fontRenderer.font.opengl;

import java.io.IOException;
import java.util.ArrayList;


public class CompositeIOException extends IOException {

	private final ArrayList exceptions = new ArrayList();

	public CompositeIOException() {
		super();
	}

	public void addException(Exception e) {
		exceptions.add(e);
	}

	public String getMessage() {
		StringBuilder msg = new StringBuilder("Composite Exception: \n");
		for (Object exception : exceptions) {
			msg.append("\t").append(((IOException) exception).getMessage()).append("\n");
		}
		
		return msg.toString();
	}
}
