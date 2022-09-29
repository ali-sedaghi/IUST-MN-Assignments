package ir.iust.apps.mobilenetwork.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import ir.iust.apps.mobilenetwork.viewmodels.MainViewModel
import ir.iust.apps.mobilenetwork.Pages
import ir.iust.apps.mobilenetwork.R
import ir.iust.apps.mobilenetwork.Regex
import java.util.*

class LoginFragment : Fragment() {

    // Android View Models
    private val viewModel: MainViewModel by viewModels()

    // Views
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var googleBtn: MaterialButton
    private lateinit var facebookBtn: MaterialButton
    private lateinit var goToSignupBtn: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Bind views
        emailInput = view.findViewById(R.id.login_fragment__email)
        loginBtn = view.findViewById(R.id.login_fragment__login_btn)
        googleBtn = view.findViewById(R.id.login_fragment__google_btn)
        facebookBtn = view.findViewById(R.id.login_fragment__facebook_btn)
        goToSignupBtn = view.findViewById(R.id.login_fragment__go_to_sign_up_btn)

        // Set listeners
        emailInput.addTextChangedListener {
            if (Regex.EMAIL_REGEX.matches(emailInput.text.toString())) {
                emailInput.error = null
            } else {
                emailInput.error = "Invalid Email"
            }
        }
        loginBtn.setOnClickListener {
            Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
        }
        googleBtn.setOnClickListener {
            googleBtn.setBackgroundColor(generateRandomColor())
        }
        facebookBtn.setOnClickListener {
            facebookBtn.setBackgroundColor(generateRandomColor())
        }
        goToSignupBtn.setOnClickListener {
            Log.i(LOG_TAG, "Go to Signup page btn was clicked")
            viewModel.pageState.postValue(Pages.SIGNUP)
            Log.i(LOG_TAG, "New state is ${viewModel.pageState.value}")
        }

        return view
    }

    private fun generateRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    companion object {
        const val LOG_TAG = "LOGIN_PAGE"

        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}