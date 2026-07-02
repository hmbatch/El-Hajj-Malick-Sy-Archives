package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AiState {
    object Idle : AiState
    object Loading : AiState
    data class Success(val response: ArchiveResponse) : AiState
    data class Error(val message: String) : AiState
}

sealed interface TranslationState {
    object Idle : TranslationState
    data class TranslatingNext(val targetVerseIndex: Int) : TranslationState
    data class TranslatingAll(val currentVerseIndex: Int, val totalTranslated: Int) : TranslationState
    data class Success(val message: String) : TranslationState
    data class Error(val message: String) : TranslationState
}

class ArchiveViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ArchiveDatabase.getDatabase(application)
    private val repository = ArchiveRepository(database.dao())

    // --- State Observables ---
    val bookmarks: StateFlow<List<BookmarkedVerse>> = repository.bookmarksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<PracticeSession>> = repository.sessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active States ---
    private val _selectedWork = MutableStateFlow<Work>(CorpusData.works.first())
    val selectedWork: StateFlow<Work> = _selectedWork.asStateFlow()

    private val _activeVerseIndex = MutableStateFlow<Int?>(null)
    val activeVerseIndex: StateFlow<Int?> = _activeVerseIndex.asStateFlow()

    private val _isMeditationMode = MutableStateFlow(false)
    val isMeditationMode: StateFlow<Boolean> = _isMeditationMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Playback / Audio Simulation ---
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _memorizationLoopLimit = MutableStateFlow(3) // Default repeat count
    val memorizationLoopLimit: StateFlow<Int> = _memorizationLoopLimit.asStateFlow()

    private val _currentLoopCount = MutableStateFlow(0)
    val currentLoopCount: StateFlow<Int> = _currentLoopCount.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f) // Simulated audio waveform height/position
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    // --- EHMS AI Engine Integration ---
    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private var playbackJob: Job? = null
    private var translationJob: Job? = null

    // --- Sequential Translation States ---
    private val _translationState = MutableStateFlow<TranslationState>(TranslationState.Idle)
    val translationState: StateFlow<TranslationState> = _translationState.asStateFlow()

    // --- Actions ---

    private fun loadWorkFromJsonIfAvailable(work: Work): Work {
        try {
            val fileName = "manuscripts/work_${work.index}.json"
            val filesDir = getApplication<Application>().filesDir
            val customFile = java.io.File(filesDir, fileName)
            
            val jsonString = if (customFile.exists()) {
                customFile.readText()
            } else {
                getApplication<Application>().assets.open(fileName).bufferedReader().use { it.readText() }
            }
            
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Verse::class.java)
            val adapter = moshi.adapter<List<Verse>>(listType)
            val parsedVerses = adapter.fromJson(jsonString)
            if (parsedVerses != null && parsedVerses.isNotEmpty()) {
                return work.copy(verses = parsedVerses)
            }
        } catch (e: Exception) {
            // File doesn't exist or error parsing, fallback to dummy verses
        }
        return work
    }

    fun saveWorkToInternalStorage(workIndex: Int, verses: List<Verse>) {
        try {
            val fileName = "manuscripts/work_${workIndex}.json"
            val filesDir = getApplication<Application>().filesDir
            val customFile = java.io.File(filesDir, fileName)
            customFile.parentFile?.mkdirs()
            
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Verse::class.java)
            val adapter = moshi.adapter<List<Verse>>(listType)
            val jsonString = adapter.toJson(verses)
            customFile.writeText(jsonString)
            
            refreshAvailability()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val _workAvailability = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val workAvailability: StateFlow<Map<Int, Boolean>> = _workAvailability.asStateFlow()

    fun refreshAvailability() {
        viewModelScope.launch(Dispatchers.IO) {
            val availability = mutableMapOf<Int, Boolean>()
            CorpusData.works.forEach { work ->
                val fileName = "manuscripts/work_${work.index}.json"
                val filesDir = getApplication<Application>().filesDir
                val customFile = java.io.File(filesDir, fileName)
                
                val hasJson = if (customFile.exists()) {
                    true
                } else {
                    try {
                        getApplication<Application>().assets.open(fileName).close()
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                availability[work.index] = hasJson
            }
            _workAvailability.value = availability
        }
    }

    init {
        // Load the initial work with real JSON data if available
        val initialWork = loadWorkFromJsonIfAvailable(CorpusData.works.first())
        _selectedWork.value = initialWork
        _activeVerseIndex.value = if (initialWork.verses.isNotEmpty()) initialWork.verses.first().verseIndex else null
        refreshAvailability()
    }

    fun selectWork(work: Work) {
        stopPlayback()
        val loadedWork = loadWorkFromJsonIfAvailable(work)
        _selectedWork.value = loadedWork
        _activeVerseIndex.value = if (loadedWork.verses.isNotEmpty()) loadedWork.verses.first().verseIndex else null
    }

    fun selectVerse(verseIndex: Int) {
        _activeVerseIndex.value = verseIndex
        _currentLoopCount.value = 0
    }

    fun toggleMeditationMode() {
        _isMeditationMode.value = !_isMeditationMode.value
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateLoopLimit(limit: Int) {
        _memorizationLoopLimit.value = limit.coerceIn(1, 5)
    }

    fun updateCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    // --- Playback Control ---

    fun togglePlayback() {
        if (_isPlaying.value) {
            stopPlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        val verses = _selectedWork.value.verses
        if (verses.isEmpty()) return

        _isPlaying.value = true
        if (_activeVerseIndex.value == null) {
            _activeVerseIndex.value = verses.first().verseIndex
        }
        _currentLoopCount.value = 1

        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val currentIndex = _activeVerseIndex.value ?: verses.first().verseIndex
                val currentVerse = verses.find { it.verseIndex == currentIndex } ?: verses.first()

                // Simulate reading/chanting one loop of the active verse
                val totalLoopsNeeded = _memorizationLoopLimit.value
                
                for (progressTick in 1..20) {
                    _playbackProgress.value = progressTick / 20f
                    delay(200) // Each loop takes ~4 seconds to read/chant
                }

                if (_currentLoopCount.value < totalLoopsNeeded) {
                    // Loop the same verse
                    _currentLoopCount.value++
                } else {
                    // Save practice session log to Room
                    repository.savePracticeSession(
                        workIndex = _selectedWork.value.index,
                        verseIndex = currentVerse.verseIndex,
                        loops = _currentLoopCount.value
                    )

                    // Advance to next verse
                    val nextPos = verses.indexOf(currentVerse) + 1
                    if (nextPos < verses.size) {
                        _activeVerseIndex.value = verses[nextPos].verseIndex
                        _currentLoopCount.value = 1
                    } else {
                        // End of work reached
                        stopPlayback()
                        break
                    }
                }
            }
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        _isPlaying.value = false
        _currentLoopCount.value = 0
        _playbackProgress.value = 0f
    }

    // --- Room Bookmarks Actions ---

    fun toggleBookmark(workIndex: Int, verseIndex: Int, defaultNotes: String = "") {
        viewModelScope.launch {
            val isBookmarked = isVerseBookmarked(workIndex, verseIndex)
            if (isBookmarked) {
                repository.removeBookmark(workIndex, verseIndex)
            } else {
                repository.addBookmark(workIndex, verseIndex, defaultNotes)
            }
        }
    }

    fun saveBookmarkNotes(workIndex: Int, verseIndex: Int, notes: String) {
        viewModelScope.launch {
            repository.addBookmark(workIndex, verseIndex, notes)
        }
    }

    fun isVerseBookmarked(workIndex: Int, verseIndex: Int): Boolean {
        return bookmarks.value.any { it.workIndex == workIndex && it.verseIndex == verseIndex }
    }

    fun getBookmarkNotes(workIndex: Int, verseIndex: Int): String {
        return bookmarks.value.find { it.workIndex == workIndex && it.verseIndex == verseIndex }?.userNotes ?: ""
    }

    // --- AI Engine Query Action ---

    fun queryAiEngine(promptText: String) {
        if (promptText.isBlank()) return
        _aiState.value = AiState.Loading

        viewModelScope.launch {
            val key = _customApiKey.value.trim().ifEmpty { null }
            val result = GeminiService.queryArchiveEngine(promptText, key)
            if (result != null) {
                _aiState.value = AiState.Success(result)
                
                // Automatically append newly parsed verses to our active work representation!
                // This simulates real-time parsing of El Hajj Malick Sy works.
                if (result.segmentedText.isNotEmpty()) {
                    val incomingWork = Work(
                        index = result.workMetadata.canonicalIndexNumber,
                        titleArabic = result.workMetadata.titleArabic,
                        titleTransliteration = result.workMetadata.titleTransliteration,
                        theologicalCategory = result.workMetadata.theologicalCategory,
                        description = "Analysis and parsed stanzas generated by the EHMS-Engine.",
                        verses = result.segmentedText.map { v ->
                            Verse(
                                verseIndex = v.verseIndex,
                                arabicText = v.arabicText,
                                latinTransliteration = v.latinTransliteration,
                                englishTranslation = v.englishTranslation,
                                wolofTranslationStandard = v.wolofTranslationStandard,
                                wolofalAjamiScript = v.wolofalAjamiScript ?: "",
                                memorizationLoopCount = v.memorizationLoopCount ?: 1,
                                suggestedTasbihTarget = v.suggestedTasbihTarget ?: 11
                            )
                        },
                        oralTradition = OralTraditionMetadata(
                            chantedStyleReference = result.oralTraditionMetadata?.chantedStyleReference ?: "Custom Recitation",
                            singingMelodyAirDescription = result.oralTraditionMetadata?.singingMelodyAirDescription ?: "Spoken scholarly analysis",
                            historicalContextNotes = result.oralTraditionMetadata?.historicalContextNotes ?: "Context provided by AI Engine"
                        )
                    )
                    
                    // Set as active work
                    _selectedWork.value = incomingWork
                    _activeVerseIndex.value = incomingWork.verses.firstOrNull()?.verseIndex
                }
            } else {
                _aiState.value = AiState.Error(
                    "Error querying EHMS-Engine. Please make sure a valid Google AI Studio GEMINI_API_KEY is configured."
                )
            }
        }
    }

    fun clearAiState() {
        _aiState.value = AiState.Idle
    }

    // --- Sequential Translation Actions ---

    fun translateNextStanza() {
        val work = _selectedWork.value
        val nextIndex = work.verses.size + 1
        _translationState.value = TranslationState.TranslatingNext(nextIndex)

        viewModelScope.launch {
            val key = _customApiKey.value.trim().ifEmpty { null }
            val prompt = "Generate a synchronized dataset for Verse $nextIndex of ${work.titleTransliteration}."
            val result = GeminiService.queryArchiveEngine(prompt, key)
            if (result != null && result.segmentedText.isNotEmpty()) {
                val newVerses = result.segmentedText.map { v ->
                    Verse(
                        verseIndex = nextIndex, // Ensure it's correctly indexed
                        arabicText = v.arabicText,
                        latinTransliteration = v.latinTransliteration,
                        englishTranslation = v.englishTranslation,
                        wolofTranslationStandard = v.wolofTranslationStandard,
                        wolofalAjamiScript = v.wolofalAjamiScript ?: "",
                        memorizationLoopCount = v.memorizationLoopCount ?: 1,
                        suggestedTasbihTarget = v.suggestedTasbihTarget ?: 11
                    )
                }
                
                val updatedList = work.verses + newVerses
                val updatedWork = work.copy(verses = updatedList)
                
                _selectedWork.value = updatedWork
                saveWorkToInternalStorage(work.index, updatedList)
                _translationState.value = TranslationState.Success("Stanza $nextIndex translated and added successfully!")
            } else {
                _translationState.value = TranslationState.Error("Failed to translate Stanza $nextIndex. Ensure a valid Gemini API Key is configured.")
            }
        }
    }

    fun translateAllStanzasSequentially() {
        stopSequentialTranslation()
        val work = _selectedWork.value
        _translationState.value = TranslationState.TranslatingAll(work.verses.size + 1, 0)

        translationJob = viewModelScope.launch {
            var count = 0
            val key = _customApiKey.value.trim().ifEmpty { null }
            while (true) {
                val currentWork = _selectedWork.value
                val nextIndex = currentWork.verses.size + 1
                _translationState.value = TranslationState.TranslatingAll(nextIndex, count)

                val prompt = "Generate a synchronized dataset for Verse $nextIndex of ${currentWork.titleTransliteration}."
                val result = GeminiService.queryArchiveEngine(prompt, key)
                if (result != null && result.segmentedText.isNotEmpty()) {
                    val newVerses = result.segmentedText.map { v ->
                        Verse(
                            verseIndex = nextIndex,
                            arabicText = v.arabicText,
                            latinTransliteration = v.latinTransliteration,
                            englishTranslation = v.englishTranslation,
                            wolofTranslationStandard = v.wolofTranslationStandard,
                            wolofalAjamiScript = v.wolofalAjamiScript ?: "",
                            memorizationLoopCount = v.memorizationLoopCount ?: 1,
                            suggestedTasbihTarget = v.suggestedTasbihTarget ?: 11
                        )
                    }
                    
                    val updatedList = currentWork.verses + newVerses
                    val updatedWork = currentWork.copy(verses = updatedList)
                    
                    _selectedWork.value = updatedWork
                    saveWorkToInternalStorage(currentWork.index, updatedList)
                    count++
                    
                    // Delay between sequential translations to avoid rate limits
                    delay(3000)
                } else {
                    _translationState.value = TranslationState.Error("Sequential translation stopped at Stanza $nextIndex. Ensure a valid Gemini API Key is configured.")
                    break
                }
            }
        }
    }

    fun stopSequentialTranslation() {
        translationJob?.cancel()
        _translationState.value = TranslationState.Idle
    }
}
