
package com.jimmy.cleanarchitecture.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.jimmy.cleanarchitecture.Document
import com.jimmy.cleanarchitecture.R
import com.jimmy.cleanarchitecture.databinding.ActivityMainBinding
import com.jimmy.cleanarchitecture.presentation.library.LibraryFragment
import com.jimmy.cleanarchitecture.presentation.reader.ReaderFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MainActivityDelegate {

    private lateinit var activityMainBinding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
   activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)

    setSupportActionBar(activityMainBinding.toolbar)

    val toggle = ActionBarDrawerToggle(
        this, activityMainBinding.drawerLayout, activityMainBinding.toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close)
      activityMainBinding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

      activityMainBinding.navView.setNavigationItemSelectedListener(this)

    if(savedInstanceState == null) {
        activityMainBinding.navView.menu.findItem(R.id.nav_library).isChecked = true
        activityMainBinding.navView.menu.performIdentifierAction(R.id.nav_library, 0)
    }
  }


  override fun onBackPressed() {
    if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    // Handle navigation view item clicks here.
    when (item.itemId) {
      R.id.nav_library -> supportFragmentManager.beginTransaction()
          .replace(R.id.content, LibraryFragment.newInstance())
          .commit()
      R.id.nav_reader -> openDocument(
          Document.EMPTY)
    }
    activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }

  override fun openDocument(document: Document) {
    activityMainBinding.navView.menu.findItem(R.id.nav_reader).isChecked = true
    supportFragmentManager.beginTransaction()
        .replace(R.id.content, ReaderFragment.newInstance(document))
        .commit()
  }
}
