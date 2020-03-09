package com.daisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PowerUp{

    int velocidad;
    Vector2 posicion;
    Texture aspecto;
    Rectangle rect;
    int tipo;

    public PowerUp(int velocidad, Vector2 posicion, Texture aspecto, int tipo) {
        this.velocidad = velocidad;
        this.posicion = posicion;
        this.aspecto = aspecto;
        this.tipo = tipo;
        rect = new Rectangle(posicion.x, posicion.y, aspecto.getWidth(), aspecto.getHeight());
    }

    private void mover(Vector2 direccion) {
        posicion.add(direccion.scl(velocidad));
        rect.setPosition(posicion);
    }

    public void moverDerecha(){
        mover(new Vector2(1, 0));
    }

    public void moverIzquierda(){
        mover(new Vector2(-1, 0));
    }

    public void moverArriba(){
        mover(new Vector2(0, 1));
    }

    public void moverAbajo(){
        mover(new Vector2(0, -1));
    }
    public void pintar(SpriteBatch batch){
        batch.draw(aspecto, posicion.x, posicion.y);
    }
}
