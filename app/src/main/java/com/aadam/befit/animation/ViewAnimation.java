package com.aadam.befit.animation;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by aadam on 14/4/2017.
 */

public abstract class ViewAnimation {

    static int repeatNumber;

    //Animation to shake a view
    public static void shake(final View view) {

        repeatNumber = 2;

        final int toPosition = 10;
        final int duration = 25;

        final Animation animation1 = new TranslateAnimation(0, toPosition, 0, 0);
        animation1.setDuration(duration);
        animation1.setFillAfter(true);

        animation1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Animation animation2 = new TranslateAnimation(toPosition, -toPosition, 0, 0);
                animation2.setDuration(duration);
                animation2.setFillAfter(true);

                animation2.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        Animation animation3 = new TranslateAnimation(-toPosition, 0, 0, 0);
                        animation3.setDuration(duration);
                        animation3.setFillAfter(true);

                        animation3.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                if (repeatNumber > 0) {

                                    repeatNumber--;

                                    view.startAnimation(animation1);
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {


                            }
                        });

                        view.startAnimation(animation3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                view.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });

        view.startAnimation(animation1);
    }

    //Show alert with alpha animation
    public static void showAlert(final View view) {

        final AlphaAnimation animation1 = new AlphaAnimation(view.getAlpha(), 1.0f);
        animation1.setDuration(500);
        animation1.setStartOffset(0);
        animation1.setFillAfter(true);

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


                view.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                final AlphaAnimation animation2 = new AlphaAnimation(view.getAlpha(), 0.0f);
                animation2.setDuration(500);
                animation2.setStartOffset(1500);
                animation2.setFillAfter(true);

                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        view.setAlpha(0.0f);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                view.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animation1);
    }

    //Show any view with alpha animation
    public static void moveViewWithAlpha(final View view, final float alpha, int duration, @Nullable final AnimationInterface animationInterface) {

        view.animate().alpha(alpha).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                view.setAlpha(alpha);

                if (animationInterface != null) {

                    animationInterface.completed();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public interface AnimationInterface {

        void completed();
    }
}
