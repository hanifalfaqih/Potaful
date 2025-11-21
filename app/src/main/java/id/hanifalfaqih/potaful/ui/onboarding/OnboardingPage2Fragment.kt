package id.hanifalfaqih.potaful.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import id.hanifalfaqih.potaful.R

class OnboardingPage2Fragment : Fragment() {

    private val sharedViewModel: OnboardingSharedViewModel by activityViewModels()
    private lateinit var cvBeginner: MaterialCardView
    private lateinit var cvIntermediate: MaterialCardView
    private lateinit var cvProfessional: MaterialCardView

    private var selectedCard: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_page2, container, false)

        cvBeginner = view.findViewById(R.id.cv_beginner)
        cvIntermediate = view.findViewById(R.id.cv_slightly_experienced)
        cvProfessional = view.findViewById(R.id.cv_experienced)

        setupClickListeners()

        // Restore previous selection if any
        sharedViewModel.skillLevel.value?.let { level ->
            when (level) {
                "Beginner" -> selectCard(cvBeginner, "Beginner")
                "Intermediate" -> selectCard(cvIntermediate, "Intermediate")
                "Professional" -> selectCard(cvProfessional, "Professional")
            }
        }

        return view
    }

    private fun setupClickListeners() {
        cvBeginner.setOnClickListener {
            selectCard(cvBeginner, "Beginner")
        }

        cvIntermediate.setOnClickListener {
            selectCard(cvIntermediate, "Intermediate")
        }

        cvProfessional.setOnClickListener {
            selectCard(cvProfessional, "Professional")
        }
    }

    private fun selectCard(card: MaterialCardView, skillLevel: String) {
        // Reset previous selection
        selectedCard?.isSelected = false

        // Highlight selected card
        card.isSelected = true

        selectedCard = card
        sharedViewModel.setSkillLevel(skillLevel)
    }
}