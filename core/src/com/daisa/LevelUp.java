package com.daisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class LevelUp implements Screen {
    Juego juego;
    Nave nave;

    Enemigo boss;
    private Array<TextureRegion> textureRegionArrayBossDerecha = new Array<>();
    private Array<TextureRegion> textureRegionArrayBossIzquierda = new Array<>();
    private Array<TextureRegion> textureRegionArrayBossArriba = new Array<>();
    private Array<TextureRegion> textureRegionArrayBossAbajo = new Array<>();
    private Array<TextureRegion> textureRegionArrayBossBala = new Array<>();
    private Array<TextureRegion> textureRegionArrayBala = new Array<>();

    Array<Bala> balasArrayNave;
    Array<Bala> balasArrayBoss;

    long ultimoDisparoBoss;

    boolean lucha;

    public LevelUp(Juego juego, Nave nave) {

        this.juego = juego;
        this.nave = nave;

        inicializarArraysTexturas();
        float posX = Constantes.ANCHO_PANTALLA / 2f - textureRegionArrayBossAbajo.get(0).getRegionWidth() / 2f;
        boss = new Enemigo(15, 4, new Vector2(posX, Constantes.ALTO_PANTALLA), textureRegionArrayBossDerecha, textureRegionArrayBossIzquierda, textureRegionArrayBossArriba, textureRegionArrayBossAbajo);

        nave.setPosicion(new Vector2(Constantes.CENTRO_HORIZONTAL_PANTALLA, 0));

        balasArrayNave = new Array<>();
        balasArrayBoss = new Array<>();

        ultimoDisparoBoss = TimeUtils.millis();
        lucha = false;
    }

    private void inicializarArraysTexturas() {

        textureRegionArrayBossDerecha.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Boss/mine-2.png"))));
        textureRegionArrayBossArriba.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Boss/mine-3.png"))));
        textureRegionArrayBossIzquierda.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Boss/mine-4.png"))));
        textureRegionArrayBossAbajo.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Boss/mine-5.png"))));

        textureRegionArrayBossBala.add(new Sprite(new Texture(Gdx.files.internal("Animation/Bullet/Boss/Ninja.png"))));

        for (int i = 1; i < 16; i++)
            textureRegionArrayBala.add(new Sprite(new Texture(Gdx.files.internal("Animation/Bullet/proton" + i + ".png"))));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        actualizar(delta);
        pintar();
    }


    private void actualizar(float dt) {

        if (lucha) {
            acionesTeclado(dt);
            comprobarLimitesPantalla();
        }
        moverNPCs(dt);
        comprobarColisiones();
    }

    private void bossDispara(int i) {
        Bala balaNinja;
        float posY = boss.getPosicion().y;
        float posX;
        if (i % 3 == 0) {
            posX = boss.getPosicion().x;

        } else if (i % 3 == 1) {
            posX = boss.getPosicion().x + boss.getFrameActual().getRegionWidth() / 2f - textureRegionArrayBossBala.get(0).getRegionWidth() / 2f;

        } else {
            posX = boss.getPosicion().x + boss.getFrameActual().getRegionWidth();
        }

        balaNinja = new Bala(7, new Vector2(posX, posY), textureRegionArrayBossBala);

        balasArrayBoss.add(balaNinja);
    }

    private void acionesTeclado(float dt) {
        nave.update(dt);
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                nave.estado = Nave.Estados.DERECHA;
                nave.moverDerecha();
                boss.estado = Nave.Estados.DERECHA;
                boss.moverDerecha();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                nave.estado = Nave.Estados.IZQUIERDA;
                nave.moverIzquierda();
                boss.estado = Nave.Estados.IZQUIERDA;
                boss.moverIzquierda();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                nave.estado = Nave.Estados.ARRIBA;
                nave.moverArriba();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                nave.estado = Nave.Estados.ABAJO;
                nave.moverAbajo();
                boss.estado = Nave.Estados.ABAJO;
                boss.moverAbajo();
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

    private void disparar() {
        if (TimeUtils.millis() - nave.getMomentoUltimoDisparo() > Nave.CADENCIA) {
            float posX = nave.getPosicion().x + nave.getFrameActual().getRegionWidth() / 2f - textureRegionArrayBala.get(0).getRegionWidth();
            float posY = nave.getPosicion().y + nave.getFrameActual().getRegionHeight();
            Bala bala = new Bala(10, new Vector2(posX, posY), textureRegionArrayBala);
            bala.estado = Personaje.Estados.ABAJO;
            balasArrayNave.add(bala);
            if (ConfigurationManager.isSoundEnabled())
                //sonidoDisparo.play();
                nave.setCargador(nave.getCargador() - 1);
            nave.setMomentoUltimoDisparo(TimeUtils.millis());
            if (nave.getCargador() <= 0) {
                nave.setPuedeDisparar(false);
                nave.setMomentoUltimaRecarga(TimeUtils.millis());
            }
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

        if (boss.getPosicion().x < 0)
            boss.setPosicion(new Vector2(0, boss.getPosicion().y));

        if (boss.getPosicion().x > Constantes.ANCHO_PANTALLA - boss.getFrameActual().getRegionWidth())
            boss.setPosicion(new Vector2(Constantes.ANCHO_PANTALLA - boss.getFrameActual().getRegionWidth(), boss.getPosicion().y));

        if (boss.getPosicion().y > Constantes.ALTO_PANTALLA - boss.getFrameActual().getRegionHeight())
            boss.setPosicion(new Vector2(boss.getPosicion().x, Constantes.ALTO_PANTALLA - boss.getFrameActual().getRegionHeight()));

        if (boss.getPosicion().y < 0)
            boss.setPosicion(new Vector2(boss.getPosicion().x, 0));


        for (Bala bala : balasArrayNave)
            if (bala.getPosicion().y > Constantes.ALTO_PANTALLA)
                balasArrayNave.removeValue(bala, true);
    }

    private void moverNPCs(float dt) {

        if (boss.getPosicion().y > Constantes.ALTO_PANTALLA * 3 / 4) {
            boss.estado = Personaje.Estados.ABAJO;
            boss.update(dt);
            boss.moverAbajo();
        } else {
            lucha = true;
            if (TimeUtils.millis() - ultimoDisparoBoss > Enemigo.CADENCIA) {
                for (int i = 0; i < 30; i++)
                    bossDispara(i);
                ultimoDisparoBoss = TimeUtils.millis();
            }

        }
        int cont = 0;
        for (Bala bala : balasArrayBoss) {
            if (cont % 3 == 0) {
                bala.mover(new Vector2(-1, -1));
            } else if (cont % 3 == 1) {
                bala.mover(new Vector2(0, -1));
            } else {
                bala.mover(new Vector2(1, -1));
            }
            bala.update(dt);
            bala.estado = Personaje.Estados.ABAJO;

            cont++;
        }

        for (Bala bala : balasArrayNave) {
            bala.moverArriba();
            bala.update(dt);
        }

    }

    private void comprobarColisiones() {

        for (Bala bala : balasArrayNave) {
            if (bala.rect.overlaps(boss.rect)) {
                balasArrayNave.removeValue(bala, true);
                boss.setVidas(boss.getVidas() - 1);
            }
        }

        for (Bala bala : balasArrayBoss) {
            if (bala.rect.overlaps(nave.rect)) {
                balasArrayBoss.removeValue(bala, true);
                nave.setVidas(nave.getVidas() - 1);
                System.out.println(nave.getVidas());
                if (nave.comprobarVida()) {
                    //juego.setScreen(new EndGameScreen(juego, nave));
                    juego.setScreen(new MainMenuScreen(juego));
                }
            }
        }
    }


    private void pintar() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        juego.batch.begin();

        juego.font.draw(juego.batch, "Vidas: " + nave.getVidas(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight());
        juego.font.draw(juego.batch, "Vida BOSS: " + boss.getVidas(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 3);
        juego.font.draw(juego.batch, "Nivel: " + nave.getNivel(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 7);
        if (nave.isPuedeDisparar())
            juego.font.draw(juego.batch, "Cargador: " + nave.getCargador(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);
        else
            juego.font.draw(juego.batch, "Recargando...", 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);

        nave.pintar(juego.batch);
        boss.pintar(juego.batch);
        for (Bala bala : balasArrayBoss) {
            bala.pintar(juego.batch);
        }
        for (Bala bala : balasArrayNave) {
            bala.pintar(juego.batch);
        }
        juego.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        textureRegionArrayBossDerecha.clear();
        textureRegionArrayBossIzquierda.clear();
        textureRegionArrayBossArriba.clear();
        textureRegionArrayBossAbajo.clear();
        textureRegionArrayBossBala.clear();
        textureRegionArrayBala.clear();
        balasArrayNave.clear();
        balasArrayBoss.clear();
    }
}
