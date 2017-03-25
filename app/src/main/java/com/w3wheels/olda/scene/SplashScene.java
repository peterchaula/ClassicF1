package com.w3wheels.olda.scene;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.w3wheels.olda.resource.ResourceManager;

public class SplashScene extends Scene {
	public SplashScene(SimpleBaseGameActivity pContext) {
		super();
		setBackground(new SpriteBackground(0, 0, 0, new Sprite(0, 0, ResourceManager.getManager(pContext).mSplashBackgroundRegion, pContext.getVertexBufferObjectManager())));
	}
}
