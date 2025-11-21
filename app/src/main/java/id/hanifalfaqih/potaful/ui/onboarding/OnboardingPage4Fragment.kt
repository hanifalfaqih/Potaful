package id.hanifalfaqih.potaful.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import id.hanifalfaqih.potaful.R

class OnboardingPage4Fragment : Fragment() {

    private val sharedViewModel: OnboardingSharedViewModel by activityViewModels()
    private lateinit var cvSeldom: MaterialCardView
    private lateinit var cvOften: MaterialCardView

    private var selectedCard: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_page4, container, false)

        cvSeldom = view.findViewById(R.id.cv_sometimes_travel)
        cvOften = view.findViewById(R.id.cv_often_travel)

        setupClickListeners()

        // Restore previous selection if any
        sharedViewModel.homeFrequency.value?.let { freq ->
            when (freq) {
                "Seldom" -> selectCard(cvSeldom, "Seldom")
                "Often" -> selectCard(cvOften, "Often")
            }
        }

        return view
    }

    private fun setupClickListeners() {
        cvSeldom.setOnClickListener {
            selectCard(cvSeldom, "Seldom")
        }

        cvOften.setOnClickListener {
            selectCard(cvOften, "Often")
        }
    }

    private fun selectCard(card: MaterialCardView, homeFrequency: String) {
        // Reset previous selection
        selectedCard?.isSelected = false

        // Highlight selected card
        card.isSelected = true

        selectedCard = card
        sharedViewModel.setHomeFrequency(homeFrequency)
    }
}