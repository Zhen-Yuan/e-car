package com.example.denis.ecar.fragmentAnimation;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.animation.Transformation;
import com.labo.kaji.fragmentanimations.ViewPropertyAnimation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Denis on 01.11.2017.
 */

public class MoveAnimation extends ViewPropertyAnimation {


    @IntDef({UP, DOWN, LEFT, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Direction {}
    public static final int UP    = 1;
    public static final int DOWN  = 2;
    public static final int LEFT  = 3;
    public static final int RIGHT = 4;

    protected final @Direction int mDirection;
    protected final boolean mEnter;

    /**
     * Neue Animation Erstellen
     * @param direction Richtung der Animation
     * @param enter true = anfang, false = ende
     * @param duration Dauer der Animation (300 ist ein guter Wert)
     * @return
     */
    public static @NonNull
    MoveAnimation create(@Direction int direction, boolean enter, long duration) {
        switch (direction) {
            case UP:
            case DOWN:
                return new VerticalMoveAnimation(direction, enter, duration);
            case LEFT:
            case RIGHT:
            default:
                return new HorizontalMoveAnimation(direction, enter, duration);
        }
    }

    private MoveAnimation(@Direction int direction, boolean enter, long duration) {
        mDirection = direction;
        mEnter = enter;
        setDuration(duration);
    }

    private static class VerticalMoveAnimation extends MoveAnimation {

        private VerticalMoveAnimation(@Direction int direction, boolean enter, long duration) {
            super(direction, enter, duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float value = mEnter ? (interpolatedTime - 1.0f) : interpolatedTime;
            if (mDirection == DOWN) value *= -1.0f;
            mTranslationY = -value * mHeight;

            super.applyTransformation(interpolatedTime, t);
            applyTransformation(t);
        }

    }

    private static class HorizontalMoveAnimation extends MoveAnimation {

        private HorizontalMoveAnimation(@Direction int direction, boolean enter, long duration) {
            super(direction, enter, duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float value = mEnter ? (interpolatedTime - 1.0f) : interpolatedTime;
            if (mDirection == RIGHT) value *= -1.0f;
            mTranslationX = -value * mWidth;

            super.applyTransformation(interpolatedTime, t);
            applyTransformation(t);
        }

    }

}
