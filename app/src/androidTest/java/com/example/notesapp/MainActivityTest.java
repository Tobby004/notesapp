package com.example.notesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void launch() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testAddNoteSuccessfully() {
        onView(withId(R.id.etNote)).perform(typeText("Buy milk"), closeSoftKeyboard());
        onView(withId(R.id.btnAdd)).perform(click());
        onView(withText("Buy milk")).check(matches(withText("Buy milk")));
    }

    @Test
    public void testSelectNote() {
        onView(withId(R.id.etNote)).perform(typeText("Hello"), closeSoftKeyboard());
        onView(withId(R.id.btnAdd)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.lvNotes)).atPosition(0).perform(click());
    }

    @Test
    public void testDeleteSelectedNote() {
        onView(withId(R.id.etNote)).perform(typeText("Delete me"), closeSoftKeyboard());
        onView(withId(R.id.btnAdd)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.lvNotes)).atPosition(0).perform(click());
        onView(withId(R.id.btnDelete)).perform(click());
    }
}
