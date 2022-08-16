package me.theresa.fontRenderer.font.opengl;

import me.theresa.fontRenderer.font.log.Log;

import java.security.AccessController;
import java.security.PrivilegedAction;


public class ImageDataFactory {
	
	private static boolean usePngLoader = true;
	
	private static boolean pngLoaderPropertyChecked = false;
	
	
	private static final String PNG_LOADER = "org.newdawn.slick.pngloader";
	
	
	@SuppressWarnings("unchecked")
	private static void checkProperty() {
		if (!pngLoaderPropertyChecked) {
			pngLoaderPropertyChecked = true;

			try {
				AccessController.doPrivileged((PrivilegedAction) () -> {
                    String val = System.getProperty(PNG_LOADER);
                    if ("false".equalsIgnoreCase(val)) {
                        usePngLoader = false;
                    }

                    Log.info("Use Java PNG Loader = " + usePngLoader);
                    return null;
                });
			} catch (Throwable ignored) {}
		}
	}
	
	
	public static LoadableImageData getImageDataFor(String ref) {
		LoadableImageData imageData;
		checkProperty();
		
		ref = ref.toLowerCase();
		
        if (ref.endsWith(".tga")) {
        	return new TGAImageData();
        } 
        if (ref.endsWith(".png")) {
        	CompositeImageData data = new CompositeImageData();
        	if (usePngLoader) {
        		data.add(new PNGImageData());
        	}
        	data.add(new ImageIOImageData());
        	
        	return data;
        } 
        
        return new ImageIOImageData();
	}
}
