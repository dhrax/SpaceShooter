package com.daisa;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemigo extends  Personaje{

    public static int VELOCIDAD_ENEMIGO = 2;
    public static int CADENCIA = 500;

    public Enemigo(int vidas,Vector2 posicion, Array<TextureRegion> textureRegionArrayEnemigo){
        super(vidas, VELOCIDAD_ENEMIGO, posicion, textureRegionArrayEnemigo);
    }

    public Enemigo(int vidas, int velocidad, Vector2 posicion, Array<TextureRegion> ... textureRegionArray){
        super(vidas, velocidad, posicion, textureRegionArray);
    }



}
