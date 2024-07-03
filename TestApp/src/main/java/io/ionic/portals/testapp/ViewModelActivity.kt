package io.ionic.portals.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import io.ionic.portals.PortalBuilder
import io.ionic.portals.PortalFragment

class ViewModelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_viewmodel)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(
                    R.id.fragmentContainerView,
                    PortalFragment(
                        portal = PortalBuilder("testportal").create()
                    ),
                    "test"
                )
            }
        }
    }
}