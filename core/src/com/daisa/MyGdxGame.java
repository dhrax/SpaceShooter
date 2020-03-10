package com.daisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class MyGdxGame implements Screen {

    Juego juego;
    Nave nave;
    Array<Enemigo> enemigoArray;
    Array<Bala> balasArray;
    Array<PowerUp> powerUpArray;
    Random rand = new Random();

    Estados estado;
    int siguienteEnemigo;
    long ultimoEnemigo;
    int siguientePowerUp;
    long ultimoPowerUp;
    Music music;
    Sound sonidoDisparo;

    private Array<TextureRegion> textureRegionArrayNaveDerecha = new Array<>();
    private Array<TextureRegion> textureRegionArrayNaveIzquierda = new Array<>();
    private Array<TextureRegion> textureRegionArrayNaveArriba = new Array<>();
    private Array<TextureRegion> textureRegionArrayNaveAbajo = new Array<>();
    private Array<TextureRegion> textureRegionArrayBala = new Array<>();
    private Array<TextureRegion> textureRegionArrayEnemigoRojo = new Array<>();
    private Array<TextureRegion> textureRegionArrayEnemigoAzul = new Array<>();

    private Array<Texture> textureArrayPowerUp = new Array<>();

    public MyGdxGame(Juego juego) {
        this.juego = juego;

        enemigoArray = new Array<>();
        balasArray = new Array<>();
        powerUpArray = new Array<>();

        sonidoDisparo = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/proton__shot.ogg"));

        estado = Estados.JUGANDO;

        inicializarArraysTexturas();

        nave = new Nave(2, 5, new Vector2(Constantes.CENTRO_HORIZONTAL_PANTALLA, 0), textureRegionArrayNaveDerecha, textureRegionArrayNaveIzquierda, textureRegionArrayNaveArriba, textureRegionArrayNaveAbajo);

        siguienteEnemigo = rand.nextInt(Nave.MAX_GENERAR_ENEMIGO) + Nave.MIN_GENERAR_ENEMIGO;
        ultimoEnemigo = TimeUtils.millis();

        siguientePowerUp = rand.nextInt(Nave.MAX_GENERAR_POWER_UP) + Nave.MIN_GENERAR_POWER_UP;
        ultimoPowerUp = TimeUtils.millis();
    }

    @Override
    public void render(float delta) {
        actualizar();
        pintar();
    }

    private void inicializarArraysTexturas() {
        for (int i = 1; i < 16; i++)
            textureRegionArrayBala.add(new Sprite(new Texture(Gdx.files.internal("Animation/Bullet/proton" + i + ".png"))));

        for (int i = 1; i < 10; i++)
            textureRegionArrayEnemigoRojo.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Red/spaceship_enemy_" + i + ".png"))));

        for (int i = 1; i < 4; i++)
            textureRegionArrayEnemigoAzul.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Blue/spaceship_enemy_" + i + ".png"))));

        textureRegionArrayNaveDerecha.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraDerecha1.png"))));
        textureRegionArrayNaveDerecha.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraDerecha2.png"))));

        textureRegionArrayNaveIzquierda.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraIzquierda1.png"))));
        textureRegionArrayNaveIzquierda.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraIzquierda2.png"))));

        textureRegionArrayNaveArriba.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraArriba1.png"))));
        textureRegionArrayNaveArriba.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraArriba2.png"))));

        textureRegionArrayNaveAbajo.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraAbajo1.png"))));
        textureRegionArrayNaveAbajo.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraAbajo2.png"))));

        textureArrayPowerUp.add(new Texture(Gdx.files.internal("PowerUp/reducir_cadencia.png")));
        textureArrayPowerUp.add(new Texture(Gdx.files.internal("PowerUp/aumenta_salud.png")));
        textureArrayPowerUp.add(new Texture(Gdx.files.internal("PowerUp/limpiar_enemigos.png")));
        textureArrayPowerUp.add(new Texture(Gdx.files.internal("PowerUp/reducir_velocidad_enemigos.png")));
        textureArrayPowerUp.add(new Texture(Gdx.files.internal("PowerUp/velocidad_aumenta.png")));
    }

    private void restart() {
        nave.inicializar(2, new Vector2(Constantes.CENTRO_HORIZONTAL_PANTALLA - textureRegionArrayNaveAbajo.get(0).getRegionWidth() / 2f, 0));
        balasArray.clear();
        enemigoArray.clear();
    }

    public void actualizar() {
        if (estado != Estados.JUGANDO)
            return;

        float dt = Gdx.graphics.getDeltaTime();

        generarEnemigos();
        generarPowerUps();
        acionesTeclado(dt);
        comprobarLimitesPantalla();
        moverNPCs(dt);
        comprobarColisiones();
    }

    private void generarPowerUps() {

        if (TimeUtils.millis() - ultimoPowerUp > siguientePowerUp) {
            System.out.println("GENERA POWER UP");
            int pos = rand.nextInt(textureArrayPowerUp.size);
            Texture aspecto = textureArrayPowerUp.get(pos);
            int posX = rand.nextInt(Constantes.ANCHO_PANTALLA - aspecto.getWidth());
            float posY = Constantes.ALTO_PANTALLA;
            PowerUp powerUp = new PowerUp(8, new Vector2(posX, posY), aspecto, pos);

            powerUpArray.add(powerUp);
            ultimoPowerUp = TimeUtils.millis();
            siguientePowerUp = rand.nextInt(Nave.MAX_GENERAR_POWER_UP) + Nave.MIN_GENERAR_POWER_UP;
        }

    }

    private void comprobarLimitesPantalla() {
        if (nave.getPosicion().x < 0)
            nave.setPosicion(new Vector2(0, nave.getPosicion().y));

        if (nave.getPosicion().x > Constantes.ANCHO_PANTALLA - nave.getFrameActual().getRegionWidth())
            nave.setPosicion(new Vector2(Constantes.ANCHO_PANTALLA - nave.getFrameActual().getRegionWidth(), nave.getPosicion().y));

        if (nave.getPosicion().y > Constantes.ALTO_PANTALLA - nave.getFrameActual().getRegionHeight())
            nave.setPosicion(new Vector2(nave.getPosicion().x, Constantes.ALTO_PANTALLA - nave.getFrameActual().getRegionHeight()));

        if (nave.getPosicion().y < 0)
            nave.setPosicion(new Vector2(nave.getPosicion().x, 0));

        for (Bala bala : balasArray)
            if (bala.getPosicion().y > Constantes.ALTO_PANTALLA)
                balasArray.removeValue(bala, true);

        for (Enemigo enemigo : enemigoArray) {
            if (enemigo.getPosicion().y < 0) {
                enemigoArray.removeValue(enemigo, true);
                nave.setVidas(nave.getVidas() - 1);
                if (nave.comprobarVida())
                    estado = Estados.FIN_JUEGO;
            }
        }
    }

    private void comprobarColisiones() {
        for (Enemigo enemigo : enemigoArray) {
            for (Bala bala : balasArray) {
                if (bala.rect.overlaps(enemigo.rect)) {
                    enemigoArray.removeValue(enemigo, true);
                    balasArray.removeValue(bala, true);
                    nave.setPuntuacion(nave.getPuntuacion() + 1);
                    //Se sube de nivel cada 5 puntos
                    if (nave.getPuntuacion() == 1) {
                        nave.setNivel(1);
                        estado = Estados.LVL2;
                        juego.setScreen(new LevelUp(juego, nave));
                        this.dispose();

                    }
                }
                if (nave.rect.overlaps(enemigo.rect)) {
                    enemigoArray.removeValue(enemigo, true);
                    nave.setVidas(nave.getVidas() - 1);
                    if (nave.comprobarVida()) {
                        estado = Estados.FIN_JUEGO;
                    }
                }
            }
        }
        for (PowerUp powerUp : powerUpArray) {
            if (nave.rect.overlaps(powerUp.rect)) {
                switch (powerUp.tipo) {
                    case 0:
                        if (Nave.CADENCIA > 100) {
                            Nave.CADENCIA -= 100;
                        }
                        break;
                    case 1:
                        nave.setVidas(nave.getVidas() + 1);
                        break;
                    case 2:
                        enemigoArray.clear();
                        break;
                    case 3:
                        if (Enemigo.VELOCIDAD_ENEMIGO > 1) {
                            Enemigo.VELOCIDAD_ENEMIGO--;
                            for (Enemigo enemigo : enemigoArray) {
                                enemigo.setVelocidad(Enemigo.VELOCIDAD_ENEMIGO);
                            }
                        }

                        break;
                    case 4:
                        nave.setVelocidad(nave.getVelocidad() + 1);
                        break;
                }
                powerUpArray.removeValue(powerUp, true);
            }
        }
    }

    private void generarEnemigos() {
        if (TimeUtils.millis() - ultimoEnemigo > siguienteEnemigo) {
            Enemigo enemigo;
            if (rand.nextBoolean()) {
                int posX = rand.nextInt(Constantes.ANCHO_PANTALLA - textureRegionArrayEnemigoRojo.get(0).getRegionWidth());
                float posY = Constantes.ALTO_PANTALLA;

                enemigo = new Enemigo(1, new Vector2(posX, posY), textureRegionArrayEnemigoRojo);
            } else {
                int posX = rand.nextInt(Constantes.ANCHO_PANTALLA - textureRegionArrayEnemigoAzul.get(0).getRegionWidth());
                float posY = Constantes.ALTO_PANTALLA;

                enemigo = new Enemigo(1, new Vector2(posX, posY), textureRegionArrayEnemigoAzul);
            }

            enemigo.estado = Personaje.Estados.ABAJO;
            enemigoArray.add(enemigo);
            ultimoEnemigo = TimeUtils.millis();
            siguienteEnemigo = rand.nextInt(Nave.MAX_GENERAR_ENEMIGO) + Nave.MIN_GENERAR_ENEMIGO;
        }
    }

    public void acionesTeclado(float dt) {

        nave.update(dt);
        //Si estamos en el menu de inicio y pulsamos ENTER, jugamos
        if (estado == Estados.FIN_JUEGO && Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            restart();
            estado = Estados.JUGANDO;
        }
        //Si estamos jugando y pulsamos ESCAPE, vamos a menu de pausa
        if (estado == Estados.JUGANDO && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            estado = Estados.PAUSA;
            juego.setScreen(new PauseScreen(juego, this));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                nave.estado = Nave.Estados.DERECHA;
                nave.moverDerecha();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                nave.estado = Nave.Estados.IZQUIERDA;
                nave.moverIzquierda();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                nave.estado = Nave.Estados.ARRIBA;
                nave.moverArriba();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                nave.estado = Nave.Estados.ABAJO;
                nave.moverAbajo();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                nave.setPuedeDisparar(false);
                nave.setMomentoUltimaRecarga(TimeUtils.millis());
                nave.recargar();
            }
            if (!nave.isPuedeDisparar())
                nave.recargar();
            else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                disparar();
            }
        } else {
            nave.estado = Nave.Estados.QUIETO;
        }

    }

    public void disparar() {
        if (TimeUtils.millis() - nave.getMomentoUltimoDisparo() > Nave.CADENCIA) {
            float posX = nave.getPosicion().x + nave.getFrameActual().getRegionWidth() / 2f - textureRegionArrayBala.get(0).getRegionWidth();
            float posY = nave.getPosicion().y + nave.getFrameActual().getRegionHeight();
            Bala bala = new Bala(8, new Vector2(posX, posY), textureRegionArrayBala);
            bala.estado = Personaje.Estados.ABAJO;
            balasArray.add(bala);
            if (ConfigurationManager.isSoundEnabled())
                sonidoDisparo.play();
            nave.setCargador(nave.getCargador() - 1);
            nave.setMomentoUltimoDisparo(TimeUtils.millis());
            if (nave.getCargador() <= 0) {
                nave.setPuedeDisparar(false);
                nave.setMomentoUltimaRecarga(TimeUtils.millis());
            }
        }
    }

    public void moverNPCs(float dt) {
        for (Enemigo enemigo : enemigoArray) {
            enemigo.update(dt);
            enemigo.moverAbajo();
        }

        for (Bala bala : balasArray) {
            bala.update(dt);
            bala.moverArriba();
        }
        for (PowerUp powerUp : powerUpArray) {
            powerUp.moverAbajo();
        }

    }

    public void pintar() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        juego.batch.begin();
        //IF que decide si se pinta el menu del juego o el del menu de inicio
        if (estado == Estados.JUGANDO) {
            juego.font.draw(juego.batch, "Vidas: " + nave.getVidas(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight());
            juego.font.draw(juego.batch, "Puntuacion: " + nave.getPuntuacion(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 3);
            juego.font.draw(juego.batch, "Nivel: " + nave.getNivel(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 7);
            if (nave.isPuedeDisparar())
                juego.font.draw(juego.batch, "Cargador: " + nave.getCargador(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);
            else
                juego.font.draw(juego.batch, "Recargando...", 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);

            nave.pintar(juego.batch);
            for (Enemigo enemigo : enemigoArray)
                enemigo.pintar(juego.batch);
            for (Bala bala : balasArray)
                bala.pintar(juego.batch);
            for (PowerUp powerUp : powerUpArray)
                powerUp.pintar(juego.batch);
        } else if (estado == Estados.FIN_JUEGO) {
            juego.setScreen(new MainMenuScreen(juego));
        }
        juego.batch.end();
    }

    @Override
    public void show() {
        estado = Estados.JUGANDO;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        estado = Estados.PAUSA;
    }

    @Override
    public void resume() {
        estado = Estados.JUGANDO;
    }

    @Override
    public void hide() {
        estado = Estados.PAUSA;
    }

    @Override
    public void dispose() {
        powerUpArray.clear();
        enemigoArray.clear();
        balasArray.clear();
        textureRegionArrayBala.clear();
        textureRegionArrayEnemigoRojo.clear();
        textureRegionArrayEnemigoAzul.clear();

    }

    public enum Estados {
        JUGANDO, FIN_JUEGO, PAUSA, LVL2
    }
}
