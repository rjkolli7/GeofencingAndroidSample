package com.rjkolli.gfsample.ui.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.databinding.FragmentMainBinding
import com.rjkolli.gfsample.di.injector.Injectable
import com.rjkolli.gfsample.helper.getCurrentSsid
import com.rjkolli.gfsample.ui.base.BaseFragment
import javax.inject.Inject

class MainFragment : BaseFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainViewModel::class.java)
        viewModel.setupMap(childFragmentManager)
        binding.model = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        context?.let { context -> getCurrentSsid(context) }
    }

    override fun onLocationPermissionAccept() {
        viewModel.onLocationPermissionGrant()
    }
}
