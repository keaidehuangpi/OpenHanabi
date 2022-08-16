package me.theresa.fontRenderer.font.util;

import java.io.InputStream;
import java.net.URL;

public interface SlickResourceLocation {

    InputStream getResourceAsStream(String ref);

    URL getResource(String ref);
}
