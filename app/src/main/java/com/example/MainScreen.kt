package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.QrCode
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.ui.theme.NaturalDarkGreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HadraBlack
import com.example.ui.theme.HadraCharcoal
import com.example.ui.theme.HadraDarkGreen
import com.example.ui.theme.HadraEmerald
import com.example.ui.theme.HadraGold
import com.example.ui.theme.HadraGoldLight
import com.example.ui.theme.HadraGray
import com.example.ui.theme.HadraIvory
import com.example.ui.theme.HadraMint
import com.example.ui.theme.HadraSilver
import com.example.ui.theme.HadraSufiOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ArchiveViewModel) {
    val selectedWork by viewModel.selectedWork.collectAsState()
    val activeVerseIndex by viewModel.activeVerseIndex.collectAsState()
    val isMeditationMode by viewModel.isMeditationMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val loopLimit by viewModel.memorizationLoopLimit.collectAsState()
    val currentLoop by viewModel.currentLoopCount.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()
    val workAvailability by viewModel.workAvailability.collectAsState()
    val translationState by viewModel.translationState.collectAsState()

    var activeTab by remember { mutableStateOf(0) }
    var showQrDialog by remember { mutableStateOf(false) }
    val tabs = listOf("17 Canonical Works", "Interactive Reader", "EHMS AI Engine", "My Daara", "System Status")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                SufiHeroBanner(onShowQr = { showQrDialog = true })
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = HadraDarkGreen,
                    contentColor = HadraGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                            color = HadraGold
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = activeTab == index,
                            onClick = { activeTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (activeTab == index) HadraGold else HadraSilver
                                )
                            },
                            modifier = Modifier.testTag("tab_$index")
                        )
                    }
                }
            }
        },
        containerColor = HadraBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(HadraBlack)
        ) {
            when (activeTab) {
                0 -> WorksTab(
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onWorkSelected = { work ->
                        viewModel.selectWork(work)
                        activeTab = 1 // Navigate to reader
                    }
                )
                1 -> ReaderTab(
                    work = selectedWork,
                    activeVerseIndex = activeVerseIndex,
                    isMeditationMode = isMeditationMode,
                    isPlaying = isPlaying,
                    loopLimit = loopLimit,
                    currentLoop = currentLoop,
                    playbackProgress = playbackProgress,
                    onVerseSelected = viewModel::selectVerse,
                    onToggleMeditation = viewModel::toggleMeditationMode,
                    onTogglePlayback = viewModel::togglePlayback,
                    onUpdateLoopLimit = viewModel::updateLoopLimit,
                    isBookmarked = { verseIndex -> viewModel.isVerseBookmarked(selectedWork.index, verseIndex) },
                    onToggleBookmark = { verseIndex -> 
                        viewModel.toggleBookmark(
                            selectedWork.index,
                            verseIndex,
                            "Studying stanza ${verseIndex} of ${selectedWork.titleTransliteration}"
                        )
                    },
                    getBookmarkNotes = { verseIndex -> viewModel.getBookmarkNotes(selectedWork.index, verseIndex) },
                    onSaveNotes = { verseIndex, notes -> viewModel.saveBookmarkNotes(selectedWork.index, verseIndex, notes) },
                    translationState = translationState,
                    onTranslateNext = viewModel::translateNextStanza,
                    onTranslateAll = viewModel::translateAllStanzasSequentially,
                    onStopTranslation = viewModel::stopSequentialTranslation
                )
                2 -> AiEngineTab(
                    aiState = aiState,
                    customApiKey = customApiKey,
                    onApiKeyChange = viewModel::updateCustomApiKey,
                    onQuery = viewModel::queryAiEngine,
                    onClear = viewModel::clearAiState,
                    onInjectWork = { work ->
                        viewModel.selectWork(work)
                        activeTab = 1 // Switch to reader to view injected work
                    }
                )
                3 -> DaaraTab(
                    bookmarks = bookmarks,
                    sessions = sessions,
                    onNavigateToBookmark = { workIndex, verseIndex ->
                        val work = CorpusData.getWorkByIndex(workIndex)
                        if (work != null) {
                            viewModel.selectWork(work)
                            viewModel.selectVerse(verseIndex)
                            activeTab = 1 // Switch to Reader
                        }
                    },
                    onShowQr = { showQrDialog = true }
                )
                4 -> SystemStatusTab(
                    workAvailability = workAvailability,
                    onRefresh = viewModel::refreshAvailability
                )
            }
        }

        if (showQrDialog) {
            QrCodeDialog(onDismiss = { showQrDialog = false })
        }
    }
}

