package com.stylingandroid.viewpageranimator;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.TypeEvaluator;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class ViewPagerAnimator<V> implements ViewPager.OnPageChangeListener {
    private final Provider<V> provider;
    private final Property<V> property;
    private final TypeEvaluator<V> evaluator;

    private ViewPager viewPager;

    private V startValue;
    private V endValue;

    private Interpolator interpolator;

    private int currentPage = 0;
    private int targetPage = -1;
    private int lastPosition = 0;

    public static ViewPagerAnimator<Integer> ofInt(ViewPager viewPager, Provider<Integer> provider, Property<Integer> property) {
        final IntEvaluator evaluator = new IntEvaluator();
        final Interpolator interpolator = new LinearInterpolator();

        return ofInt(viewPager, provider, property, evaluator, interpolator);
    }

    @VisibleForTesting
    static ViewPagerAnimator<Integer> ofInt(ViewPager viewPager,
                                            Provider<Integer> provider,
                                            Property<Integer> property,
                                            TypeEvaluator<Integer> evaluator,
                                            Interpolator interpolator) {
        return new ViewPagerAnimator<>(viewPager, provider, property, evaluator, interpolator);
    }

    public static ViewPagerAnimator<Integer> ofArgb(ViewPager viewPager, Provider<Integer> provider, Property<Integer> property) {
        @SuppressWarnings("unchecked") final TypeEvaluator<Integer> evaluator = new ArgbEvaluator();
        final Interpolator interpolator = new LinearInterpolator();

        return ofArgb(viewPager, provider, property, evaluator, interpolator);
    }

    @VisibleForTesting
    static ViewPagerAnimator<Integer> ofArgb(ViewPager viewPager,
                                             Provider<Integer> provider,
                                             Property<Integer> property,
                                             TypeEvaluator<Integer> evaluator,
                                             Interpolator interpolator) {
        return new ViewPagerAnimator<>(viewPager, provider, property, evaluator, interpolator);
    }

    public static ViewPagerAnimator<Number> ofFloat(ViewPager viewPager, Provider<Number> provider, Property<Number> property) {
        final FloatEvaluator evaluator = new FloatEvaluator();
        final Interpolator interpolator = new LinearInterpolator();

        return ofFloat(viewPager, provider, property, evaluator, interpolator);
    }

    @VisibleForTesting
    static ViewPagerAnimator<Number> ofFloat(ViewPager viewPager,
                                             Provider<Number> provider,
                                             Property<Number> property,
                                             TypeEvaluator<Number> evaluator,
                                             Interpolator interpolator) {
        return new ViewPagerAnimator<>(viewPager, provider, property, evaluator, interpolator);
    }

    public ViewPagerAnimator(@NonNull ViewPager viewPager,
                             @NonNull Provider<V> provider,
                             @NonNull Property<V> property,
                             @NonNull TypeEvaluator<V> evaluator) {
        this(viewPager, provider, property, evaluator, new LinearInterpolator());
    }

    public ViewPagerAnimator(@NonNull ViewPager viewPager,
                             @NonNull Provider<V> provider,
                             @NonNull Property<V> property,
                             @NonNull TypeEvaluator<V> evaluator,
                             @NonNull Interpolator interpolator) {
        setViewPager(viewPager);
        this.provider = provider;
        this.property = property;
        this.evaluator = evaluator;
        setInterpolator(interpolator);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0 && positionOffsetPixels == 0 && !isAnimating()) {
            onPageSelected(0);
        }
        if (isAnimating() && lastPosition != position || positionOffsetPixels == 0) {
            endAnimation(position);
        }
        if (positionOffsetPixels > 0) {
            beginAnimation(position);
        }
        if (isAnimating()) {
            float fraction = interpolator.getInterpolation(positionOffset);
            V value = evaluator.evaluate(fraction, startValue, endValue);
            property.set(value);
        }
        lastPosition = position;
    }

    private boolean isAnimating() {
        return targetPage >= 0;
    }

    private void endAnimation(int position) {
        currentPage = position;
        targetPage = -1;
    }

    private void beginAnimation(int position) {
        PagerAdapter adapter = viewPager.getAdapter();
        if (position == currentPage && position + 1 < adapter.getCount()) {
            targetPage = position + 1;
            startValue = provider.get(position);
            endValue = provider.get(targetPage);
        } else if (position >= 0) {
            targetPage = position;
            startValue = provider.get(position);
            endValue = provider.get(currentPage);
        }
    }

    @Override
    public void onPageSelected(int position) {
        V value = provider.get(position);
        property.set(value);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void destroy() {
        viewPager.removeOnPageChangeListener(this);
    }

    public void setViewPager(ViewPager viewPager) {
        if (this.viewPager != null) {
            this.viewPager.removeOnPageChangeListener(this);
        }
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this);
    }

    public void setInterpolator(Interpolator newInterpolator) {
        if (newInterpolator == null) {
            interpolator = new LinearInterpolator();
        } else {
            interpolator = newInterpolator;
        }
    }
}
