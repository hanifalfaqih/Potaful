package id.hanifalfaqih.potaful.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.local.PreferenceManager

class OnboardingPage1Fragment : Fragment() {

    private lateinit var sharedViewModel: OnboardingSharedViewModel
    private lateinit var etLocation: EditText
    private var preferenceManager: PreferenceManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_page1, container, false)

        preferenceManager = PreferenceManager(requireContext())
        sharedViewModel =
            ViewModelProvider(requireActivity())[OnboardingSharedViewModel::class.java]
        etLocation = view.findViewById(R.id.et_location)

        // Restore previous value if any
        sharedViewModel.location.value?.let {
            etLocation.setText(it)
        }

        // Save location when text changes
        etLocation.addTextChangedListener {
            val loc = it.toString().trim()
            sharedViewModel.setLocation(loc)
            if (loc.isNotEmpty()) {
                preferenceManager?.saveUserLocation(loc)
            }
        }

        return view
    }
}