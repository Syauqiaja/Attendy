package com.hashtest.attendy.presentation.main.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashtest.attendy.databinding.BottomSheetTimePickerBinding
import kotlin.math.max

class BottomSheetTimePicker(private val onButtonClicked: OnButtonClicked) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetTimePickerBinding
    private val hourList = (0..11).map { String.format("%02d", it) }.toTypedArray()
    private val minuteList = (0..59).map { String.format("%02d", it) }.toTypedArray()
    private val amList = arrayOf("AM", "PM")
    private var vibrator: Vibrator? = null
    private var isSaving = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetTimePickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        binding.apply {
            pickerHour.apply {
                minValue = 0
                maxValue = 11
                displayedValues = hourList


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        vibrator?.cancel()
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator?.vibrate(effect)
                    }
                }
            }
            pickerMinute.apply {
                minValue = 0
                maxValue = 59
                displayedValues = minuteList
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        vibrator?.cancel()
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator?.vibrate(effect)
                    }
                }
            }
            pickerAm.apply {
                minValue = 0
                maxValue = 1
                displayedValues = amList
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        vibrator?.cancel()
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator?.vibrate(effect)
                    }
                }
            }

            btnSave.setOnClickListener {
                onButtonClicked.onSave(pickerHour.value, pickerMinute.value, amList[pickerAm.value])
                isSaving = true
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(!isSaving){
            onButtonClicked.onCancel()
        }
    }
    public interface OnButtonClicked{
        abstract fun onCancel()
        abstract fun onSave(hour: Int, minute: Int, am: String)
    }
}
