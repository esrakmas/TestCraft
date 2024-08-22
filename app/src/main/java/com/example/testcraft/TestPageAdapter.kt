package com.example.testcraft

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TestPageAdapter(
    fragmentActivity: FragmentActivity,
    private var examTitleList: List<String>,
    private var lessonsMap: Map<String, List<Map<String, Any>>>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return examTitleList.size
    }

    override fun createFragment(position: Int): Fragment {
        val examTitle = examTitleList[position]
        val lessons = lessonsMap[examTitle]
        return LessonFragment.newInstance(examTitle, lessons)
    }

    // Bu yöntem adapter'ı güncellemek için kullanılır
    fun updateData(newExamTitleList: List<String>, newLessonsMap : Map<String, List<Map<String, Any>>>) {
        examTitleList = newExamTitleList
        lessonsMap = newLessonsMap
        notifyDataSetChanged()
    }
}
