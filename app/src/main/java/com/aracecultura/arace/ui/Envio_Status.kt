package com.aracecultura.arace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentEnvioStatusBinding


class Envio_Status : Fragment() {
    private var _biding: FragmentEnvioStatusBinding? = null
    private val binding get() = _biding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _biding = FragmentEnvioStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _biding= null
    }
}