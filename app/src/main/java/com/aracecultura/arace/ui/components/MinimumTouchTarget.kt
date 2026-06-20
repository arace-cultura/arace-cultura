package com.aracecultura.arace.ui.components

import android.graphics.Rect
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

private const val MINIMUM_TOUCH_TARGET_DP = 44

fun View.ensureMinimumTouchTargets() {
    if (this is ViewGroup) {
        for (index in 0 until childCount) {
            getChildAt(index).ensureMinimumTouchTargets()
        }
    }

    if (isClickable) {
        expandTouchTarget()
    }
}

private fun View.expandTouchTarget() {
    val parentView = parent as? View ?: return

    parentView.post {
        if (!isAttachedToWindow || width == 0 || height == 0) return@post

        val density = resources.displayMetrics.density
        val minimumSize = (MINIMUM_TOUCH_TARGET_DP * density).toInt()
        val horizontalExpansion = max(0, minimumSize - width)
        val verticalExpansion = max(0, minimumSize - height)

        if (horizontalExpansion == 0 && verticalExpansion == 0) return@post

        val bounds = Rect()
        getHitRect(bounds)
        bounds.left -= horizontalExpansion / 2
        bounds.right += horizontalExpansion - horizontalExpansion / 2
        bounds.top -= verticalExpansion / 2
        bounds.bottom += verticalExpansion - verticalExpansion / 2

        val delegate = TouchDelegate(bounds, this)
        val currentDelegate = parentView.touchDelegate
        val delegateGroup = when (currentDelegate) {
            is TouchDelegateGroup -> currentDelegate
            null -> TouchDelegateGroup(parentView)
            else -> TouchDelegateGroup(parentView).apply { add(currentDelegate) }
        }

        delegateGroup.add(delegate)
        parentView.touchDelegate = delegateGroup
    }
}

private class TouchDelegateGroup(delegateView: View) :
    TouchDelegate(Rect(), delegateView) {

    private val delegates = mutableListOf<TouchDelegate>()

    fun add(delegate: TouchDelegate) {
        delegates += delegate
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return delegates.any { it.onTouchEvent(event) }
    }
}
