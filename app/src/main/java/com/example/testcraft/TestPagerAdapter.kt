package com.example.testcraft

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TestPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var examTitleList: List<String>,
    private var questionsMap: Map<String, List<Map<String, Any>>>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return examTitleList.size
    }

    override fun createFragment(position: Int): Fragment {
        val examTitle = examTitleList[position]
        val questions = questionsMap[examTitle]
        return QuestionsFragment.newInstance(examTitle, questions)
    }

    // Bu yöntem adapter'ı güncellemek için kullanılır
    fun updateData(newExamTitleList: List<String>, newQuestionsMap: Map<String, List<Map<String, Any>>>) {
        examTitleList = newExamTitleList
        questionsMap = newQuestionsMap
        notifyDataSetChanged()
    }
}
