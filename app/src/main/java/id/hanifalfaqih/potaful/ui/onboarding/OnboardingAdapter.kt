package id.hanifalfaqih.potaful.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPage1Fragment()
            1 -> OnboardingPage2Fragment()
            2 -> OnboardingPage3Fragment()
            3 -> OnboardingPage4Fragment()
            else -> OnboardingPage1Fragment()
        }
    }
}