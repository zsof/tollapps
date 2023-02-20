package hu.zsof.tollapps.ui.main

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.zsof.tollapps.R
import hu.zsof.tollapps.RemoveButtonClickListener
import hu.zsof.tollapps.adapter.MainAdapter
import hu.zsof.tollapps.databinding.FragmentMainBinding
import hu.zsof.tollapps.network.repository.LocalDataStateService
import hu.zsof.tollapps.safeNavigate
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment(), RemoveButtonClickListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MainAdapter
    private val viewModel: MainViewModel by viewModels()
    private var fabVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        adapter = MainAdapter(this)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindings()
        subscribeToObservers()
    }

    private fun setupBindings() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.shared_pref_key),
            Context.MODE_PRIVATE,
        )

        binding.apply {
            currentUser.text = LocalDataStateService.name
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getEvent(LocalDataStateService.name)
            }
            exitIcon.setOnClickListener {
                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        putString(getString(R.string.shared_pref_name_key), null)
                        apply()
                    }
                }
                safeNavigate(MainFragmentDirections.actionMainFrToLoginFr())
            }

            optionBtn.setOnClickListener {
                if (fabVisible) {
                    applyNewMember.visibility = View.GONE
                    countSumBtn.visibility = View.GONE
                    optionBtn.setImageResource(R.drawable.ic_add)
                    fabVisible = false
                } else {
                    applyNewMember.visibility = View.VISIBLE
                    countSumBtn.visibility = View.VISIBLE
                    optionBtn.setImageResource(R.drawable.ic_close)
                    fabVisible = true
                }
            }

            countSumBtn.setOnClickListener {
                val input = EditText(requireContext())
                input.hint = "3000Ft"
                input.inputType = InputType.TYPE_CLASS_NUMBER

                val dialog = AlertDialog.Builder(
                    ContextThemeWrapper(
                        requireContext(),
                        R.style.AlertDialogCustom,
                    ),
                )
                    .setTitle("Mennyibe kerültek a pályák összesen?")
                    .setView(input)
                    .setPositiveButton("Számol!") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()

                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
                dialog.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeToObservers() {
        viewModel.apply {
            getEvent(LocalDataStateService.name)
            event.observe(viewLifecycleOwner) { event ->
                adapter.memberList = event.participants
                binding.recyclerMember.adapter = adapter

                val formatter = SimpleDateFormat("EEE, MMM d")
                binding.currentGameDate.text = formatter.format(event.date)

                var plusMembers = 0
                event.participants.forEach { member ->
                    if (member.contains("1")) {
                        plusMembers += 1
                    } else if (member.contains("2")) {
                        plusMembers += 2
                    }
                }
                val totalMembers = event.participants.size + plusMembers
                binding.totalMembers.text = getString(R.string.total_members, totalMembers)

                val currentTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
                val time = Calendar.getInstance().time

                binding.applyNewMember.setOnClickListener {
                    if (currentTimeFormatter.format(time) > event.deadline.toString() &&
                        (currentTimeFormatter.format(time) < event.date.toString())
                    ) {
                        val dialog = AlertDialog.Builder(requireContext())
                            .setTitle("Nincs jelentkezési időszak!")
                            .setMessage("Lejárt a jelentkezési határidő erre a játékra. \nA következő játékra jövő hét hétfőtől tudsz jelentkezni!")
                            .setPositiveButton(R.string.ok_btn) { dialog, _ ->
                                dialog.cancel()
                            }
                            .create()

                        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
                        dialog.show()
                    } else {
                        safeNavigate(MainFragmentDirections.actionMainFrToNewMemberDialogFr())
                    }
                }
            }
        }
    }

    override fun onRemoveBtnClicked() {
        viewModel.deleteApply(LocalDataStateService.name)
    }
}
