package ir.iust.apps.mobilenetwork.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class UiUtils {
    fun changeFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int,
        isReplaced: Boolean,
        tagName: String
    ) {
        if (isReplaced) {
            fragmentManager
                .beginTransaction()
                .replace(containerId, fragment)
                .commit()
        } else {
            fragmentManager
                .beginTransaction()
                .add(containerId, fragment, tagName)
                .commit()
        }
    }
}