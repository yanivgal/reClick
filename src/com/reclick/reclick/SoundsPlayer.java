package com.reclick.reclick;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundsPlayer {

    private SoundPool soundPool = null;
    @SuppressLint("UseSparseArrays")
	private HashMap<Integer, Integer> sounds = new HashMap<Integer, Integer>();

    public SoundsPlayer(Context pContext)
    {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        
        sounds.put(R.raw.blue, this.soundPool.load(pContext, R.raw.blue, 1));
        sounds.put(R.raw.green, this.soundPool.load(pContext, R.raw.green, 1));
        sounds.put(R.raw.red, this.soundPool.load(pContext, R.raw.red, 1));
        sounds.put(R.raw.yellow, this.soundPool.load(pContext, R.raw.yellow, 1));
        sounds.put(R.raw.wrong, this.soundPool.load(pContext, R.raw.wrong, 1));
    }

    public void playResource(int piResource) {
        int iSoundId = (Integer) sounds.get(piResource);
        this.soundPool.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
    }
    
    public void release() {
        this.soundPool.release();
        this.soundPool = null;
    }
}
