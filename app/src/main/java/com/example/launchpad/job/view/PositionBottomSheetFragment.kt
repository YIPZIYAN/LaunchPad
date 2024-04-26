package com.example.launchpad.job.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.launchpad.databinding.FragmentPositionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PositionBottomSheetFragment(checkedState: MutableList<String>) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private lateinit var binding: FragmentPositionBottomSheetBinding
    private var listener: BottomSheetListener? = null // Interface reference
    private var checkedState = checkedState // Stores checked states

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPositionBottomSheetBinding.inflate(inflater, container, false)

        binding.chkDirector.isChecked = checkedState.contains("Director")
        binding.chkManager.isChecked = checkedState.contains("Manager")
        binding.chkSenior.isChecked = checkedState.contains("Senior")
        binding.chkJunior.isChecked = checkedState.contains("Junior")

        checkedState.clear()

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (binding.chkDirector.isChecked) checkedState.add("Director")
        if (binding.chkManager.isChecked) checkedState.add("Manager")
        if (binding.chkSenior.isChecked) checkedState.add("Senior")
        if (binding.chkJunior.isChecked) checkedState.add("Junior")

        Log.d("LIST", checkedState.toString())
        listener?.onValueSelected(checkedState)
    }

    fun setListener(listener: BottomSheetListener) {
        this.listener = listener
    }

}

interface BottomSheetListener {
    fun onValueSelected(value: List<String>)
}