// --- Visual Header (Sufi Canvas Drawing) ---

@Composable
fun SufiHeroBanner(onShowQr: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(HadraDarkGreen, HadraBlack)
                )
            )
    ) {
        // Draw elegant Islamic dome outlines on a beautiful Canvas background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Left dome arch
            val pathLeft = Path().apply {
                moveTo(0f, height)
                quadraticTo(width * 0.15f, height * 0.8f, width * 0.25f, height * 0.3f)
                quadraticTo(width * 0.35f, height * 0.1f, width * 0.5f, 0f)
                quadraticTo(width * 0.65f, height * 0.1f, width * 0.75f, height * 0.3f)
                quadraticTo(width * 0.85f, height * 0.8f, width, height)
                close()
            }
            drawPath(
                path = pathLeft,
                color = HadraEmerald.copy(alpha = 0.25f)
            )

            // Golden ornamental arch line
            val pathLine = Path().apply {
                moveTo(0f, height * 0.9f)
                quadraticTo(width * 0.25f, height * 0.7f, width * 0.5f, height * 0.15f)
                quadraticTo(width * 0.75f, height * 0.7f, width, height * 0.9f)
            }
            drawPath(
                path = pathLine,
                color = HadraGold.copy(alpha = 0.2f),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Spiritual Logo",
                        tint = HadraGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "EL HAJJ MALICK SY",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = HadraGoldLight
                        )
                        Text(
                            text = "Digital Archive AI Engine (EHMS)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            color = HadraSilver
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tivaouane University Academic Edition",
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    color = HadraGray
                )
            }

            // High-fidelity QR Code button to install or share the app
            IconButton(
                onClick = onShowQr,
                modifier = Modifier
                    .size(42.dp)
                    .background(HadraEmerald.copy(alpha = 0.3f), CircleShape)
                    .border(BorderStroke(1.dp, HadraGold.copy(alpha = 0.5f)), CircleShape)
                    .testTag("show_qr_button")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Show Installation QR Code",
                    tint = HadraGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- TAB 1: 17 Works List ---

@Composable
fun WorksTab(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onWorkSelected: (Work) -> Unit
) {
    val filteredWorks = remember(searchQuery) {
        CorpusData.works.filter {
            it.titleTransliteration.contains(searchQuery, ignoreCase = true) ||
                    it.titleArabic.contains(searchQuery) ||
                    it.theologicalCategory.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search the 17 Canonical Works...", color = HadraGray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = HadraGold) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_field"),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = HadraIvory,
                unfocusedTextColor = HadraIvory,
                focusedBorderColor = HadraGold,
                unfocusedBorderColor = HadraEmerald,
                focusedContainerColor = HadraCharcoal,
                unfocusedContainerColor = HadraCharcoal
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "The 17 Canonical Manuscripts",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HadraGoldLight,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(filteredWorks) { work ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWorkSelected(work) }
                        .testTag("work_item_${work.index}"),
                    colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
                    border = BorderStroke(1.dp, HadraEmerald.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Index Circle
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(HadraEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = work.index.toString(),
                                color = HadraGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = work.titleTransliteration,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HadraIvory
                                )
                                Text(
                                    text = work.titleArabic,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HadraGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = work.theologicalCategory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HadraMint
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = work.description,
                                fontSize = 12.sp,
                                color = HadraSilver,
                                maxLines = 2,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Read",
                            tint = HadraGoldLight
                        )
                    }
                }
            }
        }
    }
}

// --- TAB 2: Interactive Reader Tab ---

