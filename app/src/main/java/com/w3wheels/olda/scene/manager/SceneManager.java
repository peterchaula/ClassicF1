/**
 * 
 */
/**
 * @author Peter
 *
 */
package com.w3wheels.olda.scene.manager;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;


public class SceneManager{
	public SceneManager(){
		
	}
	
	public void setScene(Engine pEngine, Scene pScene){
		pEngine.getEngineLock().lock();
		pEngine.setScene(pScene);
		pEngine.getEngineLock().unlock();
	}
	
	public void displaySplashScreen(){
		
	}
}