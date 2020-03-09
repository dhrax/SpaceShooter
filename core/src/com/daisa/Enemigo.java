package com.daisa;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemigo extends  Personaje{

    public Enemigo(int vidas, int velocidad, Vector2 posicion, Array<TextureRegion> textureRegionArrayEnemigo){
        super(vidas, velocidad, posicion, textureRegionArrayEnemigo);
    }



}
