package com.w3wheels.olda.entity;

/**
 * Note: this code is based on
 * http://www.andengine.org/forums/development/my-solution
 * -for-vertical-parallax-with-pausing-t584.html and
 * http://www.andengine.org/forums/gles2/vertical-parallax-with-gles2-t6806.html
 * 
 * Your mileage may vary.
 */

import android.annotation.SuppressLint;
import java.util.ArrayList;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.util.GLState;

/**
 * The role of VerticalParallaxBackground is to enable a background that scrolls
 * in the Y dimension, instead of the X dimension.
 * 
 * NB: This code is directly derived from the above websites, and has not been
 * verified or tuned in any appreciable way.
 */
public class VerticalParallaxBackground extends ParallaxBackground
{
    /**
     * The set of entities that scroll at various speeds
     */
    private final ArrayList<VerticalParallaxEntity> _entities = new ArrayList<VerticalParallaxEntity>();

    /**
     * The rate of scrolling
     */
    protected float _scrollRate;

    /**
     * Simple constructor
     * 
     * @param red
     *            The red portion of the color
     * @param green
     *            The green portion of the color
     * @param blue
     *            The blue portion of the color
     */
   public VerticalParallaxBackground(float red, float green, float blue)
    {
        super(red, green, blue);
    }

    /**
     * Set the rate of scrolling
     * 
     * @param scrollRate
     *            The rate to use
     */
    public void setParallaxValue(final float scrollRate)
    {
        _scrollRate = scrollRate;
    }

    /**
     * Draw the entities
     */
    @SuppressLint("WrongCall") @Override
    public void onDraw(final GLState glState, final Camera camera)
    {
        super.onDraw(glState, camera);

        final float parallaxValue = _scrollRate;
        for (VerticalParallaxEntity vpe : _entities)
            vpe.onDraw(glState, camera, parallaxValue);
    }

    /**
     * Add an entity to the set of Y-scrolling images
     * 
     * @param entity
     *            The entity to add
     */
    public void attachVerticalParallaxEntity(final VerticalParallaxEntity entity)
    {
        _entities.add(entity);
    }

    /**
     * These are the objects that we add to the parallax set
     */
    public static class VerticalParallaxEntity
    {
        /**
         * Scroll rate of this entity
         */
        final float _entityScrollRate;

        /**
         * The image to draw
         */
        final IAreaShape _shape;

        /**
         * Basic constructor
         * 
         * @param rate
         *            The rate of motion
         * @param shape
         *            The image to draw
         */
        public VerticalParallaxEntity(final float rate, final IAreaShape shape)
        {
            _entityScrollRate = rate;
            _shape = shape;
        }

        /**
         * Draw this entity
         * 
         * @param glState
         *            the current GL state
         * @param camera
         *            the camera we are using
         * @param scrollRate
         *            the rate of scrolling
         */
        @SuppressLint("WrongCall") void onDraw(final GLState glState, final Camera camera, final float scrollRate)
        {
            glState.pushModelViewGLMatrix();
            float cameraHeight = camera.getHeight();
            float shapeHeightScaled = _shape.getHeightScaled();
            float baseOffset = (scrollRate * _entityScrollRate) % shapeHeightScaled;
            // NB: The math could be much more efficient than a while loop...
            while (baseOffset > 0) {
                baseOffset -= shapeHeightScaled;
            }
            glState.translateModelViewGLMatrixf(0, (-1 * baseOffset), 0);
            float currentMaxY = baseOffset;
            do {
                _shape.onDraw(glState, camera);
                glState.translateModelViewGLMatrixf(0, (-1 * shapeHeightScaled), 0);
                currentMaxY += shapeHeightScaled;
            }
            while (currentMaxY < (cameraHeight + shapeHeightScaled));
            glState.popModelViewGLMatrix();
        }
    }
}
