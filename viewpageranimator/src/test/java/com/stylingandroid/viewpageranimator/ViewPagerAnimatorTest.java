package com.stylingandroid.viewpageranimator;

import android.animation.TypeEvaluator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Interpolator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ViewPagerAnimatorTest {
    private ViewPagerAnimator<Number> numberAnimator;
    private TestProvider<Number> numberProvider = new TestProvider<>(new Number[]{0f, 1f, 2f});
    @Spy
    private TestProperty<Number> numberProperty = new TestProperty<>();

    private ViewPagerAnimator<Integer> integerAnimator;
    private TestProvider<Integer> integerProvider = new TestProvider<>(new Integer[]{0, 100, 200});
    @Spy
    private TestProperty<Integer> integerProperty = new TestProperty<>();

    private ViewPagerAnimator<Integer> argbAnimator;
    private TestProvider<Integer> argbProvider = new TestProvider<>(new Integer[]{0xFFFF0000, 0xFF00FF00, 0xFF0000FF});
    @Spy
    private TestProperty<Integer> argbProperty = new TestProperty<>();

    private Interpolator interpolator = new TestInterpolator();

    @Mock
    ViewPager numberViewPager, integerViewPager, argbViewPager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(numberViewPager.getAdapter()).thenReturn(numberProvider);
        when(integerViewPager.getAdapter()).thenReturn(integerProvider);
        when(argbViewPager.getAdapter()).thenReturn(argbProvider);

        numberAnimator = ViewPagerAnimator.ofFloat(numberViewPager, numberProvider, numberProperty, new TestFloatEvaluator(), interpolator);
        integerAnimator = ViewPagerAnimator.ofInt(integerViewPager, integerProvider, integerProperty, new TestIntEvaluator(), interpolator);
        argbAnimator = ViewPagerAnimator.ofArgb(argbViewPager, argbProvider, argbProperty, new TestArgbEvaluator(), interpolator);
    }

    @Test
    public void givenAStartedIntegerAnimation_whenWeReverseDirectionPastAPageBoundary_thenTheFractionValueIsCorrect() {
        integerAnimator.onPageScrolled(1, 0f, 0);
        integerAnimator.onPageScrolled(1, 0.1f, 10);

        integerAnimator.onPageScrolled(0, 0.9f, 90);

        assertThat(integerProperty.get()).isEqualTo(90);
    }

    @Test
    public void givenAnIdleIntegerAnimation_whenWeDragTheViewPager_thenTheAnimationStarts() {
        integerAnimator.onPageScrolled(1, 0f, 0);

        integerAnimator.onPageScrolled(0, 0.9f, 90);

        assertThat(integerProperty.get()).isEqualTo(90);
    }

    @Test
    public void givenAnInitialisedIntegerAnimation_whenDestroyIt_thenItIsDetachedFromTheViewPager() {
        integerAnimator.destroy();

        verify(integerViewPager, times(1)).removeOnPageChangeListener(eq(integerAnimator));
    }

    @Test
    public void givenANewlyCreatedIntegerAnimation_whenItIsInitialised_thenThePageIsSet() {
        integerAnimator.onPageScrolled(0, 0f, 0);

        verify(integerProperty, times(1)).set(eq(0));
    }

    @Test
    public void givenAStartedFloatAnimation_whenWeReverseDirectionPastAPageBoundary_thenTheFractionValueIsCorrect() {
        numberAnimator.onPageScrolled(1, 0f, 0);
        numberAnimator.onPageScrolled(1, 0.1f, 10);

        numberAnimator.onPageScrolled(0, 0.9f, 90);

        assertThat(numberProperty.get()).isEqualTo(0.9f);
    }

    @Test
    public void givenAStartedArgbAnimation_whenWeReverseDirectionPastAPageBoundary_thenTheFractionValueIsCorrect() {
        argbAnimator.onPageScrolled(1, 0f, 0);
        argbAnimator.onPageScrolled(1, 0.1f, 10);

        argbAnimator.onPageScrolled(0, 0.9f, 90);

        assertThat(argbProperty.get()).isEqualTo(0xFF1AE500);
    }

    private class TestProperty<V> implements Property<V> {
        private V value;

        @Override
        public void set(V value) {
            this.value = value;
        }

        V get() {
            return value;
        }
    }

    private class TestProvider<V> extends PagerAdapter implements Provider<V> {
        private final V[] items;

        private TestProvider(V[] items) {
            this.items = items;
        }

        @Override
        public V get(int position) {
            return items[position];
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return true;
        }
    }

    private class TestInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return input;
        }
    }

    private class TestIntEvaluator implements TypeEvaluator<Integer> {
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            int startInt = startValue;
            return (int) (startInt + fraction * (endValue - startInt));
        }
    }

    private class TestFloatEvaluator implements TypeEvaluator<Number> {
        public Number evaluate(float fraction, Number startValue, Number endValue) {
            float startFloat = startValue.floatValue();
            return startFloat + fraction * (endValue.floatValue() - startFloat);
        }
    }

    private class TestArgbEvaluator implements TypeEvaluator<Integer> {
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            int startInt = startValue;
            int startA = (startInt >> 24) & 0xff;
            int startR = (startInt >> 16) & 0xff;
            int startG = (startInt >> 8) & 0xff;
            int startB = startInt & 0xff;

            int endInt = endValue;
            int endA = (endInt >> 24) & 0xff;
            int endR = (endInt >> 16) & 0xff;
            int endG = (endInt >> 8) & 0xff;
            int endB = endInt & 0xff;

            return ((startA + (int) (fraction * (endA - startA))) << 24) |
                    ((startR + (int) (fraction * (endR - startR))) << 16) |
                    ((startG + (int) (fraction * (endG - startG))) << 8) |
                    ((startB + (int) (fraction * (endB - startB))));
        }
    }
}
