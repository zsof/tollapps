package hu.zsof.tollapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

fun NavController.safeNavigate(direction: NavDirections, args: Bundle? = null) {
    currentDestination?.getAction(direction.actionId)?.run {
        if (args == null) {
            navigate(direction)
        } else {
            navigate(direction.actionId, args)
        }
    }
}

fun Fragment.safeNavigate(direction: NavDirections, args: Bundle? = null) {
    if (isAdded) {
        findNavController().safeNavigate(direction, args)
    }
}
