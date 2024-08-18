package com.example.testcraft

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class QuestionsFragment : Fragment() {

    companion object {
        private const val ARG_EXAM_TITLE = "exam_title"
        private const val ARG_QUESTIONS = "questions"

        fun newInstance(examTitle: String, questions: List<Map<String, Any>>?): QuestionsFragment {
            val fragment = QuestionsFragment()
            val args = Bundle()
            args.putString(ARG_EXAM_TITLE, examTitle)
            args.putSerializable(ARG_QUESTIONS, ArrayList(questions))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_questions, container, false)

        // Arguments'dan verileri al
        val examTitle = arguments?.getString(ARG_EXAM_TITLE)
        val questions = arguments?.getSerializable(ARG_QUESTIONS) as? List<Map<String, Any>>

        // Burada questions RecyclerView'e bağlanabilir.

        return view
    }
}
