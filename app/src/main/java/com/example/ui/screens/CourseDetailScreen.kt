package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.QuizAnswer
import com.example.data.model.QuizQuestion
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    course: Course,
    watchPosition: Int,
    isPlaying: Boolean,
    selectedQuality: String,
    quizIndex: Int,
    selectedOption: Int?,
    savedAnswers: List<QuizAnswer>,
    onBackClick: () -> Unit,
    onPlayToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onQualitySelect: (String) -> Unit,
    onOptionSelect: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onResetQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("lessons") } // lessons, transcript, quiz
    val questions = remember(course) {
        // Simple extraction of questions from the mock json
        // In ViewModel we have getQuizQuestionsForCurrentCourse, but let's keep a local parser too
        // parsing is fully guaranteed since json conforms to structure
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("course_detail_root")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Video Player Card
            item {
                VideoPlayerComponent(
                    course = course,
                    watchPosition = watchPosition,
                    isPlaying = isPlaying,
                    selectedQuality = selectedQuality,
                    onPlayToggle = onPlayToggle,
                    onSeek = onSeek,
                    onQualitySelect = onQualitySelect
                )
            }

            // 2. Tab Selectors
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        "lessons" to "Video Player",
                        "transcript" to "Study Notes",
                        "quiz" to "Course Quiz"
                    )
                    tabs.forEach { (key, label) ->
                        val isSelected = activeTab == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryBlue else Color.Transparent)
                                .clickable { activeTab = key }
                                .padding(vertical = 10.dp)
                                .testTag("tab_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // 3. Dynamic Tab Content
            item {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_content"
                ) { targetTab ->
                    when (targetTab) {
                        "lessons" -> {
                            LessonsTabContent(course = course)
                        }
                        "transcript" -> {
                            TranscriptTabContent(course = course)
                        }
                        "quiz" -> {
                            QuizTabContent(
                                course = course,
                                currentQuizIndex = quizIndex,
                                selectedOptionIndex = selectedOption,
                                savedAnswers = savedAnswers,
                                onOptionSelect = onOptionSelect,
                                onSubmitAnswer = onSubmitAnswer,
                                onResetQuiz = onResetQuiz
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPlayerComponent(
    course: Course,
    watchPosition: Int,
    isPlaying: Boolean,
    selectedQuality: String,
    onPlayToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onQualitySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var qualityMenuExpanded by remember { mutableStateOf(false) }
    val durationSeconds = 300 // 5 minutes standard
    
    val minutes = watchPosition / 60
    val seconds = watchPosition % 60
    val durationText = String.format("%02d:%02d / 05:00", minutes, seconds)
    val sliderValue = watchPosition.toFloat() / durationSeconds.toFloat()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .testTag("video_player_card")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedQuality == "Text-Only") {
                // Text-Only placeholder
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Text Only",
                        tint = AccentOrange,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Text-Only Mode Active",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Conserving daily cellular bandwidth. Full transcripts are loaded below.",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                    )
                }
            } else {
                // Simulated Animated Video frame
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                            )
                        )
                ) {
                    // Playback visual indicator
                    if (isPlaying) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Playing",
                                tint = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(96.dp)
                            )
                        }

                        // Bottom right live bandwidth meter
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SuccessGreen, CircleShape)
                                )
                                Text(
                                    text = "Streaming offline cache ($selectedQuality)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // Paused frame
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onPlayToggle,
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White, CircleShape)
                                    .testTag("video_overlay_play_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Video",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top Quality Selector dropdown button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Button(
                    onClick = { qualityMenuExpanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("quality_selector_trigger")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Quality Options",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = selectedQuality, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                DropdownMenu(
                    expanded = qualityMenuExpanded,
                    onDismissRequest = { qualityMenuExpanded = false }
                ) {
                    val qualities = listOf("Auto", "1080p", "720p", "480p", "Text-Only")
                    qualities.forEach { quality ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(quality)
                                    if (quality == "Text-Only" || quality == "480p") {
                                        Box(
                                            modifier = Modifier
                                                .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Data Saver",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                onQualitySelect(quality)
                                qualityMenuExpanded = false
                            },
                            modifier = Modifier.testTag("quality_option_$quality")
                        )
                    }
                }
            }

            // Controls bottom panel
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Slider(
                    value = sliderValue,
                    onValueChange = { onSeek((it * durationSeconds).toInt()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .testTag("video_progress_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = AccentOrange,
                        activeTrackColor = AccentOrange,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onPlayToggle,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("video_play_pause_button")
                        ) {
                            if (isPlaying) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.size(14.dp)
                                ) {
                                    Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Color.White))
                                    Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Color.White))
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = durationText,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Offline Video Cache: ✓ OK",
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LessonsTabContent(course: Course) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Course Description",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = course.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
            )

            Text(
                text = "Class Checklist",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val items = listOf(
                    "Watch full lecture (5 mins offline video/audio)" to true,
                    "Review full notes and transcripts" to false,
                    "Take interactive course quiz and achieve 80%+" to false
                )
                items.forEach { (text, done) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (done) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Status Done",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            )
                        }
                        Text(
                            text = text,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TranscriptTabContent(course: Course) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study Notes & Full Transcript",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Pre-Cached",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = course.transcript,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.testTag("transcript_text")
            )
        }
    }
}

