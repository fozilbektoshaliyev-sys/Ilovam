package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MediaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AniManhwa3D Uz", appName)
  }

  @Test
  fun `viewmodel initialization succeeds`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = MediaViewModel(application)
    assertNotNull(viewModel)
    
    // Check initial values
    assertEquals("asosiy", viewModel.currentTab.value)
    assertEquals(false, viewModel.isUserVip.value)
    assertNotNull(viewModel.filteredMediaList.value)
  }
}