@Composable
fun ReaderTab(
    work: Work,
    activeVerseIndex: Int?,
    isMeditationMode: Boolean,
    isPlaying: Boolean,
    loopLimit: Int,
    currentLoop: Int,
    playbackProgress: Float,
    onVerseSelected: (Int) -> Unit,
    onToggleMeditation: () -> Unit,
    onTogglePlayback: () -> Unit,
    onUpdateLoopLimit: (Int) -> Unit,
    isBookmarked: (Int) -> Boolean,
    onToggleBookmark: (Int) -> Unit,
    getBookmarkNotes: (Int) -> String,
    onSaveNotes: (Int, String) -> Unit,
    translationState: TranslationState = TranslationState.Idle,
    onTranslateNext: () -> Unit = {},
    onTranslateAll: () -> Unit = {},
    onStopTranslation: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    var autoScrollEnabled by remember { mutableStateOf(true) }

    // Scroll active verse into view dynamically
    LaunchedEffect(activeVerseIndex, autoScrollEnabled) {
        if (autoScrollEnabled && activeVerseIndex != null) {
            val index = work.verses.indexOfFirst { it.verseIndex == activeVerseIndex }
            if (index >= 0) {
                listState.animateScrollToItem(index)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Work Header details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WORK #${work.index}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HadraMint,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = work.titleTransliteration,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HadraGold
                        )
                    }
                    Text(
                        text = work.titleArabic,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HadraIvory
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = work.theologicalCategory,
                    fontSize = 12.sp,
                    color = HadraSilver,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = work.oralTradition.chantedStyleReference + " • " + work.oralTradition.singingMelodyAirDescription,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    color = HadraGray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reader Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Meditation Mode Toggle
                Button(
                    onClick = onToggleMeditation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMeditationMode) HadraSufiOrange else HadraEmerald
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("meditation_toggle")
                ) {
                    Icon(
                        imageVector = if (isMeditationMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Meditation",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMeditationMode) "Meditation Active" else "Meditation Mode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HadraIvory
                    )
                }

                // Auto-Scroll Toggle
                Button(
                    onClick = { autoScrollEnabled = !autoScrollEnabled },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (autoScrollEnabled) HadraGold.copy(alpha = 0.8f) else HadraCharcoal
                    ),
                    border = BorderStroke(1.dp, HadraGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("autoscroll_toggle")
                ) {
                    Icon(
                        imageVector = if (autoScrollEnabled) Icons.Default.Timer else Icons.Default.Loop,
                        contentDescription = "Auto-Scroll",
                        tint = if (autoScrollEnabled) HadraBlack else HadraGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (autoScrollEnabled) "Auto-Scroll" else "Free Scroll",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (autoScrollEnabled) HadraBlack else HadraIvory
                    )
                }
            }

            // Playback Actions (Memorization Loop Controls)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop counter selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(HadraCharcoal, RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Loop,
                        contentDescription = "Loops",
                        tint = HadraGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Repeat: ${loopLimit}x",
                        fontSize = 11.sp,
                        color = HadraIvory,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { onUpdateLoopLimit(if (loopLimit >= 5) 1 else loopLimit + 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Cycle loops",
                            tint = HadraGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play / Pause FAB
                IconButton(
                    onClick = onTogglePlayback,
                    modifier = Modifier
                        .size(38.dp)
                        .background(HadraGold, CircleShape)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play Control",
                        tint = HadraBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Play progress indicator
        if (isPlaying) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = HadraEmerald.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Loop,
                            contentDescription = "Loop countdown",
                            tint = HadraGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chanting Loop $currentLoop of $loopLimit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HadraGoldLight
                        )
                    }
                    // Simple simulated wave lines
                    Row {
                        repeat(5) { wave ->
                            val heightFactor = if (wave % 2 == 0) playbackProgress else (1f - playbackProgress)
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 1.5.dp)
                                    .width(3.dp)
                                    .height((8.dp.value * heightFactor).dp.coerceIn(3.dp, 12.dp))
                                    .background(HadraGold, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Empty Stanzas Notice
        if (work.verses.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Empty",
                        tint = HadraGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Manuscript Text Unloaded",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = HadraIvory
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To generate or retrieve synchronized multi-layered verses, use the EHMS AI Engine tab to query our Tivaouane scholarly dataset.",
                        fontSize = 12.sp,
                        color = HadraSilver,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Verses list
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(work.verses) { verse ->
                    val isActive = activeVerseIndex == verse.verseIndex
                    VerseCard(
                        verse = verse,
                        isActive = isActive,
                        isMeditationMode = isMeditationMode,
                        isBookmarked = isBookmarked(verse.verseIndex),
                        onSelect = { onVerseSelected(verse.verseIndex) },
                        onToggleBookmark = { onToggleBookmark(verse.verseIndex) },
                        bookmarkNotes = getBookmarkNotes(verse.verseIndex),
                        onSaveNotes = { notes -> onSaveNotes(verse.verseIndex, notes) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AiTranslationControlsPanel(
                        translationState = translationState,
                        onTranslateNext = onTranslateNext,
                        onTranslateAll = onTranslateAll,
                        onStopTranslation = onStopTranslation
                    )
                }
            }
        }
    }
}

@Composable
fun AiTranslationControlsPanel(
    translationState: TranslationState,
    onTranslateNext: () -> Unit,
    onTranslateAll: () -> Unit,
    onStopTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_translation_panel"),
        colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, HadraGold.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = HadraGold,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "AI Sequential Translator (EHMS-Engine)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HadraIvory
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Each manuscript contains dozens to hundreds of stanzas. You can use the EHMS-Engine to translate and append further stanzas dynamically, one-by-one.",
                fontSize = 12.sp,
                color = HadraSilver,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            when (translationState) {
                is TranslationState.Idle -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onTranslateNext,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("translate_next_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = HadraGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Translate Next",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraBlack
                            )
                        }
                        
                        Button(
                            onClick = onTranslateAll,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("translate_all_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = HadraEmerald),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Translate All (Seq)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraIvory
                            )
                        }
                    }
                }
                is TranslationState.TranslatingNext -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = HadraGold,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI translating Stanza ${translationState.targetVerseIndex}... Please wait.",
                            fontSize = 12.sp,
                            color = HadraGoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                is TranslationState.TranslatingAll -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = HadraEmerald,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Translating Stanza ${translationState.currentVerseIndex}... (${translationState.totalTranslated} translated this session)",
                            fontSize = 12.sp,
                            color = HadraMint,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onStopTranslation,
                            colors = ButtonDefaults.buttonColors(containerColor = HadraSufiOrange),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("stop_translation_button")
                        ) {
                            Text("Stop Translation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is TranslationState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HadraEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = HadraMint,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = translationState.message,
                                fontSize = 12.sp,
                                color = HadraMint,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onTranslateNext,
                                colors = ButtonDefaults.buttonColors(containerColor = HadraGold),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Translate Next", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HadraBlack)
                            }
                            Button(
                                onClick = onTranslateAll,
                                colors = ButtonDefaults.buttonColors(containerColor = HadraEmerald),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Translate All (Seq)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HadraIvory)
                            }
                        }
                    }
                }
                is TranslationState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HadraSufiOrange.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = HadraSufiOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = translationState.message,
                                fontSize = 12.sp,
                                color = HadraSufiOrange,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onTranslateNext,
                            colors = ButtonDefaults.buttonColors(containerColor = HadraGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HadraBlack)
                        }
                    }
                }
            }
        }
    }
}

