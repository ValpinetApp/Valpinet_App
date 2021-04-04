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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InfoExcursionsTest {

    @Rule
    public ActivityTestRule<Accueil> mActivityTestRule = new ActivityTestRule<>(Accueil.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void infoExcursionsTest() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_distanceValue), withText("0.6"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView.check(matches(withText("0.6")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_dureeValue), withText("5min"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView2.check(matches(withText("5min")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_monteeValue), withText("1"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView3.check(matches(withText("1")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.tv_descenteValue), withText("1"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView4.check(matches(withText("1")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.tv_typePValue), withText("Circular"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView5.check(matches(withText("Circular")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.tv_hostiliteValue), withText("1"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView6.check(matches(withText("1")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.tv_difficulteTValue), withText("1"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView7.check(matches(withText("1")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.tv_difficulteOValue), withText("1"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView8.check(matches(withText("1")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.tv_effortValue), withText("1"),
                        withParent(withParent(withId(R.id.tl_excursion))),
                        isDisplayed()));
        textView9.check(matches(withText("1")));

        ViewInteraction tableLayout = onView(
                allOf(withId(R.id.tl_excursion),
                        withParent(withParent(withId(R.id.scrollView3))),
                        isDisplayed()));
        tableLayout.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.button3), withText("Y ALLER !"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
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
