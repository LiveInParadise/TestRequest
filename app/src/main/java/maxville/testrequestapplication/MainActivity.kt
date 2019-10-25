package maxville.testrequestapplication

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private var address: String = ""
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        address = getAddress()
        setTitle(address)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val etAddress = navView.getHeaderView(0).findViewById<EditText>(R.id.etAddress)
        val btnSave = navView.getHeaderView(0).findViewById<Button>(R.id.btnSave)
        etAddress.setText(address)
        btnSave.setOnClickListener {
            address = etAddress.text.toString()
            setTitle(address)
            storeAddress(address)
            drawerLayout.closeDrawers()
        }

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.text.observe(this, Observer { text ->
            tvResponse.text = text
        })

        homeViewModel.toast.observe(this, Observer { text ->
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        })

        homeViewModel.progressShow.observe(this, Observer { isShow ->
            if (isShow) {
                progressBar.visibility = View.VISIBLE
                tvResponse.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                tvResponse.visibility = View.VISIBLE
            }
        })

        btnSendRequest.setOnClickListener {
            homeViewModel.executeRequest(address)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setTitle(address: String) {
        tvAddress.text = String.format(getString(R.string.title_server_address), address)
    }

    private fun storeAddress(address: String) {
        val editor = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit()
        editor.putString(ADDRESS_PREFS, address).apply()
    }

    private fun getAddress(): String =
        getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(ADDRESS_PREFS, "")
            ?: ""

    companion object {
        const val ADDRESS_PREFS = "address"
        const val SHARED_PREFS = "settings"
    }
}
