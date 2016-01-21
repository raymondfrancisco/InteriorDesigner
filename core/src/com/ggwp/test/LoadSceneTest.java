package com.ggwp.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Raymond on 1/19/2016.
 */
public class LoadSceneTest implements ApplicationListener {
    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public AssetManager assets;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public Environment environment;
    public boolean loading;

    public Array<ModelInstance> blocks = new Array<ModelInstance>();
    public Array<ModelInstance> invaders = new Array<ModelInstance>();
    public ModelInstance ship;
    public ModelInstance space;

    @Override
    public void create () {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 7f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        assets = new AssetManager();
//        assets.load("data/ship.obj", Model.class);
//        assets.load("data/block.obj", Model.class);
//        assets.load("data/invader.obj", Model.class);
//        assets.load("data/spacesphere.obj", Model.class);

//        assets.load("data/invaders.g3db", Model.class);
        assets.load("data/invaderscene.g3db", Model.class);
        loading = true;
    }

    private void doneLoading() {
        Model model = assets.get("data/invaderscene.g3db", Model.class);
        for (int i = 0; i < model.nodes.size; i++) {
            String id = model.nodes.get(i).id;
            ModelInstance instance = new ModelInstance(model, id);
            Node node = instance.getNode(id);

            instance.transform.set(node.globalTransform);
            node.translation.set(0,0,0);
            node.scale.set(1,1,1);
            node.rotation.idt();
            instance.calculateTransforms();

            if (id.equals("space")) {
                space = instance;
                continue;
            }

            instances.add(instance);

            if (id.equals("ship"))
                ship = instance;
            else if (id.startsWith("block"))
                blocks.add(instance);
            else if (id.startsWith("invader"))
                invaders.add(instance);
        }

        ship = new ModelInstance(assets.get("data/ship.obj", Model.class));
//        ship.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f);
//        instances.add(ship);
//
//        Model blockModel = assets.get("data/block.obj", Model.class);
//        for (float x = -5f; x <= 5f; x += 2f) {
//            ModelInstance block = new ModelInstance(model,"block");
//            block.transform.setToTranslation(x, 0, 3f);
//            instances.add(block);
//            blocks.add(block);
//        }
//
//        Model invaderModel = assets.get("data/invader.obj", Model.class);
//        for (float x = -5f; x <= 5f; x += 2f) {
//            for (float z = -8f; z <= 0f; z += 2f) {
//                ModelInstance invader = new ModelInstance(model,"invader");
//                invader.transform.setToTranslation(x, 0, z);
//                instances.add(invader);
//                invaders.add(invader);
//            }
//        }
//
//
//        space = new ModelInstance(assets.get("data/spacesphere.obj", Model.class));

        loading = false;
    }

    @Override
    public void render () {
        if (loading && assets.update())
            doneLoading();
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        if (space != null)
            modelBatch.render(space);
        modelBatch.end();
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void pause () {
    }
}