package ir.iust.apps.mobilenetwork

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.iust.apps.mobilenetwork.fragments.LoginFragment
import ir.iust.apps.mobilenetwork.fragments.SignupFragment
import ir.iust.apps.mobilenetwork.utils.UiUtils
import ir.iust.apps.mobilenetwork.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {

    // Fragments
    private val loginFragment: LoginFragment = LoginFragment.newInstance()
    private val signupFragment: SignupFragment = SignupFragment.newInstance()

    // Android View Models
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default fragment is login,
        // Change the fragment to login fragment.
        changeMainFragment(loginFragment, LOGIN_FRAGMENT_TAG)

        // Set observers
        viewModel.pageState.observe(this, {
            Log.i(LOG_TAG, "New page state is: $it")
            it?.let {
                when (it) {
                    Pages.LOGIN -> changeMainFragment(loginFragment, LOGIN_FRAGMENT_TAG)
                    Pages.SIGNUP -> changeMainFragment(signupFragment, SIGNUP_FRAGMENT_TAG)
                }
            }
        })
    }

    private fun changeMainFragment(fragment: Fragment, tag: String) {
        UiUtils().changeFragment(
            supportFragmentManager,
            fragment,
            R.id.main_activity__main_frame,
            true,
            tag
        )
    }

    companion object {
        const val LOG_TAG = "MAIN_ACTIVITY"

        const val LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT"
        const val SIGNUP_FRAGMENT_TAG = "SIGNUP_FRAGMENT"
    }
}