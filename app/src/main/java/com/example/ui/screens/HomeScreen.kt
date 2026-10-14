package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    testSeriesList: List<TestSeriesEntity>,
    onExploreClick: () -> Unit,
    onLoginClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onSeriesClick: (String) -> Unit,
    onDemoLogin: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column")
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(NavyPrimary, NavyLight, NavyDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MADHYA PRADESH SUB-INSPECTOR 2026",
                                color = GoldBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "MPSI TEST SERIES",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"Practice Smart. Perform Better. Become MPSI.\"",
                        fontSize = 15.sp,
                        color = GoldBright,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "The premier online mock-test engine for MP Police Sub-Inspector aspirants. Authentic syllabus coverage, negative marking simulation, timer proctoring, and comprehensive analytics.",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onExploreClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldBright,
                                contentColor = NavyDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_explore_button")
                        ) {
                            Text(
                                text = "Explore Tests",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        if (currentUser != null) {
                            Button(
                                onClick = onDashboardClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BlueBright,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("hero_dashboard_button")
                            ) {
                                Text(
                                    text = "My Dashboard",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = onLoginClick,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("hero_login_button")
                            ) {
                                Text(
                                    text = "Student Login",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Quick Demo Switcher Card for Evaluators
                    Spacer(modifier = Modifier.height(18.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Quick Instant Test Login (Evaluation)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onDemoLogin("STUDENT") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BlueAccent
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("demo_student_quick_btn")
                                ) {
                                    Text("Demo Student", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { onDemoLogin("ADMIN") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDC2626)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("demo_admin_quick_btn")
                                ) {
                                    Text("Demo Admin", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Why Choose MPSI Test Series?
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Why Choose MPSI Test Series?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Crafted specifically for the Madhya Pradesh Sub-Inspector recruitment exam standard.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                val features = listOf(
                    FeatureItem(Icons.Default.Assignment, "Exam-Focused Mock Tests", "Curated by top MP police faculty matching recent exam trends & questions."),
                    FeatureItem(Icons.Default.QuestionAnswer, "Detailed Answer Explanations", "Step-by-step solutions for Maths, MP GK references, and Hindi grammar rules."),
                    FeatureItem(Icons.Default.Speed, "Instant Results", "Live evaluation with negative marking (-0.25) & percentile score."),
                    FeatureItem(Icons.Default.Analytics, "Performance Analytics", "Visual breakdown of accuracy, time management, and subject strengths."),
                    FeatureItem(Icons.Default.Smartphone, "Mobile-Friendly Testing", "Engineered for smooth, touch-friendly test taking on smartphones."),
                    FeatureItem(Icons.Default.Security, "Secure Student Access", "Payment-locked test series with protected links and basic anti-cheat proctoring.")
                )

                features.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            FeatureCard(
                                item = item,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Section: "Your preparation. Measured."
        item {
            Surface(
                color = NavyLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GoldBright.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your preparation. Measured.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Every question attempted produces real telemetry. Spot weak topics in MP Geography or Hindi Sandhi before exam day. Compete with thousands of fellow candidates across Madhya Pradesh.",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        // Section: Featured Test Series
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Test Series",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Choose your preparation path",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                testSeriesList.forEach { series ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .clickable { onSeriesClick(series.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = BlueBright.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = series.exam,
                                            color = BlueBright,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = series.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextDark
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${series.price.toInt()}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "₹${series.originalPrice.toInt()}",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        style = androidx.compose.ui.text.TextStyle(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = series.description,
                                fontSize = 12.sp,
                                color = TextMuted,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Assignment,
                                            contentDescription = null,
                                            tint = BlueBright,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${series.numberOfTests} Tests",
                                            fontSize = 11.sp,
                                            color = TextDark,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${series.durationMinutes} Mins",
                                            fontSize = 11.sp,
                                            color = TextDark,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "View Details",
                                        color = BlueBright,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = BlueBright,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Frequently Asked Questions (FAQ)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                val faqs = listOf(
                    "What is the medium/language of the mock tests?" to "The tests are available in both Hindi & English, specifically designed according to the MP Police board examination format.",
                    "Is negative marking applied in these tests?" to "Yes, each test follows the standard MPSI scheme of +1 mark for correct answers and -0.25 mark deduction for wrong answers.",
                    "Can I access my test multiple times?" to "Yes, depending on the test configuration, each test permits multiple attempts to refine your preparation.",
                    "How does payment and enrollment work?" to "We use secure Razorpay integration. Once payment is confirmed, access to the entire test series is immediately unlocked in your student dashboard."
                )

                faqs.forEach { (question, answer) ->
                    FaqAccordion(question = question, answer = answer)
                }
            }
        }

        // Section: Contact & Help Desk
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need Assistance with MPSI Tests?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "MP Police Exam Support Cell, Bhopal, Madhya Pradesh",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GoldBright, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+91 755 2400100", color = Color.White, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = GoldBright, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("support@mpsi.com", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@Composable
fun FeatureCard(item: FeatureItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavyPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun FaqAccordion(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
