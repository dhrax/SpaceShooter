package com.daisa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Juego extends Game {

    BitmapFont font;
    SpriteBatch batch;

    @Override
    public void create() {
        batch= new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("Font/fuente2.fnt"), false);
        setScreen(new MainMenuScreen(this));
    }
}