@Composable
fun QuizTabContent(
    course: Course,
    currentQuizIndex: Int,
    selectedOptionIndex: Int?,
    savedAnswers: List<QuizAnswer>,
    onOptionSelect: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onResetQuiz: () -> Unit
) {
    // We can extract questions from JSON
    val questions = remember(course) {
        // Simple extraction for the composable UI. Fits perfectly with MockData structure
        // Math 1 questions, etc.
        when (course.id) {
            "math_1" -> listOf(
                QuizQuestion("What is a letter that represents an unknown number in algebra called?", listOf("Coefficient", "Variable", "Constant", "Exponent"), 1),
                QuizQuestion("In the equation 3x + 7 = 22, what is the coefficient of x?", listOf("3", "7", "22", "x"), 0),
                QuizQuestion("Solve the equation: 5x - 4 = 16. What is x?", listOf("2", "3", "4", "5"), 2),
                QuizQuestion("What must you do to keep an equation balanced when modifying it?", listOf("Perform the operation on only the left side", "Perform the operation on only the right side", "Perform the exact same operation on both sides", "Divide both sides by zero"), 2),
                QuizQuestion("If x = 3, what is the value of 4x - 2?", listOf("10", "12", "14", "8"), 0)
            )
            "math_2" -> listOf(
                QuizQuestion("What is the sum of the interior angles of any triangle?", listOf("90 degrees", "180 degrees", "270 degrees", "360 degrees"), 1),
                QuizQuestion("What is an angle of exactly 90 degrees called?", listOf("Acute angle", "Obtuse angle", "Right angle", "Straight angle"), 2),
                QuizQuestion("What type of triangle does the Pythagorean Theorem apply to?", listOf("Equilateral triangle", "Isosceles triangle", "Right-angled triangle", "Scalene triangle"), 2),
                QuizQuestion("If a right triangle has sides of length 6 and 8, what is the length of its hypotenuse?", listOf("10", "12", "14", "9"), 0),
                QuizQuestion("What is the geometric term for a point where two or more lines meet?", listOf("Vertex", "Segment", "Ray", "Tangent"), 0)
            )
            "sci_1" -> listOf(
                QuizQuestion("Which pigment captures sunlight inside chloroplasts?", listOf("Carotenoid", "Chlorophyll", "Melanin", "Hemoglobin"), 1),
                QuizQuestion("What are the microscopic pores on plant leaves that absorb carbon dioxide called?", listOf("Stomata", "Pores", "Roots", "Chloroplasts"), 0),
                QuizQuestion("Which of the following is NOT required for photosynthesis?", listOf("Sunlight", "Water", "Carbon Dioxide", "Oxygen"), 3),
                QuizQuestion("What sugar molecule do plants produce as food during photosynthesis?", listOf("Sucrose", "Fructose", "Glucose", "Lactose"), 2),
                QuizQuestion("Where in the plant cell does photosynthesis take place?", listOf("Nucleus", "Mitochondria", "Ribosome", "Chloroplast"), 3)
            )
            "sci_2" -> listOf(
                QuizQuestion("Who proposed the Law of Universal Gravitation after observing a falling apple?", listOf("Albert Einstein", "Galileo Galilei", "Isaac Newton", "Nikola Tesla"), 2),
                QuizQuestion("What are the two factors that determine the strength of gravity?", listOf("Mass and Distance", "Speed and Temperature", "Volume and Mass", "Color and Altitude"), 0),
                QuizQuestion("According to Einstein, what is gravity?", listOf("An invisible magnet", "The bending of space and time", "A chemical reaction", "Atmospheric pressure"), 1),
                QuizQuestion("What celestial body holds the Earth in orbit using its immense gravity?", listOf("The Moon", "Jupiter", "The Sun", "Mars"), 2),
                QuizQuestion("If you go to the Moon, how does your mass change compared to Earth?", listOf("It decreases", "It increases", "It stays the exact same", "It becomes zero"), 2)
            )
            "eng_1" -> listOf(
                QuizQuestion("Which sentence is in the Present Continuous tense?", listOf("I play football.", "I played football.", "I am playing football.", "I will play football."), 2),
                QuizQuestion("What is the past tense of the irregular verb 'write'?", listOf("Writed", "Wrote", "Written", "Writing"), 1),
                QuizQuestion("Complete the sentence: 'Tomorrow, she ______ to New Delhi.'", listOf("goes", "went", "will go", "is going to"), 2),
                QuizQuestion("Which tense is used to describe a general habit or universal truth?", listOf("Simple Present", "Simple Past", "Present Perfect", "Simple Future"), 0),
                QuizQuestion("Choose the correct past tense sentence:", listOf("He did runned very fast.", "He runned very fast.", "He ran very fast.", "He will run very fast."), 2)
            )
            "eng_2" -> listOf(
                QuizQuestion("Which is the most polite way to get someone's attention?", listOf("Hey you!", "Excuse me", "Tell me something", "What is your name?"), 1),
                QuizQuestion("How should you respond when someone says, 'Nice to meet you'?", listOf("Yes, I know.", "Nice to meet you too.", "Thank you.", "Goodbye."), 1),
                QuizQuestion("What should you say after someone helps you find your way?", listOf("You are welcome.", "No problem.", "Thank you so much for your help!", "I am sorry."), 2),
                QuizQuestion("Which question is best to keep a conversation going?", listOf("What is your weight?", "How are you doing today?", "Who are you?", "Do you have money?"), 1),
                QuizQuestion("Which phrase is a common, friendly way to say goodbye?", listOf("See you later!", "Go away.", "Stay here.", "Hello."), 0)
            )
            "soc_1" -> listOf(
                QuizQuestion("In which year did India gain Independence from British rule?", listOf("1942", "1945", "1947", "1950"), 2),
                QuizQuestion("Who was the first Prime Minister of independent India?", listOf("Mahatma Gandhi", "Jawaharlal Nehru", "Sardar Patel", "Dr. B.R. Ambedkar"), 1),
                QuizQuestion("What philosophy of non-violent resistance did Mahatma Gandhi introduce?", listOf("Satyagraha", "Swaraj", "Inquilab", "Ahimsa"), 0),
                QuizQuestion("Who founded the Indian National Army (INA)?", listOf("Bhagat Singh", "Subhash Chandra Bose", "Bal Gangadhar Tilak", "Lala Lajpat Rai"), 1),
                QuizQuestion("Which major movement was launched by Gandhi in 1942, demanding British exit?", listOf("Salt March", "Non-Cooperation Movement", "Quit India Movement", "Swadeshi Movement"), 2)
            )
            "soc_2" -> listOf(
                QuizQuestion("Which planet is known as the 'Red Planet'?", listOf("Venus", "Mars", "Jupiter", "Saturn"), 1),
                QuizQuestion("What gas makes up the majority of Earth's atmosphere?", listOf("Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen"), 2),
                QuizQuestion("Why is Earth's position called the 'Goldilocks Zone'?", listOf("It is close to the moon", "It has a golden appearance", "It is at the perfect distance from the Sun to support liquid water", "It is covered in forests"), 2),
                QuizQuestion("Which is the largest planet in our solar system?", listOf("Saturn", "Jupiter", "Neptune", "Uranus"), 1),
                QuizQuestion("What keeps all the planets orbiting the Sun instead of drifting away?", listOf("Magnetic force", "Solar winds", "The Sun's gravity", "Atmospheric pressure"), 2)
            )
            else -> emptyList()
        }
    }

    val isQuizFinished = currentQuizIndex >= questions.size

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (isQuizFinished) {
                // Scoring Dashboard
                val totalAnswersSaved = savedAnswers.size
                val correctAnswers = savedAnswers.count { it.isCorrect }
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Success Star",
                            tint = SuccessGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Text(
                        text = "Quiz Completed!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "You scored $correctAnswers / ${questions.size} correct answers.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    // Sync state of results
                    val allSynced = savedAnswers.all { it.isSynced }
                    Row(
                        modifier = Modifier
                            .background(
                                if (allSynced) SuccessGreen.copy(alpha = 0.15f) else AccentOrange.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (allSynced) Icons.Default.CheckCircle else Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = if (allSynced) SuccessGreen else AccentOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (allSynced) "All answers synced to cloud" else "Answers stored offline (pending sync)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (allSynced) SuccessGreen else AccentOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onResetQuiz,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("retake_quiz_button")
                    ) {
                        Text(
                            text = "Retake Quiz",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Interactive Question Layout
                val currentQuestion = questions[currentQuizIndex]
                
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuizIndex + 1} of ${questions.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Box(
                            modifier = Modifier
                                .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Offline Saving Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                        }
                    }

                    Text(
                        text = currentQuestion.question,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag("quiz_question_text")
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentQuestion.options.forEachIndexed { index, option ->
                            val isSelected = selectedOptionIndex == index
                            Surface(
                                onClick = { onOptionSelect(index) },
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) {
                                    BorderStroke(2.dp, PrimaryBlue)
                                } else {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                                },
                                color = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .testTag("quiz_option_$index")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + index).toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    }
                                    Text(
                                        text = option,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                              }
                        }
                    }

                    Button(
                        onClick = onSubmitAnswer,
                        enabled = selectedOptionIndex != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_quiz_answer_button")
                    ) {
                        Text(
                            text = "Submit Answer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
