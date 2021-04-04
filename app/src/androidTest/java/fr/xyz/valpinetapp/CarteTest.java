package fr.xyz.valpinetapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CarteTest {

    @Rule
    public ActivityTestRule<Accueil> mActivityTestRule = new ActivityTestRule<>(Accueil.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void carteTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.b_francais), withText("Liste des excursions et ascensions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.imageView4),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatImageView.perform(click());

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataInteraction materialTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.lv_maListe),
                        childAtPosition(
                                withId(R.id.linearLayout),
                                0)))
                .atPosition(0);
        materialTextView.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button3), withText("Y aller !"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction zoomButton = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                0),
                        isDisplayed()));
        zoomButton.perform(click());

        ViewInteraction zoomButton2 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                0),
                        isDisplayed()));
        zoomButton2.perform(click());

        ViewInteraction zoomButton3 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                0),
                        isDisplayed()));
        zoomButton3.perform(longClick());

        ViewInteraction zoomButton4 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                0),
                        isDisplayed()));
        zoomButton4.perform(longClick());

        ViewInteraction zoomButton5 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                0),
                        isDisplayed()));
        zoomButton5.perform(longClick());

        ViewInteraction zoomButton6 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                0),
                        isDisplayed()));
        zoomButton6.perform(click());

        ViewInteraction zoomButton7 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                1),
                        isDisplayed()));
        zoomButton7.perform(click());

        ViewInteraction zoomButton8 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                1),
                        isDisplayed()));
        zoomButton8.perform(click());

        ViewInteraction zoomButton9 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                1),
                        isDisplayed()));
        zoomButton9.perform(click());

        ViewInteraction zoomButton10 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                1),
                        isDisplayed()));
        zoomButton10.perform(click());

        ViewInteraction zoomButton11 = onView(
                allOf(withClassName(is("android.widget.ZoomButton")),
                        childAtPosition(
                                withClassName(is("android.widget.ZoomControls")),
                                1),
                        isDisplayed()));
        zoomButton11.perform(click());

        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.mv_map),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
