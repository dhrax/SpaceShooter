package com.daisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Nave extends Personaje {

    public static int CADENCIA = 500;
    public static int TIEMPO_RECARGA = 2000;
    public static int BALAS_CARGADOR = 10;
    private int cargador;
    private boolean puedeDisparar;
    private long momentoUltimaRecarga, momentoUltimoDisparo;
    private int puntuacion;
    private int nivel;
    public static int MIN_GENERAR_ENEMIGO;
    public static int MAX_GENERAR_ENEMIGO;
    public static int MIN_GENERAR_POWER_UP;
    public static int MAX_GENERAR_POWER_UP;
    public static int NUM_ENEMIGOS_BOSS = 15;


    public Nave(int vidas, int velocidad, Vector2 posicion, Array<TextureRegion>... textureRegionArray) {
        super(vidas, velocidad, posicion, textureRegionArray);
        cargador = BALAS_CARGADOR;
        puntuacion = 0;
        puedeDisparar = true;
        momentoUltimoDisparo = TimeUtils.millis();
        nivel = 0;
        MIN_GENERAR_ENEMIGO = 500;
        MAX_GENERAR_ENEMIGO = 1000;

        MIN_GENERAR_POWER_UP = 8000;
        MAX_GENERAR_POWER_UP = 15000;
    }


    public long getMomentoUltimoDisparo() {
        return momentoUltimoDisparo;
    }

    public void setMomentoUltimoDisparo(long momentoUltimoDisparo) {
        this.momentoUltimoDisparo = momentoUltimoDisparo;
    }

    public long getMomentoUltimaRecarga() {
        return momentoUltimaRecarga;
    }

    public void setMomentoUltimaRecarga(long momentoUltimaRecarga) {
        this.momentoUltimaRecarga = momentoUltimaRecarga;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getCargador() {
        return cargador;
    }

    public void setCargador(int cargador) {
        this.cargador = cargador;
    }

    public boolean isPuedeDisparar() {
        return puedeDisparar;
    }

    public void setPuedeDisparar(boolean puedeDisparar) {
        this.puedeDisparar = puedeDisparar;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    /**
     * Se recargan las balas si ha pasado el tiempo requerido desde la ultima recarga
     */
    public void recargar() {
        if (TimeUtils.millis() - momentoUltimaRecarga > TIEMPO_RECARGA) {
            this.puedeDisparar = true;
            this.setCargador(BALAS_CARGADOR);
            momentoUltimaRecarga = TimeUtils.millis();
        }
    }

    /**
     * Se inicializan los datos para empezar una nueva partida
     *
     * @param vidas
     * @param posicion
     */
    public void inicializar(int vidas, Vector2 posicion) {
        super.setVidas(vidas);
        super.setPosicion(posicion);
        super.rect.setPosition(posicion);
        this.cargador = BALAS_CARGADOR;
        this.puedeDisparar = true;
        this.puntuacion = 0;
        MIN_GENERAR_ENEMIGO = 1000;
        MAX_GENERAR_ENEMIGO = 2000;
        estado = Estados.QUIETO;
    }

    /**
     * Devuelve si el jugador sigue vivo o no
     *
     * @return
     */
    public boolean comprobarVida() {
        return this.getVidas() <= 0;
    }

    /**
     * Si se sube de nivel, se reduce el tiempo en el que aparecen los enemigos, para hacer el juego más difícil
     *
     * @param i
     */
    public void subirNivel(int i) {
        MIN_GENERAR_ENEMIGO /= i;
        MAX_GENERAR_ENEMIGO /= i;
    }
}
