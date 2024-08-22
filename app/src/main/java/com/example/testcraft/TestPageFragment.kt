package com.example.testcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class TestPageFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var testPageAdapter: TestPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_page, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        // activity nesnesinin null olup olmadığını kontrol edin
        activity?.let { activity ->
            val firebaseHelper = QuestionFireBaseHelper(activity)
            firebaseHelper.fetchGroupsByExamTitle   { groupedLessons  ->


                /*
                Anahtarları Listeye Çevirme:
                groupedLessons.keys ifadesi, groupedLessons'ın tüm anahtarlarını (exam_title değerlerini) alır.
                toList() metodu ise bu anahtarları bir List (liste) haline getirir
                */
                val examTitleList = groupedLessons .keys.toList()

                testPageAdapter = TestPageAdapter(activity, examTitleList, groupedLessons )
                viewPager.adapter = testPageAdapter

                // { tab, position -> ... }: Bu lambda fonksiyonu,
                // her bir sekme (tab) için başlık metnini (examTitleList[position]) ayarlar.
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = examTitleList[position]
                }.attach()
                testPageAdapter.updateData(examTitleList, groupedLessons)

            }
        } ?: run {
            Log.e("TestPage", "questions boş")
        }

        return view
    }


}


