# ViewPagerAnimator

_ViewPagerAnimator_ is a new lightweight, yet powerful _ViewPager_ animation library for Android. it is designed to animate arbitrary values as the user navigates between pages within a _ViewPager_, and will precisely follow the motion of h[is|er] finger. Although the library itself may be of use to some, the main purpose of publishing this library is to demonstrate some wonderful API subtleties which really come to the fore when using Java 8 extensions which are coming our way soon. Sample projects for both Java 7 and Java 8 are provided.

More comprehensive documentation is available on the [Styling Android blog](https://blog.stylingandroid.com/viewpageranimator-the-basics/)

The library is published to jcenter and can be included in to a project just add the following to the _dependencies_ section:

`compile 'com.stylingandroid.viewpageranimator:viewpageranimator:1.0.1'`

## Java 7

To use _ViewPagerAnimator_ in Java 7 code it is necessary to implement two interfaces which act as facades to arbitrary objects of your chosing: The _Provider_ interface will provide an arbitrary value for each pager position within the _ViewPager_ (this would typicaly be a value controlled by the _PagerAdapter_); the _Property_ interface will  control the value we wish to animate (this would typically be a value which controlled the appearance of a _View_).

The only thing that we need to do is ensure that the value types for both the _Provider_ and _Property_ match. As both are Generic interfaces, then we need to match the Generic types. For example _Provider&lt;**Integer**&gt;_ would need to be matched with _Property&lt;**Integer**&gt;_. 

### Provider

We can create a _Provider_ facade to our custom _PagerAdapter_ which has a method named `getColour(int position)` which will return a distinct colour value for each page position within the _ViewPager_:

```java
final ViewPager viewPager = ...;
final ColourPagerAdapter pagerAdapter = ...;
viewPager.setAdapter(pagerAdapter);
Provider<Integer> provider = new Provider<Integer>() {
    @Override
    public Integer get(int position) {
        return pagerAdapter.getColour(position);
    }
};
```

So now we have a _Provider_ instance. Any consumer of this requires no knowledge of _ColourPagerAdapter_ put can retrieve a value from it using `Provider#get(int position)`.

### Property

We can create a _Property_ facade to the _ViewPager_ itself to change the background colour:

```java
Property<Integer> property = new Property<Integer>() {
    @Override
    public void set(Integer value) {
        viewPager.setBackgroundColor(value);
    }
};
```

Once again the _Property_ facade allows a component to set the background colour of the _ViewPager_ instance without any knowledge of what a _ViewPager_ is. This becomes particularly powerful if we are changing the appearance of any arbitrary UI component such as the system bar colour.

### ViewPagerAnimator

Now that we have a _Provider_ and a _Property_ we can create the _ViewPagerAnimator_:

```java
ViewPagerAnimator animator = ViewPagerAnimator.ofArgb(viewPager, provider, property);
```

There are three factory methods which enable the construction of ViewPagerAnimator instances whic will animate ARGB colour values (as in this example), _Integer_ values, or _Float_ values. It is also possible to animate any object type you like by calling the constructor directly, but you will need to provide a _TypeEvaluator_ which will be responsible for calculating intermediate values.

The compiler will give an error if we mis-match the generic types of the _Provider_ and a _Property_ implementation (i.e. if we were to mix a _Property&lt;**Float**&gt;_ with a _Provider&lt;**Integer**&gt;_).

## Java 8

Although we are not required to do _that_ much work in Java 7, things become much terser and more fluent if we use Java 8 (this is currently in the 2.4 alpha 6 and later Android gradle plugin). The exact same functionality as Java7 can be implemented in this way:

```java
final ViewPager viewPager = ...;
final ColourPagerAdapter pagerAdapter = ...;
viewPager.setAdapter(pagerAdapter);
ViewPagerAnimator animator = ViewPagerAnimator.ofArgb(viewPager, pagerAdapter::getColour, viewPager::setBackgroundColor);
```

Here we can use Java 8 method references to save us from having to actually create the _Provider_ and  _Property_ instances as long as we specify methods which match the method signature of the single method in each facade interface.

The facade pattern is really useful for decoupling components, but when we then add it Java 8 method references the APIs become really simple and extremely fluent. That is the really interesting thing here, IMO.
