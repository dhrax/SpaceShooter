package com.daisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Personaje {

    private int vidas;
    private int velocidad;
    private Vector2 posicion;
    Rectangle rect;

    private Array<TextureRegion> textureRegionArrayDerecha = new Array<>();
    private  Array<TextureRegion> textureRegionArrayIzquierda = new Array<>();
    private Array<TextureRegion> textureRegionArrayArriba = new Array<>();
    private Array<TextureRegion> textureRegionArrayAbajo = new Array<>();
    Animation animacionDerecha, animacionIzquierda, animacionArriba, animacionAbajo;
    public Estados estado;
    private TextureRegion frameActual;
    private float stateTime;



    public Personaje(int vidas, int velocidad, Vector2 posicion, Array<TextureRegion> ... textureRegionArray ) {
        this.vidas = vidas;
        this.velocidad = velocidad;
        this.posicion = posicion;

        inicializarAnimaciones(textureRegionArray);
        estado = Estados.QUIETO;

        rect = new Rectangle(posicion.x, posicion.y, textureRegionArrayAbajo.get(0).getRegionWidth() , textureRegionArrayAbajo.get(0).getRegionHeight());
    }

    public enum Estados{
        ARRIBA, ABAJO, DERECHA, IZQUIERDA, QUIETO
    }

    private void inicializarAnimaciones(Array<TextureRegion>[] textureRegionArray) {

        if (textureRegionArray.length == 1){
            textureRegionArrayAbajo = textureRegionArray[0];
            animacionAbajo = new Animation(0.25f, textureRegionArrayAbajo);
        }else{
            textureRegionArrayDerecha = textureRegionArray[0];
            textureRegionArrayIzquierda = textureRegionArray[1];
            textureRegionArrayArriba = textureRegionArray[2];
            textureRegionArrayAbajo = textureRegionArray[3];

            animacionDerecha = new Animation(0.25f, textureRegionArrayDerecha);
            animacionIzquierda = new Animation(0.25f, textureRegionArrayIzquierda);
            animacionArriba = new Animation(0.25f, textureRegionArrayArriba);
            animacionAbajo = new Animation(0.25f, textureRegionArrayAbajo);
        }

    }

    public TextureRegion getFrameActual() {
        return frameActual;
    }

    public void setFrameActual(TextureRegion frameActual) {
        this.frameActual = frameActual;
    }

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public Vector2 getPosicion() {
        return posicion;
    }

    public void setPosicion(Vector2 posicion) {
        this.posicion = posicion;
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
        batch.draw(frameActual, getPosicion().x, getPosicion().y);
    }

    public void update(float dt) {
        stateTime += dt;

        switch (estado) {
            case DERECHA:
                frameActual = (TextureRegion) animacionDerecha.getKeyFrame(stateTime, true);
                break;
            case IZQUIERDA:
                frameActual = (TextureRegion) animacionIzquierda.getKeyFrame(stateTime, true);
                break;
            case ARRIBA:
                frameActual = (TextureRegion) animacionArriba.getKeyFrame(stateTime, true);
                break;
            case ABAJO:
            default:
                frameActual = (TextureRegion) animacionAbajo.getKeyFrame(stateTime, true);
                break;
        }
    }

}
