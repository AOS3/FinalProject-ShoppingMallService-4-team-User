package com.aladin.finalproject_shoppingmallservice_4_team.ui.notification


import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.aladin.finalproject_shoppingmallservice_4_team.ui.adapter.NotificationAdapter

class NotificationSwipeCallback(
    private val adapter: NotificationAdapter,
    private val list: MutableList<String>,
): ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeFlag(ACTION_STATE_SWIPE, START)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        list.removeAt(position)

        adapter.updateList(list)
        adapter.notifyDataSetChanged()
    }

}