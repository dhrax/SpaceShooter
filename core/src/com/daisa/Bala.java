package com.daisa;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Bala extends Personaje {

    public Bala(int vidas, int velocidad, Vector2 posicion, Array<TextureRegion> textureRegionArrayBala) {
        super(vidas, velocidad, posicion, textureRegionArrayBala);
    }
}