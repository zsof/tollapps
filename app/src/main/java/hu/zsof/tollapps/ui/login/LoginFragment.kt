package hu.zsof.tollapps.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hu.zsof.tollapps.R
import hu.zsof.tollapps.databinding.LoginFragmentBinding
import hu.zsof.tollapps.network.repository.LocalDataStateService
import hu.zsof.tollapps.safeNavigate
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindings()
    }

    private fun setupBindings() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.shared_pref_key),
            Context.MODE_PRIVATE,
        )

        val storedName = sharedPref?.getString(getString(R.string.shared_pref_name_key), "")
        if (!storedName.isNullOrEmpty()) {
            LocalDataStateService.name = storedName
            safeNavigate(LoginFragmentDirections.actionLoginFrToMainFr())
        }

        binding.apply {
            nextButton.setOnClickListener {
                val name = nameTextInput.text?.trim().toString()
                lifecycleScope.launch {
                    if (name.isNotEmpty()) {
                        val response = viewModel.login(name)
                        if (response != null) {
                            if (response.success) {
                                LocalDataStateService.name = name
                                safeNavigate(LoginFragmentDirections.actionLoginFrToMainFr())
                            }
                        }
                    }
                }

                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        putString(getString(R.string.shared_pref_name_key), name)
                        apply()
                    }
                }
            }
        }
    }
}
