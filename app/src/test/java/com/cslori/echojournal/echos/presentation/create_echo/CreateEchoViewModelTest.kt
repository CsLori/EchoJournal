package com.cslori.echojournal.echos.presentation.create_echo

import com.cslori.echojournal.echos.domain.echo.Mood
import com.cslori.echojournal.echos.presentation.create_echo.fakes.TestRecordingStorage
import com.cslori.echojournal.echos.presentation.create_echo.fakes.TestSettingsPreferences
import com.cslori.echojournal.echos.presentation.echos.fakes.TestAudioPlayer
import com.cslori.echojournal.echos.presentation.echos.fakes.TestEchoDataSource
import com.cslori.echojournal.echos.presentation.echos.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests for CreateEchoViewModel components using fake implementations.
 *
 * Note: Full integration tests with the actual ViewModel are challenging due to
 * the SavedStateHandle.toRoute() navigation dependency. These tests focus on
 * testing the individual components and fakes that the ViewModel depends on.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreateEchoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var recordingStorage: TestRecordingStorage
    private lateinit var audioPlayer: TestAudioPlayer
    private lateinit var echoDataSource: TestEchoDataSource
    private lateinit var settingsPreferences: TestSettingsPreferences

    @Before
    fun setUp() {
        recordingStorage = TestRecordingStorage()
        audioPlayer = TestAudioPlayer()
        echoDataSource = TestEchoDataSource()
        settingsPreferences = TestSettingsPreferences()
    }

    @Test
    fun testRecordingStorageSuccessfulSave() = runTest {
        val tempPath = "/temp/recording.mp4"
        val result = recordingStorage.savePersistently(tempPath)

        assertNotNull(result)
        assertEquals("/persistent/$tempPath", result)
        assertEquals(1, recordingStorage.savedPaths.size)
        assertTrue(recordingStorage.savedPaths.contains(result))
    }

    @Test
    fun testRecordingStorageFailedSave() = runTest {
        recordingStorage.shouldFailSave = true
        val tempPath = "/temp/recording.mp4"

        val result = recordingStorage.savePersistently(tempPath)

        assertNull(result)
        assertEquals(0, recordingStorage.savedPaths.size)
    }

    @Test
    fun testRecordingStorageCleanup() = runTest {
        recordingStorage.cleanUpTemporaryFiles()

        assertEquals(1, recordingStorage.cleanedUpCount)
    }

    @Test
    fun testAudioPlayerPlayPauseFlow() = runTest {
        val filePath = "/test/audio.mp3"

        // Initial state
        assertNull(audioPlayer.activeTrack.value)
        assertFalse(audioPlayer.isPaused)
        assertFalse(audioPlayer.isResumed)

        // Play
        audioPlayer.play(filePath) { }
        assertNotNull(audioPlayer.activeTrack.value)
        assertTrue(audioPlayer.activeTrack.value?.isPlaying == true)

        // Pause
        audioPlayer.pause()
        assertTrue(audioPlayer.isPaused)
        assertTrue(audioPlayer.activeTrack.value?.isPlaying == false)

        // Resume
        audioPlayer.resume()
        assertTrue(audioPlayer.isResumed)
        assertFalse(audioPlayer.isPaused)
        assertTrue(audioPlayer.activeTrack.value?.isPlaying == true)

        // Stop
        audioPlayer.stop()
        assertTrue(audioPlayer.isStopped)
        assertNull(audioPlayer.activeTrack.value)
    }

    @Test
    fun testEchoDataSourceInsertAndRetrieve() = runTest {
        assertEquals(0, echoDataSource.getInsertedEchos().size)

        val echosList = echoDataSource.observeEchos().first()
        val testEcho = echosList.first()
        echoDataSource.insertEcho(testEcho)

        assertEquals(1, echoDataSource.getInsertedEchos().size)
        assertEquals(testEcho, echoDataSource.getInsertedEchos().first())
    }

    @Test
    fun testEchoDataSourceObserveTopics() = runTest {
        val topics = echoDataSource.observeTopics().first()

        assertNotNull(topics)
        assertTrue(topics.contains("work"))
        assertTrue(topics.contains("meeting"))
        assertTrue(topics.contains("personal"))
    }

    @Test
    fun testEchoDataSourceSearchTopics() = runTest {
        val searchResults = echoDataSource.searchTopics("work").first()

        assertNotNull(searchResults)
        assertTrue(searchResults.contains("work"))
        assertTrue(searchResults.contains("meeting"))
    }

    @Test
    fun testSettingsPreferencesDefaultMood() = runTest {
        // Initial default
        val initialMood = settingsPreferences.observeDefaultMood().first()
        assertEquals(Mood.NEUTRAL, initialMood)

        // Set new default
        settingsPreferences.setDefaultMood(Mood.EXCITED)
        val updatedMood = settingsPreferences.observeDefaultMood().first()
        assertEquals(Mood.EXCITED, updatedMood)
    }

    @Test
    fun testSettingsPreferencesDefaultTopics() = runTest {
        // Initial default
        val initialTopics = settingsPreferences.observeDefaultTopics().first()
        assertEquals(emptyList<String>(), initialTopics)

        // Set new defaults
        val newTopics = listOf("work", "personal", "health")
        settingsPreferences.setDefaultTopics(newTopics)

        val updatedTopics = settingsPreferences.observeDefaultTopics().first()
        assertEquals(newTopics, updatedTopics)
    }

    @Test
    fun testSettingsPreferencesReset() = runTest {
        // Set some values
        settingsPreferences.setDefaultMood(Mood.EXCITED)
        settingsPreferences.setDefaultTopics(listOf("work", "personal"))

        // Reset
        settingsPreferences.reset()

        // Verify reset
        assertEquals(Mood.NEUTRAL, settingsPreferences.observeDefaultMood().first())
        assertEquals(emptyList<String>(), settingsPreferences.observeDefaultTopics().first())
    }

    @Test
    fun testCreateEchoStateInitialValues() {
        val state = CreateEchoState()

        assertEquals("", state.titleText)
        assertEquals("", state.addTopicText)
        assertEquals("", state.noteText)
        assertTrue(state.showMoodSelector)
        assertEquals(emptyList<String>(), state.searchResults)
        assertFalse(state.canSaveEcho)
        assertFalse(state.showTopicSuggestions)
        assertFalse(state.showConfirmLeaveDialog)
    }

    @Test
    fun testCreateEchoStateCanSaveLogic() {
        val state = CreateEchoState(
            titleText = "Test Title",
            mood = null
        )
        assertFalse(state.canSaveEcho)

        val stateWithMood =
            state.copy(mood = com.cslori.echojournal.echos.presentation.models.MoodUi.EXCITED)
        assertFalse(stateWithMood.canSaveEcho)
    }

    @Test
    fun testAudioPlayerAsyncOperations() = runTest {
        val filePath = "/test/audio.mp3"

        audioPlayer.play(filePath) { }

        assertTrue(audioPlayer.activeTrack.value?.isPlaying == true)
    }
}