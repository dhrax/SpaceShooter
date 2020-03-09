package com.daisa;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class MyGdxGame implements Screen {

	Juego juego;
	SpriteBatch batch;
	Nave nave;
	Array<Enemigo> enemigoArray;
	Array<Bala> balasArray;
	Random rand = new Random();

	Estados estado;
	int siguienteEnemigo;
	long ultimoEnemigo;
	Music music;
	Sound sonidoDisparo;

	private Array<TextureRegion> textureRegionArrayNaveDerecha = new Array<>();
	private  Array<TextureRegion> textureRegionArrayNaveIzquierda = new Array<>();
	private Array<TextureRegion> textureRegionArrayNaveArriba = new Array<>();
	private Array<TextureRegion> textureRegionArrayNaveAbajo = new Array<>();
	private Array<TextureRegion> textureRegionArrayBala = new Array<>();
	private Array<TextureRegion> textureRegionArrayEnemigoRojo = new Array<>();
	private Array<TextureRegion> textureRegionArrayEnemigoAzul = new Array<>();

	public MyGdxGame(Juego juego) {
		this.juego = juego;
		batch = new SpriteBatch();

		enemigoArray = new Array<>();
		balasArray = new Array<>();


		sonidoDisparo = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/proton__shot.ogg"));

		estado = Estados.JUGANDO;

		inicializarArraysTexturas();

		nave = new Nave(2, 5, new Vector2(Constantes.CENTRO_HORIZONTAL_PANTALLA, 0), textureRegionArrayNaveDerecha, textureRegionArrayNaveIzquierda, textureRegionArrayNaveArriba, textureRegionArrayNaveAbajo);

		siguienteEnemigo = rand.nextInt(Nave.MAX_GENERAR_ENEMIGO) + Nave.MIN_GENERAR_ENEMIGO;
		ultimoEnemigo = TimeUtils.millis();
	}

	@Override
	public void render (float delta) {

		actualizar();
		pintar();
		//Si estamos en el menu de inicio y pulsamos ENTER, jugamos
		if (estado == Estados.FIN_JUEGO && Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
			restart();
			estado = Estados.JUGANDO;
		}
		//Si estamos jugando y pulsamos ESCAPE, vamos a menu de inicio
		if (estado == Estados.JUGANDO && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			estado = Estados.FIN_JUEGO;
		}
	}

	private void inicializarArraysTexturas() {
		for(int i = 1; i<16; i++)
			textureRegionArrayBala.add(new Sprite(new Texture(Gdx.files.internal("Animation/Bullet/proton"+i+".png"))));

		for(int i = 1; i<10; i++)
			textureRegionArrayEnemigoRojo.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Red/spaceship_enemy_"+i+".png"))));

		for(int i = 1; i<4; i++)
			textureRegionArrayEnemigoAzul.add(new Sprite(new Texture(Gdx.files.internal("Animation/Enemy/Blue/spaceship_enemy_"+i+".png"))));

		textureRegionArrayNaveDerecha.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraDerecha1.png"))));
		textureRegionArrayNaveDerecha.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraDerecha2.png"))));

		textureRegionArrayNaveIzquierda.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraIzquierda1.png"))));
		textureRegionArrayNaveIzquierda.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraIzquierda2.png"))));

		textureRegionArrayNaveArriba.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraArriba1.png"))));
		textureRegionArrayNaveArriba.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraArriba2.png"))));

		textureRegionArrayNaveAbajo.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraAbajo1.png"))));
		textureRegionArrayNaveAbajo.add(new Sprite(new Texture(Gdx.files.internal("Animation/naveNegraAbajo2.png"))));
	}

	private void restart() {
		nave.inicializar(2, new Vector2(Constantes.CENTRO_HORIZONTAL_PANTALLA - textureRegionArrayNaveAbajo.get(0).getRegionWidth()/2f, 0));
		balasArray.clear();
		enemigoArray.clear();
	}

	public void actualizar(){
		if (estado != Estados.JUGANDO)
			return;

		float dt = Gdx.graphics.getDeltaTime();

		generarEnemigos();
		acionesTeclado(dt);
		comprobarLimitesPantalla();
		moverNPCs(dt);
		comprobarColisiones();
	}

	private void comprobarLimitesPantalla() {
		if(nave.getPosicion().x < 0)
			nave.setPosicion(new Vector2(0, nave.getPosicion().y));

		if(nave.getPosicion().x > Constantes.ANCHO_PANTALLA - nave.getFrameActual().getRegionWidth())
			nave.setPosicion(new Vector2(Constantes.ANCHO_PANTALLA - nave.getFrameActual().getRegionWidth(), nave.getPosicion().y));

		if(nave.getPosicion().y > Constantes.ALTO_PANTALLA - nave.getFrameActual().getRegionHeight())
			nave.setPosicion(new Vector2(nave.getPosicion().x, Constantes.ALTO_PANTALLA - nave.getFrameActual().getRegionHeight()));

		if(nave.getPosicion().y < 0)
			nave.setPosicion(new Vector2(nave.getPosicion().x, 0));

		for (Bala bala : balasArray)
			if (bala.getPosicion().y > Constantes.ALTO_PANTALLA)
				balasArray.removeValue(bala, true);

		for (Enemigo enemigo : enemigoArray){
			if (enemigo.getPosicion().y < 0){
				enemigoArray.removeValue(enemigo, true);
				nave.setVidas(nave.getVidas()-1);
				if (nave.comprobarVida())
					estado = Estados.FIN_JUEGO;
			}
		}


	}

	private void comprobarColisiones() {
		for(Enemigo enemigo : enemigoArray){
			for (Bala bala : balasArray){
				if(bala.rect.overlaps(enemigo.rect)){
					enemigoArray.removeValue(enemigo, true);
					balasArray.removeValue(bala, true);
					nave.setPuntuacion(nave.getPuntuacion()+1);
					//Se sube de nivel cada 5 puntos
					if (nave.getPuntuacion() % 5 == 0) {
						nave.setNivel(nave.getPuntuacion() / 5);
						nave.subirNivel(nave.getNivel());
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
	}

	private void generarEnemigos() {
		if(TimeUtils.millis() - ultimoEnemigo > siguienteEnemigo){
			Enemigo enemigo;
			if(rand.nextBoolean()){
				int posX = rand.nextInt(Constantes.ANCHO_PANTALLA - textureRegionArrayEnemigoRojo.get(0).getRegionWidth());
				float posY = Constantes.ALTO_PANTALLA;

				enemigo = new Enemigo(1, 2, new Vector2(posX, posY), textureRegionArrayEnemigoRojo);
			}else{
				int posX = rand.nextInt(Constantes.ANCHO_PANTALLA - textureRegionArrayEnemigoAzul.get(0).getRegionWidth());
				float posY = Constantes.ALTO_PANTALLA;

				enemigo = new Enemigo(1, 2, new Vector2(posX, posY), textureRegionArrayEnemigoAzul);
			}

			enemigo.estado = Personaje.Estados.ABAJO;
			enemigoArray.add(enemigo);
			ultimoEnemigo = TimeUtils.millis();
			siguienteEnemigo = rand.nextInt(Nave.MAX_GENERAR_ENEMIGO) + Nave.MIN_GENERAR_ENEMIGO;
		}
	}



	public void acionesTeclado(float dt){

		nave.update(dt);

		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)){
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				nave.estado = Nave.Estados.DERECHA;
				nave.moverDerecha();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				nave.estado = Nave.Estados.IZQUIERDA;
				nave.moverIzquierda();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.UP)){
				nave.estado = Nave.Estados.ARRIBA;
				nave.moverArriba();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
				nave.estado = Nave.Estados.ABAJO;
				nave.moverAbajo();
			}
			if (Gdx.input.isKeyPressed(Input.Keys.R)) {
				nave.setPuedeDisparar(false);
				nave.setMomentoUltimaRecarga(TimeUtils.millis());
				nave.recargar();
			}
			if(!nave.isPuedeDisparar())
				nave.recargar();
			else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
				disparar();
			}
		}else{
			nave.estado = Nave.Estados.QUIETO;
		}

	}

	public void disparar(){
		if(TimeUtils.millis() - nave.getMomentoUltimoDisparo() > Nave.TIEMPO_ENTRE_BALAS){
			float posX = nave.getPosicion().x + nave.getFrameActual().getRegionWidth()/2f - textureRegionArrayBala.get(0).getRegionWidth();
			float posY = nave.getPosicion().y + nave.getFrameActual().getRegionHeight();
			Bala bala = new Bala(1, 8, new Vector2(posX, posY), textureRegionArrayBala);
			bala.estado = Personaje.Estados.ABAJO;
			balasArray.add(bala);
			if(ConfigurationManager.isSoundEnabled())
				sonidoDisparo.play();
			nave.setCargador(nave.getCargador()-1);
			nave.setMomentoUltimoDisparo(TimeUtils.millis());
			if(nave.getCargador() <= 0){
				nave.setPuedeDisparar(false);
				nave.setMomentoUltimaRecarga(TimeUtils.millis());
			}
		}
	}

	public void moverNPCs(float dt){
		for(Enemigo enemigo : enemigoArray){
			enemigo.update(dt);
			enemigo.moverAbajo();
		}

		for (Bala bala : balasArray){
			bala.update(dt);
			bala.moverArriba();
		}

	}

	public void pintar(){
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		//IF que decide si se pinta el menu del juego o el del menu de inicio
		if (estado == Estados.JUGANDO) {
			juego.font.draw(batch, "Vidas: " + nave.getVidas(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight());
			juego.font.draw(batch, "Puntuacion: " + nave.getPuntuacion(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 3);
			juego.font.draw(batch, "Siguiente enemigo en: " + siguienteEnemigo / 1000f, 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 5);
			juego.font.draw(batch, "Nivel: " + nave.getNivel(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 7);
			if (nave.isPuedeDisparar())
				juego.font.draw(batch, "Cargador: " + nave.getCargador(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);
			else
				juego.font.draw(batch, "Recargando...", 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);


			nave.pintar(batch);
			for (Enemigo enemigo : enemigoArray)
				enemigo.pintar(batch);
			for (Bala bala : balasArray)
				bala.pintar(batch);
		} else {
			/*juego.font.draw(batch, "Fin del JUEGO", 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight());
			juego.font.draw(batch, "Puntuacion: " + nave.getPuntuacion(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 3);
			juego.font.draw(batch, "Tiempo de Recarga: " + Nave.TIEMPO_RECARGA, 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 5);
			juego.font.draw(batch, "Cadencia: " + Nave.TIEMPO_ENTRE_BALAS, 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 7);
			juego.font.draw(batch, "Nivel: " + nave.getNivel(), 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 9);
			juego.font.draw(batch, "Pulse Enter para volver a Jugar", 0, Constantes.ALTO_PANTALLA - juego.font.getXHeight() * 11);*/



			juego.setScreen(new MainMenuScreen(juego));
		}
		batch.end();
	}

	@Override
	public void show() {

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
	public void dispose () {
		batch.dispose();
	}

	public enum Estados{
		JUGANDO, FIN_JUEGO, PAUSA
	}
}
