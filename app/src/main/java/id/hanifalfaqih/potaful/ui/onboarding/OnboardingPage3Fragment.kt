package id.hanifalfaqih.potaful.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import id.hanifalfaqih.potaful.R

class OnboardingPage3Fragment : Fragment() {

    private val sharedViewModel: OnboardingSharedViewModel by activityViewModels()
    private lateinit var cvFruit: MaterialCardView
    private lateinit var cvVegetable: MaterialCardView

    private var selectedCard: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_page3, container, false)

        cvFruit = view.findViewById(R.id.cv_fruit)
        cvVegetable = view.findViewById(R.id.cv_vegetable)

        setupClickListeners()

        // Restore previous selection if any
        sharedViewModel.preference.value?.let { pref ->
            when (pref) {
                "Fruits" -> selectCard(cvFruit, "Fruits")
                "Vegetables" -> selectCard(cvVegetable, "Vegetables")
            }
        }

        return view
    }

    private fun setupClickListeners() {
        cvFruit.setOnClickListener {
            selectCard(cvFruit, "Fruits")
        }

        cvVegetable.setOnClickListener {
            selectCard(cvVegetable, "Vegetables")
        }
    }

    private fun selectCard(card: MaterialCardView, preference: String) {
        // Reset previous selection
        selectedCard?.isSelected = false

        // Highlight selected card
        card.isSelected = true

        selectedCard = card
        sharedViewModel.setPreference(preference)
    }
}