package fr.xyz.valpinetapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TabExcursionVideTest2 {

    @Rule
    public ActivityTestRule<Accueil> mActivityTestRule = new ActivityTestRule<>(Accueil.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void tabExcursionVideTest2() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.b_francais), withText("Liste des excursions et ascensions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("aucune excursion"),
                        withParent(allOf(withId(R.id.lv_maListe),
                                withParent(withId(R.id.linearLayout)))),
                        isDisplayed()));
        textView.check(matches(withText("aucune excursion")));

        ViewInteraction listView = onView(
                allOf(withId(R.id.lv_maListe),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        listView.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.imageView4),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.imageView4),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatImageView.perform(click());
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
