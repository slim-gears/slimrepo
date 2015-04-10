package com.slimgears.slimorm.android;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest=Config.NONE)
public class RepositoryPrototypeTest  {
    @Test
    public void simpleTest() {
        assertThat(RuntimeEnvironment.application, notNullValue());
    }
}