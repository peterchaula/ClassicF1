package com.w3wheels.olda.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Opponent extends Sprite {
	
	public boolean mAttached = false;

	public Opponent(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVbom) {
		super(pX, pY, pTextureRegion, pVbom);
		setZIndex(1);
	}

	public void move(final float pStep) {
		this.setY(this.mY + pStep);
	}

	public boolean shouldBeRecycled(final float pCameraHeight) {
		return getY() > pCameraHeight;
	}

	public Opponent setYPos(final float pY) {
		super.setY(pY);
		return this;
	}
	public Opponent setXPos(final float pX){
		super.setX(pX);;
		return this;
	}
	
}
