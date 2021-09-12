package com.vincentwang.mobilephone.ui.currency

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.vincentwang.mobilephone.MainActivity
import com.vincentwang.mobilephone.R
import com.vincentwang.mobilephone.databinding.FragmentCurrencyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class CurrencyFragment : Fragment() {

    lateinit var act: MainActivity
    lateinit var binding: FragmentCurrencyBinding
    private val viewModel by viewModel<CurrencyViewModel>()
    private val adapter = CurrencyAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        act = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false).apply{
            vm = viewModel
            lifecycleOwner = this@CurrencyFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rvRateList.adapter = adapter
            rvRateList.layoutManager = GridLayoutManager(act,3)
        }
        viewModel.submitList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
        viewModel.clickLiveEvent.observe(viewLifecycleOwner){
            when(it){
                R.id.tvCurrency->{
                    viewModel.showCurrencyListDialog().observe(viewLifecycleOwner){
                        MaterialDialog(act).show {
                            listItemsSingleChoice(items = it) { dialog, index, text ->
                                viewModel.selectCurrency(text.toString())
                            }
                            positiveButton(R.string.select)
                        }
                    }

                }
            }
        }
        viewModel.showErrorDialog.observe(viewLifecycleOwner) {error->
            MaterialDialog(act).show{
                title(text="Error")
                message(text = error)
                positiveButton(R.string.confirm)
            }
        }

    }
}