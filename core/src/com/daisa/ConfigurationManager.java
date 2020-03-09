package com.daisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ConfigurationManager {

    public static Preferences prefs = Gdx.app.getPreferences("Preferencias");

    public static boolean isSoundEnabled() {

        return prefs.getBoolean("sonido");
    }
}
