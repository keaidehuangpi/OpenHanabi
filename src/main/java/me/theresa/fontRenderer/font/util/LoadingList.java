package me.theresa.fontRenderer.font.util;

import me.theresa.fontRenderer.font.log.Log;
import me.theresa.fontRenderer.font.opengl.InternalTextureLoader;

import java.util.ArrayList;


public class LoadingList {
	
	private static LoadingList single = new LoadingList();
	
	
	public static LoadingList get() {
		return single;
	}
	
	
	public static void setDeferredLoading(boolean loading) {
		single = new LoadingList();
		InternalTextureLoader.get().setDeferredLoading(loading);
	}


	public static boolean isDeferredLoading() {
		return InternalTextureLoader.get().isDeferredLoading();
	}


	private final ArrayList deferred = new ArrayList();

	private int total;


	private LoadingList() {
	}


	public void add(DeferredResource resource) {
		total++;
		deferred.add(resource);
	}
	
	
	public void remove(DeferredResource resource) {
		Log.info("Early loading of deferred resource due to req: "+resource.getDescription());
		total--;
		deferred.remove(resource);
	}
	
	
	public int getTotalResources() {
		return total;
	}
	
	
	public int getRemainingResources() {
		return deferred.size();
	}
	
	public DeferredResource getNext() {
		if (deferred.size() == 0) {
			return null;
		}
		
		return (DeferredResource) deferred.remove(0);
	}
}
