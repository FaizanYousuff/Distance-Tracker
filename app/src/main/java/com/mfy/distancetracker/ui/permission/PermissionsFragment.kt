package com.mfy.distancetracker.ui.permission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mfy.distancetracker.R
import com.mfy.distancetracker.util.Permissions.hasLocationPermission
import com.mfy.distancetracker.util.Permissions.requestLocationPermission
import com.mfy.distancetracker.databinding.FragmentPermissionsBinding
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog


class PermissionsFragment : Fragment(),EasyPermissions.PermissionCallbacks {

    private lateinit var binding : FragmentPermissionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPermissionsBinding.inflate(inflater,container, false)

        binding.btnContinue.setOnClickListener{
            if(hasLocationPermission(requireActivity())){
                findNavController().navigate(R.id.action_permissionsFragment_to_mapsFragment)
            } else {
                requestLocationPermission(this,getString(R.string.location_permission_rationale))
            }
        }
        return binding.root
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.permissionPermanentlyDenied(this,perms[0])){
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission(this,getString(R.string.location_permission_rationale))
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        findNavController().navigate(R.id.action_permissionsFragment_to_mapsFragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)

    }

}