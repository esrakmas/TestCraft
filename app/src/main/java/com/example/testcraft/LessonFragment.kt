package com.example.testcraft

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LessonFragment : Fragment() {

    companion object {
        private const val ARG_EXAM_TITLE = "exam_title"
        private const val ARG_LESSON_TITLE = "lesson_title"
        private const val ARG_QUESTIONS = "lessons"

        fun newInstance(examTitle: String, lessons: List<Map<String, Any>>?): LessonFragment {
            val fragment = LessonFragment()
            val args = Bundle()
            args.putString(ARG_EXAM_TITLE, examTitle)
            args.putSerializable(ARG_QUESTIONS, ArrayList(lessons))
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LessonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lesson, container, false)

        // Arguments'dan verileri al
        val examTitle = arguments?.getString(ARG_EXAM_TITLE)
        val lessons = arguments?.getSerializable(ARG_QUESTIONS) as? List<Map<String, Any>>


        // RecyclerView'i bul ve ayarla
        recyclerView = view.findViewById(R.id.recyclerViewLessons)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Adapter'i oluştur ve RecyclerView'e bağla
        adapter = LessonAdapter()
        recyclerView.adapter = adapter

        // Benzersiz lesson_title'ları adapter'a ekle
        lessons?.let { adapter.setLessons(it) }

        return view
    }

    private fun onLessonItemClicked(lessonTitle: String) {
        // Ders itemına tıklandığında TestListActivity'ye geçiş yap
        val intent = Intent(activity, TestListActivity::class.java)
        intent.putExtra("lesson_title", lessonTitle)
        startActivity(intent)
    }


}
