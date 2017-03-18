package com.example.brennan.lister.util;

/**
 * Created by Brennan on 2/25/2017.
 */
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimationHelper {

    private static final int HALF_A_SECOND = 500;
    private static final float VISIBLE = 1.0f;
    private static final float INVISIBLE = 0.0f;

    /**
     * @return A fade out animation from 100% - 0% taking half a second
     */
    public static Animation createFadeoutAnimation() {
        Animation fadeout = new AlphaAnimation(VISIBLE, INVISIBLE);
        fadeout.setDuration(HALF_A_SECOND);
        return fadeout;
    }

    /**
     * @return A fade in animation from 0% - 100% taking half a second
     */
    public static Animation createFadeInAnimation() {
        Animation animation = new AlphaAnimation(INVISIBLE, VISIBLE);
        animation.setDuration(HALF_A_SECOND);
        return animation;
    }

}