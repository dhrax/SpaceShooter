package com.daisa.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.daisa.Juego;
import com.daisa.MainMenuScreen;
import com.daisa.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 600;
		config.height = 800;
		config.resizable = false;

		new LwjglApplication(new Juego(), config);
	}
}
