package com.mfy.distancetracker.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mfy.distancetracker.R
import com.mfy.distancetracker.databinding.FragmentResultBinding


class ResultFragment : BottomSheetDialogFragment() {


    // Lazy initialization args
    private val args : ResultFragmentArgs by navArgs()
    private var _binding : FragmentResultBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=  FragmentResultBinding.inflate(inflater,container,false)

        binding.distanceValueTextView.text = getString(R.string.result,args.result.distance)
        binding.timeValueTextView.text = args.result.time
        binding.shareButton.setOnClickListener{
            shareResults()
        }


        return binding.root


    }

    private fun shareResults() {
        /*val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT,"I covered  ${args.result.distance}km in ${args.result.time}")
        }
        startActivity(shareIntent)*/
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}