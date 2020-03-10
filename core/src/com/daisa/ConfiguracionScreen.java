package com.daisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ConfiguracionScreen implements Screen {
    Juego juego;
    Stage stage;

    public ConfiguracionScreen(Juego juego){
        this.juego= juego;
    }

    @Override
    public void show() {

        if (!VisUI.isLoaded())
            VisUI.load();

        stage = new Stage();

        VisTable table = new VisTable(true);
        table.setFillParent(true);
        stage.addActor(table);

        final VisCheckBox checkBoxSonido = new VisCheckBox("Sonido Activado", ConfigurationManager.isSoundEnabled());

        checkBoxSonido.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean sonidoActivado =  checkBoxSonido.isChecked();
                ConfigurationManager.prefs.putBoolean("sonido", sonidoActivado);
                System.out.println(sonidoActivado);
            }
        });

        Image volver = new Image(new Texture(Gdx.files.internal("Screen/Classic/back_button.png")));
        volver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event,  float x, float y) {
                juego.setScreen(new MainMenuScreen(juego));
                dispose();
            }
        });

        // Añade filas a la tabla y añade los componentes
        table.row();
        table.add(checkBoxSonido).width(300);
        table.row();
        table.add(volver);

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Pinta la UI en la pantalla
        stage.act(delta);
        stage.draw();
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

    }
}
