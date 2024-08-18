package com.example.testcraft

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TestPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val examTitleList: List<String>,
    private val questionsMap: Map<String, List<Map<String, Any>>>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return examTitleList.size
    }

    override fun createFragment(position: Int): Fragment {
        val examTitle = examTitleList[position]
        val questions = questionsMap[examTitle]

        // ExamTitle'a göre sorularla dolu bir fragment döndür.
        return QuestionsFragment.newInstance(examTitle, questions)
    }
}
