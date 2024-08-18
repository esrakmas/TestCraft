package com.example.testcraft

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class TestFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var testPagerAdapter: TestPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        val firebaseHelper = QuestionFireBaseHelper(requireActivity())
        firebaseHelper.fetchQuestionsByExamTitle { groupedQuestions ->
            val examTitleList = groupedQuestions.keys.toList()

            testPagerAdapter = TestPagerAdapter(requireActivity(), examTitleList, groupedQuestions)
            viewPager.adapter = testPagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = examTitleList[position]
            }.attach()
        }

        return view
    }
}
