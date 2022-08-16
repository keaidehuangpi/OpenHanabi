package me.theresa.fontRenderer.font.opengl;

import me.theresa.fontRenderer.font.log.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CompositeImageData implements LoadableImageData {

	private final ArrayList sources = new ArrayList();

	private LoadableImageData picked;


	public void add(LoadableImageData data) {
		sources.add(data);
	}


	public ByteBuffer loadImage(InputStream fis) throws IOException {
		return loadImage(fis, false, null);
	}

	
	public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException {
		return loadImage(fis, flipped, false, transparent);
	}

	
	public ByteBuffer loadImage(InputStream is, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
		CompositeIOException exception = new CompositeIOException();
		ByteBuffer buffer = null;
		
		BufferedInputStream in = new BufferedInputStream(is, is.available());
		in.mark(is.available());
		
		// cycle through our source until one of them works
		for (Object source : sources) {
			in.reset();
			try {
				LoadableImageData data = (LoadableImageData) source;

				buffer = data.loadImage(in, flipped, forceAlpha, transparent);
				picked = data;
				break;
			} catch (Exception e) {
				Log.warn(source.getClass() + " failed to read the data", e);
				exception.addException(e);
			}
		}
		
		if (picked == null) {
			throw exception;
		}
		
		return buffer;
	}

	
	private void checkPicked() {
		if (picked == null) {
			throw new RuntimeException("Attempt to make use of uninitialised or invalid composite image data");
		}
	}
	
	
	public int getDepth() {
		checkPicked();
		
		return picked.getDepth();
	}

	
	public int getHeight() {
		checkPicked();
		
		return picked.getHeight();
	}

	
	public ByteBuffer getImageBufferData() {
		checkPicked();
		
		return picked.getImageBufferData();
	}

	
	public int getTexHeight() {
		checkPicked();
		
		return picked.getTexHeight();
	}

	public int getTexWidth() {
		checkPicked();
		
		return picked.getTexWidth();
	}

	public int getWidth() {
		checkPicked();
		
		return picked.getWidth();
	}
	
	public void configureEdging(boolean edging) {
		for (Object source : sources) {
			((LoadableImageData) source).configureEdging(edging);
		}
	}

}
