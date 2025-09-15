package com.cslori.echojournal.echos.presentation.echos

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cslori.echojournal.echos.presentation.echos.fakes.TestAudioPlayer
import com.cslori.echojournal.echos.presentation.echos.fakes.TestEchoDataSource
import com.cslori.echojournal.echos.presentation.echos.fakes.TestVoiceRecorder
import com.cslori.echojournal.echos.presentation.echos.models.AudioCaptureMethod
import com.cslori.echojournal.echos.presentation.echos.models.RecordingState
import com.cslori.echojournal.echos.presentation.echos.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests for EchosViewModel that use proper coroutine testing to verify
 * asynchronous state updates and flow behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EchosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: EchosViewModel
    private lateinit var voiceRecorder: TestVoiceRecorder
    private lateinit var audioPlayer: TestAudioPlayer
    private lateinit var echoDataSource: TestEchoDataSource
    private lateinit var savedStateHandle: SavedStateHandle


    @Before
    fun setUp() {
        voiceRecorder = TestVoiceRecorder()
        audioPlayer = TestAudioPlayer()
        echoDataSource = TestEchoDataSource()
        savedStateHandle = SavedStateHandle()

        viewModel = EchosViewModel(
            voiceRecorder = voiceRecorder,
            audioPlayer = audioPlayer,
            echoDataSource = echoDataSource,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun initialStateIsCorrect() = runTest {
        val state = viewModel.state.value
        assertTrue(state.isLoading)
        assertEquals(RecordingState.NOT_RECORDING, state.recordingState)
        assertFalse(state.hasEchosRecorded)
    }

    @Test
    fun recordFabClickSetsStandardCaptureMethod() = runTest {
        viewModel.state.test {
            // Trigger both actions
            viewModel.onAction(EchosAction.RecordFabClick)
            viewModel.onAction(EchosAction.AudioPermissionGranted)

            // Wait a bit for all emissions
            advanceUntilIdle()

            // Get the most recent state
            val finalState = expectMostRecentItem()
            assertEquals(AudioCaptureMethod.STANDARD, finalState.currentCaptureMethod)
        }
    }

    @Test
    fun recordButtonLongClickStartsQuickRecording() = runTest {
        viewModel.state.test {
            // Trigger the action
            viewModel.onAction(EchosAction.RecordButtonLongClick)

            // Advance the test dispatcher to process any coroutines
            advanceUntilIdle()

            // Verify the recorder was called
            assertTrue(voiceRecorder.isStarted)

            // Verify the state update
            val finalState = expectMostRecentItem()
            assertEquals(RecordingState.QUICK_CAPTURE, finalState.recordingState)
        }
    }

    @Test
    fun pauseRecordingClickPausesRecording() = runTest {
        viewModel.state.test {
            skipItems(1) // Skip initial state

            // Start recording first
            viewModel.onAction(EchosAction.RecordButtonLongClick)

            // Consume the state after starting recording
            val recordingState = awaitItem()
            assertEquals(RecordingState.QUICK_CAPTURE, recordingState.recordingState)

            // Then pause
            viewModel.onAction(EchosAction.PauseRecordingClick)

            // Consume the state after pausing
            val pausedState = awaitItem()

            // Verify the state and recorder behavior
            assertTrue(voiceRecorder.isPaused)
            assertEquals(RecordingState.PAUSED, pausedState.recordingState)
        }
    }

    @Test
    fun cancelRecordingClickCancelsRecording() = runTest {
        // Start recording first
        viewModel.onAction(EchosAction.RecordButtonLongClick)
        advanceUntilIdle()

        // Then cancel
        viewModel.onAction(EchosAction.CancelRecordingClick)
        advanceUntilIdle()

        // Verify the state and recorder behavior
        assertTrue(voiceRecorder.isCancelled)
        val state = viewModel.state.value
        assertEquals(RecordingState.NOT_RECORDING, state.recordingState)
    }

    @Test
    fun pauseAudioClickPausesAudioPlayer() = runTest {
        viewModel.onAction(EchosAction.PauseAudioClick)
        advanceUntilIdle()

        assertTrue(audioPlayer.isPaused)
    }

    @Test
    fun stateFlowEmitsCorrectInitialValue() = runTest {
        // Test that StateFlow has the correct initial value
        val initialState = viewModel.state.value

        assertEquals(RecordingState.NOT_RECORDING, initialState.recordingState)
        assertTrue(initialState.isLoading)
        assertFalse(initialState.hasEchosRecorded)
    }

    @Test
    fun `audio permission denied should handle gracefully`() = runTest {
        viewModel.state.test {
            skipItems(1) // Skip initial state

            // Trigger record action which requests permission
            viewModel.onAction(EchosAction.RecordFabClick)

            // Should set capture method but not start recording yet
            val requestState = awaitItem()
            assertEquals(AudioCaptureMethod.STANDARD, requestState.currentCaptureMethod)
            assertEquals(RecordingState.NOT_RECORDING, requestState.recordingState)
            assertFalse(voiceRecorder.isStarted)

            // Simulate permission denial by trying to record without granting permission
            // This should not crash or cause issues
            viewModel.onAction(EchosAction.RecordButtonLongClick)

            // Even with denied permission, quick recording should still work (different permission flow)
            val afterQuickRecord = awaitItem()
            assertEquals(RecordingState.QUICK_CAPTURE, afterQuickRecord.recordingState)
            assertTrue(voiceRecorder.isStarted)

            // Test that user can cancel gracefully after permission issues
            viewModel.onAction(EchosAction.CancelRecordingClick)

            val cancelledState = awaitItem()
            assertEquals(RecordingState.NOT_RECORDING, cancelledState.recordingState)
            assertTrue(voiceRecorder.isCancelled)
        }
    }
}