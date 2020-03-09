package com.daisa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Juego extends Game {

    BitmapFont font;

    @Override
    public void create() {

        font = new BitmapFont(Gdx.files.internal("Font/fuente2.fnt"), false);
        setScreen(new MainMenuScreen(this));
    }
}
