
package me.theresa.fontRenderer.font.effect;

import java.util.List;


public interface ConfigurableEffect extends Effect {

    List getValues();

    void setValues(List values);

    interface Value {

        String getName();

        String getString();

        void setString(String value);

        Object getObject();

        void showDialog();
    }
}