// --- Card representing an individual Verse ---

@Composable
fun VerseCard(
    verse: Verse,
    isActive: Boolean,
    isMeditationMode: Boolean,
    isBookmarked: Boolean,
    onSelect: () -> Unit,
    onToggleBookmark: () -> Unit,
    bookmarkNotes: String,
    onSaveNotes: (String) -> Unit
) {
    var showNotesEditor by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(bookmarkNotes) }

    val activeBorderColor = if (isActive) HadraGold else HadraEmerald.copy(alpha = 0.3f)
    val activeBgColor = if (isActive) HadraEmerald.copy(alpha = 0.25f) else HadraCharcoal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("verse_item_${verse.verseIndex}"),
        colors = CardDefaults.cardColors(containerColor = activeBgColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isActive) 2.dp else 1.dp, activeBorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Index and Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(if (isActive) HadraGold else HadraEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = verse.verseIndex.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HadraBlack
                        )
                    }
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE VIEWPORT S(t)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HadraGoldLight,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isBookmarked) {
                        IconButton(
                            onClick = { showNotesEditor = !showNotesEditor },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit notes",
                                tint = HadraMint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) HadraGold else HadraGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Classical Arabic Layer (Beautiful font and size, styled prominently)
            Text(
                text = verse.arabicText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = HadraIvory,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                lineHeight = 36.sp
            )

            // Dynamic visibility of translations based on Meditation Mode!
            AnimatedVisibility(
                visible = !isMeditationMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Latin Transliteration Layer
                    Text(
                        text = verse.latinTransliteration,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = HadraGoldLight,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. English Translation Layer
                    Text(
                        text = verse.englishTranslation,
                        fontSize = 12.sp,
                        color = HadraSilver,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Wolof Standard Translation & Wolofal Ajami
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HadraBlack.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Wolof: " + verse.wolofTranslationStandard,
                            fontSize = 12.sp,
                            color = HadraIvory,
                            lineHeight = 16.sp
                        )
                        if (verse.wolofalAjamiScript.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Wolofal (Ajami): " + verse.wolofalAjamiScript,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraMint,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Notes Editor expanding block
            if (isBookmarked && (showNotesEditor || bookmarkNotes.isNotEmpty())) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HadraEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "My Scholarly Reflections / Notes:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HadraMint
                    )
                    if (showNotesEditor) {
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = { Text("Enter reflections...", color = HadraGray, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            textStyle = TextStyle(color = HadraIvory, fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HadraGold,
                                unfocusedBorderColor = HadraEmerald
                            )
                        )
                        Button(
                            onClick = {
                                onSaveNotes(notesText)
                                showNotesEditor = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HadraGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Save Reflections", color = HadraBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = bookmarkNotes.ifEmpty { "Click edit icon to write reflections." },
                            fontSize = 12.sp,
                            color = HadraIvory,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- TAB 3: AI Engine Scholar Query ---

@Composable
fun AiEngineTab(
    aiState: AiState,
    customApiKey: String,
    onApiKeyChange: (String) -> Unit,
    onQuery: (String) -> Unit,
    onClear: () -> Unit,
    onInjectWork: (Work) -> Unit
) {
    var queryInput by remember { mutableStateOf("") }
    var showKeyConfig by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HadraGold.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Engine",
                                tint = HadraGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EHMS AI Core Engine Config",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraIvory
                            )
                        }
                        IconButton(onClick = { showKeyConfig = !showKeyConfig }) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "API Keys",
                                tint = if (customApiKey.isNotEmpty()) HadraGold else HadraGray
                            )
                        }
                    }

                    Text(
                        text = "This parser connects to Google AI Studio's Gemini 3.5 Pro model running at 0.1 Temperature for orthographic accuracy and structured JSON generation of Senegalese Ajami and Classical Arabic, with precise metaphysical translations.",
                        fontSize = 11.sp,
                        color = HadraSilver,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    AnimatedVisibility(visible = showKeyConfig) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            OutlinedTextField(
                                value = customApiKey,
                                onValueChange = onApiKeyChange,
                                placeholder = { Text("Override default GEMINI_API_KEY", color = HadraGray, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(color = HadraIvory, fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HadraGold,
                                    unfocusedBorderColor = HadraEmerald
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Text(
                                text = "By default, the app uses the secure system-level API key injected from AI Studio Secrets.",
                                fontSize = 10.sp,
                                color = HadraMint,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = queryInput,
                onValueChange = { queryInput = it },
                placeholder = { Text("Ask EHMS-Engine (e.g. 'Generate a synchronized dataset for Verse 1 of Taysir, Wassilatul muna' or 'Analyze a passage from Faakihatul Tullab')", color = HadraGray, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("ai_query_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = HadraIvory,
                    unfocusedTextColor = HadraIvory,
                    focusedBorderColor = HadraGold,
                    unfocusedBorderColor = HadraEmerald,
                    focusedContainerColor = HadraCharcoal,
                    unfocusedContainerColor = HadraCharcoal
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        queryInput = ""
                        onClear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HadraCharcoal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Clear Query", color = HadraSilver, fontSize = 12.sp)
                }

                Button(
                    onClick = { onQuery(queryInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = HadraGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("ai_submit_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Query", tint = HadraBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Query EHMS Engine", color = HadraBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SCHOLARLY SCHEMATIC RESPONSE:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HadraMint
            )
        }

        // Response section
        item {
            when (aiState) {
                is AiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(HadraCharcoal, RoundedCornerShape(12.dp))
                            .border(1.dp, HadraEmerald.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Awaiting scholarly prompt...",
                            fontSize = 13.sp,
                            color = HadraGray,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                is AiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(HadraCharcoal, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Loop,
                                contentDescription = "Loading",
                                tint = HadraGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Querying Tivaouane Hadra Archives...",
                                fontSize = 12.sp,
                                color = HadraGoldLight
                            )
                        }
                    }
                }
                is AiState.Success -> {
                    val response = aiState.response
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HadraCharcoal, RoundedCornerShape(12.dp))
                            .border(1.dp, HadraGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = response.workMetadata.titleTransliteration,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraGold
                            )
                            Text(
                                text = response.workMetadata.titleArabic,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraIvory
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Theological Category: ${response.workMetadata.theologicalCategory}",
                            fontSize = 12.sp,
                            color = HadraMint
                        )
                        if (response.oralTraditionMetadata != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Oral Context: ${response.oralTraditionMetadata.historicalContextNotes}",
                                fontSize = 11.sp,
                                color = HadraSilver,
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Inject Button
                        Button(
                            onClick = {
                                val work = Work(
                                    index = response.workMetadata.canonicalIndexNumber,
                                    titleArabic = response.workMetadata.titleArabic,
                                    titleTransliteration = response.workMetadata.titleTransliteration,
                                    theologicalCategory = response.workMetadata.theologicalCategory,
                                    description = "Scholarly parsed stanzas generated by the EHMS-Engine.",
                                    verses = response.segmentedText.map { v ->
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
                                        chantedStyleReference = response.oralTraditionMetadata?.chantedStyleReference ?: "Custom Recitation",
                                        singingMelodyAirDescription = response.oralTraditionMetadata?.singingMelodyAirDescription ?: "Spoken scholarly analysis",
                                        historicalContextNotes = response.oralTraditionMetadata?.historicalContextNotes ?: "Context provided by AI Engine"
                                    )
                                )
                                onInjectWork(work)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HadraEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Load", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open & Read in Interactive Viewer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                is AiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HadraCharcoal, RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "API Query Unsuccessful",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = aiState.message,
                            fontSize = 12.sp,
                            color = HadraSilver,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// --- TAB 4: My Daara (Bookmarks & Practice Sessions) ---

@Composable
fun DaaraTab(
    bookmarks: List<BookmarkedVerse>,
    sessions: List<PracticeSession>,
    onNavigateToBookmark: (Int, Int) -> Unit,
    onShowQr: () -> Unit
) {
    var activeSubTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Installation & Sharing Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .clickable { onShowQr() }
                .testTag("daara_share_banner"),
            colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, HadraEmerald.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(HadraEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Install App",
                            tint = HadraGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Install Archive on Mobile",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HadraIvory
                        )
                        Text(
                            text = "Scan QR Code to install or share with study groups",
                            fontSize = 11.sp,
                            color = HadraSilver
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Open",
                    tint = HadraGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        // Mini sub tab
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HadraCharcoal, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == 0) HadraEmerald else Color.Transparent)
                    .clickable { activeSubTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Bookmarks",
                        tint = if (activeSubTab == 0) HadraGold else HadraSilver,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "My Reflecions (${bookmarks.size})",
                        color = if (activeSubTab == 0) HadraIvory else HadraSilver,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == 1) HadraEmerald else Color.Transparent)
                    .clickable { activeSubTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Practice History",
                        tint = if (activeSubTab == 1) HadraGold else HadraSilver,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daara Practice Log (${sessions.size})",
                        color = if (activeSubTab == 1) HadraIvory else HadraSilver,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSubTab == 0) {
            // Bookmarks / Reflections List
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = "Empty Bookmarks",
                            tint = HadraGray,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No reflections saved yet.",
                            fontSize = 13.sp,
                            color = HadraSilver
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bookmarks) { bookmark ->
                        val work = CorpusData.getWorkByIndex(bookmark.workIndex)
                        val verse = work?.verses?.find { it.verseIndex == bookmark.verseIndex }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToBookmark(bookmark.workIndex, bookmark.verseIndex) }
                                .testTag("bookmark_item_${bookmark.workIndex}_${bookmark.verseIndex}"),
                            colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, HadraEmerald.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = work?.titleTransliteration ?: "Unknown Manuscript",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HadraGold
                                    )
                                    Text(
                                        text = "Stanza ${bookmark.verseIndex}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HadraMint
                                    )
                                }
                                if (verse != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = verse.arabicText,
                                        fontSize = 16.sp,
                                        color = HadraIvory,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(HadraBlack.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = bookmark.userNotes.ifEmpty { "Saved study reference." },
                                        fontSize = 12.sp,
                                        color = HadraSilver,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Practice Sessions List
            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Empty History",
                            tint = HadraGray,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start practicing chanting with our loop controller!",
                            fontSize = 13.sp,
                            color = HadraSilver
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(sessions) { session ->
                        val work = CorpusData.getWorkByIndex(session.workIndex)
                        val dateString = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(session.timestamp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = work?.titleTransliteration ?: "Manuscript #${session.workIndex}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HadraIvory
                                    )
                                    Text(
                                        text = "Stanza ${session.verseIndex} • Loops Completed: ${session.completedLoops}",
                                        fontSize = 11.sp,
                                        color = HadraSilver
                                    )
                                }
                                Text(
                                    text = dateString,
                                    fontSize = 10.sp,
                                    color = HadraGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrCodeDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    // Default shared URL from AIS Metadata
    val defaultUrl = "https://ais-pre-hom7q7vcnc6lgthrhtieu4-313118594216.us-west2.run.app"
    var qrContent by remember { mutableStateOf(defaultUrl) }

    // Generate the QR bitmap dynamically using our QrCodeGenerator helper
    val qrBitmap = remember(qrContent) {
        QrCodeGenerator.generate(qrContent, size = 512)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("qr_dialog_container"),
            colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, HadraEmerald.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Install App",
                            tint = HadraGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Install & Share App",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = HadraIvory
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = HadraSilver
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Scan this QR Code with your phone's camera to install or run the archive app instantly on your mobile device.",
                    fontSize = 11.sp,
                    color = HadraSilver,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // QR Code Frame with nice white background for high contrast scanning
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Install QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error generating QR",
                                tint = Color.Red,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Failed to generate QR Code",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable text field showing/allowing custom URL
                OutlinedTextField(
                    value = qrContent,
                    onValueChange = { qrContent = it },
                    label = { Text("App Install URL", color = HadraGold, fontSize = 11.sp) },
                    textStyle = TextStyle(fontSize = 11.sp, color = HadraIvory),
                    modifier = Modifier.fillMaxWidth().testTag("qr_url_input"),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HadraIvory,
                        unfocusedTextColor = HadraIvory,
                        focusedBorderColor = HadraGold,
                        unfocusedBorderColor = HadraEmerald,
                        focusedContainerColor = HadraBlack.copy(alpha = 0.4f),
                        unfocusedContainerColor = HadraBlack.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Copy Link & System Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Share Link", qrContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).testTag("qr_copy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = HadraEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Link",
                                tint = HadraGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", color = HadraIvory, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "El Hajj Malick Sy Digital Archive")
                                putExtra(Intent.EXTRA_TEXT, "Access the El Hajj Malick Sy Digital Archive app: $qrContent")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share App Link"))
                        },
                        modifier = Modifier.weight(1f).testTag("qr_share_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = HadraGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = NaturalDarkGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = NaturalDarkGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemStatusTab(
    workAvailability: Map<Int, Boolean>,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "System Health & Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HadraGold
                )
                Text(
                    text = "Background Translation & Transliteration Monitor",
                    fontSize = 12.sp,
                    color = HadraSilver
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Status",
                    tint = HadraGold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overall progress
        val totalWorks = CorpusData.works.size
        val completedWorks = workAvailability.count { it.value }
        val progress = if (totalWorks > 0) completedWorks.toFloat() / totalWorks else 0f

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HadraCharcoal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Translation Progress: $completedWorks / $totalWorks Manuscripts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HadraIvory
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = HadraEmerald,
                    trackColor = HadraBlack
                )
                if (progress < 1f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = HadraSufiOrange,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Background translation in progress...",
                            fontSize = 12.sp,
                            color = HadraSufiOrange,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(CorpusData.works) { work ->
                val isAvailable = workAvailability[work.index] ?: false
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HadraBlack),
                    border = BorderStroke(1.dp, if (isAvailable) HadraEmerald.copy(alpha = 0.5f) else HadraCharcoal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WORK #${work.index}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = work.titleTransliteration,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HadraIvory
                            )
                        }
                        
                        if (isAvailable) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = HadraEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ready",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HadraEmerald
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Pending,
                                    contentDescription = "Pending",
                                    tint = HadraSilver,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pending",
                                    fontSize = 12.sp,
                                    color = HadraSilver
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


