package com.rjkolli.gfsample.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.databinding.FragmentSettingsBinding
import com.rjkolli.gfsample.di.injector.Injectable
import com.rjkolli.gfsample.ui.base.BaseFragment
import javax.inject.Inject

class SettingsFragment : BaseFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SettingsViewModel

    lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SettingsViewModel::class.java)
        viewModel.childFragmentManager = childFragmentManager
        binding.model = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }

    override fun onLocationPermissionAccept() {
        viewModel.onLocationPermissionGrant()
    }
}