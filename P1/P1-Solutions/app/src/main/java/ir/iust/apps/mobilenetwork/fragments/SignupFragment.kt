package ir.iust.apps.mobilenetwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import ir.iust.apps.mobilenetwork.viewmodels.MainViewModel
import ir.iust.apps.mobilenetwork.Pages
import ir.iust.apps.mobilenetwork.R
import ir.iust.apps.mobilenetwork.Regex

class SignupFragment : Fragment() {

    // Android View Models
    private val viewModel: MainViewModel by viewModels()

    // views
    private lateinit var emailInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var signupBtn: MaterialButton
    private lateinit var goToLoginBtn: MaterialTextView
    private lateinit var backBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        // Bind views
        emailInput = view.findViewById(R.id.signup_fragment__email)
        phoneInput = view.findViewById(R.id.signup_fragment__phone)
        signupBtn = view.findViewById(R.id.signup_fragment__signup_btn)
        goToLoginBtn = view.findViewById(R.id.signup_fragment__go_to_login_btn)
        backBtn = view.findViewById(R.id.signup_fragment__back_btn)

        // Set listeners
        emailInput.addTextChangedListener {
            if (Regex.EMAIL_REGEX.matches(emailInput.text.toString())) {
                emailInput.error = null
            } else {
                emailInput.error = "Invalid Email"
            }
        }
        phoneInput.addTextChangedListener {
            if (Regex.IRAN_PHONE_REGEX.matches(phoneInput.text.toString())) {
                phoneInput.error = null
            } else {
                phoneInput.error = "Invalid Phone"
            }
        }
        signupBtn.setOnClickListener {
            Toast.makeText(context, "User created", Toast.LENGTH_LONG).show()
        }
        goToLoginBtn.setOnClickListener {
            viewModel.pageState.postValue(Pages.LOGIN)
        }
        backBtn.setOnClickListener {
            viewModel.pageState.postValue(Pages.LOGIN)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignupFragment()
    }
}