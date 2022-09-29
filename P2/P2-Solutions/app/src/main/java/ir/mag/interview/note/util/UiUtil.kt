package ir.mag.interview.note.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object UiUtil {

    /**
    Fragment management
     */
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
