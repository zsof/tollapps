package hu.zsof.tollapps.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.zsof.tollapps.R
import hu.zsof.tollapps.databinding.NewMemberDialogBinding
import hu.zsof.tollapps.network.repository.LocalDataStateService
import hu.zsof.tollapps.safeNavigate

@AndroidEntryPoint
class NewMemberDialogFragment : DialogFragment() {
    private lateinit var binding: NewMemberDialogBinding
    private val viewModel: NewMemberViewModel by viewModels()
    private var type = "-1"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        binding = NewMemberDialogBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.save_btn, null)
            .setNegativeButton(R.string.cancel_btn, null)
            .create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                saveMember()
                dismiss()
                safeNavigate(NewMemberDialogFragmentDirections.actionNewMemberDialogFrToMainFr())
            }
        }

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        return dialog
    }

    private fun saveMember() {
        binding.apply {
            type = if (binding.choiceAlone.isChecked) {
                "0"
            } else if (binding.choiceOnePlus.isChecked) {
                "1"
            } else "2"
        }
        println("type $type")

        viewModel.applyNewMember(LocalDataStateService.name, type)
    }
}
