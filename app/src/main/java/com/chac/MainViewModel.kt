package com.chac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chac.domain.album.embedding.usecase.StartEmbeddingIndexingUseCase
import com.chac.onboarding.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val startEmbeddingIndexingUseCase: StartEmbeddingIndexingUseCase,
) : ViewModel() {
    val startDestination: StateFlow<StartDestination> =
        onboardingRepository.isCompleted
            .map { StartDestination.PromptInput }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StartDestination.PromptInput)

    init {
        startEmbeddingIndexingUseCase()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.markCompleted()
        }
    }
}

/** 앱 시작 화면 상태 */
enum class StartDestination {
    Loading,
    Onboarding,
    PromptInput,
}
