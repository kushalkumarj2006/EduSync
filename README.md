# 📚 EduSync - Offline-First Education Platform

<div align="center">
  
![EduSync Logo](https://img.shields.io/badge/EduSync-Offline%20Education-blue?style=for-the-badge&logo=android)
![Version](https://img.shields.io/badge/Version-1.0-green?style=flat-square)
![Platform](https://img.shields.io/badge/Platform-Android-orange?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?style=flat-square&logo=kotlin)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

**"Learn Without Limits" - Education for Rural India's 5 Million Students**

[![Architecture](https://img.shields.io/badge/📐_Architecture-blue?style=for-the-badge)](#system-architecture) 
[![Features](https://img.shields.io/badge/✨_Features-green?style=for-the-badge)](#features) 
[![Setup](https://img.shields.io/badge/⚙️_Setup-orange?style=for-the-badge)](#quick-setup) 
[![Diagrams](https://img.shields.io/badge/📊_Diagrams-purple?style=for-the-badge)](#system-design-diagrams)

</div>

---

## 📖 Overview

**EduSync** is an offline-first education platform designed for rural students in India with limited internet connectivity. Built with Jetpack Compose and Room database, it enables seamless learning even without network access, with intelligent store-and-forward synchronization.

### 🎯 Target Population
- **5 Million** rural students
- **1.5 Million** daily active users
- **$50,000/month** budget constraint
- **50MB** daily data limit

---

## ✨ Features

> **Click each feature to learn more**

<details>
<summary><b>📱 Offline-First Learning</b></summary>

All content is cached locally using Room Database. Students can:
- Watch videos without internet
- Complete assignments offline
- Access all course materials anytime
- Progress saved automatically
</details>

<details>
<summary><b>🔄 Store-and-Forward Sync</b></summary>

Intelligent sync engine that:
- Queues all offline activities
- Auto-syncs when connection detected
- Shows pending items count
- Uses last-write-wins conflict resolution
- Provides visual sync progress bar
</details>

<details>
<summary><b>🎬 Dynamic Quality Switching</b></summary>

Video quality options:
- **Auto** - Adaptive based on network
- **1080p** - HD quality
- **720p** - Standard HD
- **480p** - Data saver mode
- **Text-Only** - Zero bandwidth mode
</details>

<details>
<summary><b>📊 Data Minimization</b></summary>

Smart bandwidth management:
- 50MB daily budget tracker
- Data Saver toggle (70% reduction)
- Text-Only mode (100% reduction)
- Real-time usage monitoring
- Warning at 90% usage
</details>

<details>
<summary><b>🎮 Gamification System</b></summary>

Earn badges and track streaks:
- 6 achievement badges
- Daily study streak tracker
- 7-day visual calendar
- Badge details dialog
- Progress notifications
</details>

<details>
<summary><b>🔐 Resilient Identity</b></summary>

Offline authentication:
- Local session persistence
- Demo mode for instant access
- SharedPreferences token storage
- No internet required after first login
- Secure logout
</details>

---

## 🏗️ System Architecture

### High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer - Android App"
        UI[User Interface<br/>Jetpack Compose]
        VM[ViewModel<br/>State Management]
        REPO[Repository<br/>Data Layer]
    end
    
    subgraph "Local Storage Layer"
        ROOM[(Room Database<br/>SQLite)]
        PREF[SharedPreferences<br/>Auth & Streak]
    end
    
    subgraph "Sync Engine Layer"
        SYNC[Store-and-Forward<br/>Sync Engine]
        PENDING[Pending Queue<br/>Unsynced Items]
    end
    
    subgraph "Cloud Layer (Future)"
        FIREBASE[(Firebase/Firestore<br/>Cloud Sync)]
        CDN[CDN<br/>Video Content]
    end
    
    UI --> VM
    VM --> REPO
    REPO --> ROOM
    REPO --> PREF
    REPO --> SYNC
    SYNC --> PENDING
    SYNC --> FIREBASE
    UI --> CDN
    
    style UI fill:#4F46E5,color:#fff
    style VM fill:#7C3AED,color:#fff
    style REPO fill:#EC4899,color:#fff
    style ROOM fill:#10B981,color:#fff
    style SYNC fill:#F59E0B,color:#fff
    style FIREBASE fill:#EF4444,color:#fff
```

---

### Offline-First Workflow

```mermaid
flowchart TD
    A[User Opens App] --> B{Internet Available?}
    
    B -->|Yes| C[Load Content from<br/>Cloud/CDN]
    B -->|No| D[Load from Local<br/>Room Database]
    
    C --> E[Cache to Room DB<br/>for Offline Use]
    D --> F[Display Content<br/>Offline Mode]
    
    F --> G[User Watches Video<br/>or Takes Quiz]
    G --> H[Data Saved Locally<br/>isSynced = false]
    
    H --> I{Internet Available?}
    I -->|No| J[Queue in Pending Sync]
    I -->|Yes| K[Auto/Manual Sync]
    
    J --> L[Store-and-Forward<br/>Queue]
    L --> I
    
    K --> M[Sync to Cloud<br/>isSynced = true]
    M --> N[Clear Pending Queue]
    
    N --> O[Update UI<br/>All Synced]
    
    style A fill:#4F46E5,color:#fff
    style H fill:#F59E0B,color:#fff
    style J fill:#EF4444,color:#fff
    style M fill:#10B981,color:#fff
```

---

### MVVM Architecture Flow

```mermaid
flowchart TB
    subgraph "UI Layer (View)"
        V1[LoginScreen]
        V2[DashboardScreen]
        V3[CourseDetailScreen]
    end
    
    subgraph "ViewModel Layer"
        VM1[EduSyncViewModel]
        VM2[StateFlow<br/>- currentUser]
        VM3[StateFlow<br/>- courses]
        VM4[StateFlow<br/>- pendingSyncCount]
        VM5[StateFlow<br/>- studyStreakCount]
        VM6[StateFlow<br/>- studyBadges]
    end
    
    subgraph "Repository Layer"
        R1[EduSyncRepository]
        R2[CourseDao]
        R3[UserProgressDao]
        R4[VideoWatchTimeDao]
        R5[QuizAnswerDao]
        R6[DataUsageDao]
    end
    
    subgraph "Local Database"
        DB1[(Room Database)]
    end
    
    subgraph "Network Layer"
        N1[Firebase/Firestore<br/>Cloud Sync]
    end
    
    V1 --> VM1
    V2 --> VM1
    V3 --> VM1
    
    VM1 --> VM2
    VM1 --> VM3
    VM1 --> VM4
    VM1 --> VM5
    VM1 --> VM6
    
    VM1 --> R1
    R1 --> R2
    R1 --> R3
    R1 --> R4
    R1 --> R5
    R1 --> R6
    
    R2 --> DB1
    R3 --> DB1
    R4 --> DB1
    R5 --> DB1
    R6 --> DB1
    
    R1 --> N1
    
    style VM1 fill:#7C3AED,color:#fff
    style R1 fill:#EC4899,color:#fff
    style DB1 fill:#10B981,color:#fff
    style N1 fill:#EF4444,color:#fff
```

---

## 🗄️ Database Schema

### Room Database Entities

```mermaid
erDiagram
    COURSES ||--o{ USER_PROGRESS : "tracks"
    COURSES ||--o{ VIDEO_WATCH_TIME : "stores"
    COURSES ||--o{ QUIZ_ANSWERS : "contains"
    
    COURSES {
        string id PK
        string title
        string description
        string category
        string thumbnailUrl
        string videoUrl
        string transcript
        string quizQuestionsJson
    }
    
    USER_PROGRESS {
        string courseId PK
        int progressPercentage
        long lastWatchedTimestamp
        boolean isSynced
    }
    
    VIDEO_WATCH_TIME {
        string courseId PK
        int lastWatchedPositionSeconds
        long updatedAt
        boolean isSynced
    }
    
    QUIZ_ANSWERS {
        string courseId PK
        int questionIndex PK
        int selectedOptionIndex
        boolean isCorrect
        long updatedAt
        boolean isSynced
    }
    
    DATA_USAGE {
        string dateString PK
        long bytesUsed
    }
```

---

## 🔄 Store-and-Forward Sync Engine

### Sync Sequence Diagram

```mermaid
sequenceDiagram
    participant App as Android App
    participant Local as Room DB
    participant Queue as Pending Queue
    participant Cloud as Firebase/Firestore
    
    Note over App,Cloud: OFFLINE SCENARIO
    
    App->>Local: Save Progress (isSynced=false)
    Local->>Queue: Add to Pending Queue
    
    App->>Local: Save Quiz Answer (isSynced=false)
    Local->>Queue: Add to Pending Queue
    
    App->>App: Continue Offline Learning
    
    Note over App,Cloud: CONNECTION RESTORED
    
    App->>Cloud: Check Internet Connection
    Cloud-->>App: Connected
    
    App->>Queue: Fetch All Pending Items
    Queue-->>App: List of Unsynced Items
    
    loop Each Pending Item
        App->>Cloud: Upload Data (with timestamp)
        Cloud-->>App: Acknowledged (Server Timestamp)
        App->>Local: Update isSynced = true
        App->>Queue: Remove from Pending
        App->>App: Update UI Progress
    end
    
    App->>App: "All Data Synced Successfully"
```

---

## 🎮 Gamification System

### Badges & Achievements

```mermaid
flowchart LR
    subgraph "User Actions"
        WATCH[Watch Video]
        QUIZ[Take Quiz]
        OFFLINE[Study Offline]
        DATASAVER[Enable Data Saver]
        STREAK[Study Daily]
    end
    
    subgraph "Badge Conditions"
        B1[Bronze Starter<br/>Watch Any Video]
        B2[Gold Finisher<br/>Complete 1 Course]
        B3[Quiz Scholar<br/>Answer Quiz]
        B4[Offline Champion<br/>Study Offline]
        B5[Data Conservator<br/>Enable Data Saver]
        B6[Streak Master<br/>3-Day Streak]
    end
    
    subgraph "Rewards"
        R1[🏅 Badge Unlocked]
        R2[⭐ Streak Counter]
        R3[📊 Progress Tracking]
    end
    
    WATCH --> B1
    WATCH --> B2
    QUIZ --> B3
    OFFLINE --> B4
    DATASAVER --> B5
    STREAK --> B6
    
    B1 --> R1
    B2 --> R1
    B3 --> R1
    B4 --> R1
    B5 --> R1
    B6 --> R1
    
    STREAK --> R2
    WATCH --> R3
    QUIZ --> R3
    
    style WATCH fill:#4F46E5,color:#fff
    style QUIZ fill:#7C3AED,color:#fff
    style OFFLINE fill:#EC4899,color:#fff
    style B1 fill:#F59E0B,color:#fff
    style B6 fill:#10B981,color:#fff
    style R1 fill:#EF4444,color:#fff
```

### Available Badges

| Badge | Icon | Unlock Condition |
|-------|------|------------------|
| Bronze Starter | ⭐ | Watch any course video |
| Gold Finisher | ✅ | Complete 100% of any course |
| Quiz Scholar | ⚡ | Submit any quiz answer |
| Offline Champion | ☁️ | Study without internet |
| Data Conservator | 📶 | Enable Data Saver mode |
| Streak Master | ❤️ | 3+ day study streak |

---

## 📉 Data Minimization Strategy

### Bandwidth Optimization

```mermaid
flowchart TD
    subgraph "Data Saver Mode OFF"
        A1[1080p Video<br/>~250KB/sec]
        A2[PNG Images<br/>~500KB each]
        A3[REST API Calls<br/>Every Request]
    end
    
    subgraph "Data Saver Mode ON"
        B1[480p Video<br/>~35KB/sec<br/>75% Reduction]
        B2[WebP Images<br/>~150KB each<br/>70% Reduction]
        B3[Cached Responses<br/>90% Reduction]
    end
    
    subgraph "Text-Only Mode"
        C1[No Video<br/>0 KB/sec<br/>100% Reduction]
        C2[Text Transcript<br/>~2KB total]
        C3[Pre-cached Quiz<br/>0 Network]
    end
    
    subgraph "Bandwidth Budget"
        D1[Daily Limit: 50MB]
        D2[Used: 12.3MB]
        D3[Remaining: 37.7MB]
        D4[Progress: ████░░░░░░ 24.6%]
    end
    
    A1 -->|Toggle| B1
    A2 -->|Toggle| B2
    A3 -->|Toggle| B3
    
    B1 -->|Extreme| C1
    B2 -->|Extreme| C2
    B3 -->|Extreme| C3
    
    D1 --> D4
    D2 --> D4
    D3 --> D4
    
    style A1 fill:#EF4444,color:#fff
    style B1 fill:#F59E0B,color:#fff
    style C1 fill:#10B981,color:#fff
    style D4 fill:#4F46E5,color:#fff
```

---

## 🔐 Resilient Identity System

### Offline Authentication Flow

```mermaid
flowchart TD
    subgraph "Authentication Flow"
        A[User Opens App] --> B{Has Local Session?}
        
        B -->|Yes| C[Load User from<br/>SharedPreferences]
        B -->|No| D[Show Login Screen]
        
        D --> E[Enter Credentials]
        E --> F{Internet Available?}
        
        F -->|Yes| G[Validate with<br/>Firebase Auth]
        F -->|No| H[Store Credentials<br/>for Later Sync]
        
        G --> I{Valid?}
        I -->|Yes| J[Save Session<br/>to SharedPreferences]
        I -->|No| K[Show Error]
        
        H --> L[Save for Later]
        J --> M[Grant Access<br/>to Dashboard]
    end
    
    subgraph "Offline Access"
        N[User with Session] --> O[Read Local Token]
        O --> P[Grant Offline Access]
        P --> Q[Store New Data<br/>isSynced=false]
    end
    
    subgraph "Logout"
        R[User Logout] --> S[Clear Session]
        S --> T[Remove from<br/>SharedPreferences]
        T --> U[Return to Login]
    end
    
    M --> N
    Q --> L
    
    style A fill:#4F46E5,color:#fff
    style J fill:#10B981,color:#fff
    style P fill:#10B981,color:#fff
    style K fill:#EF4444,color:#fff
```

---

## 📊 Data Flow Diagram

```mermaid
flowchart LR
    subgraph "External Entities"
        E1[Student]
        E2[Teacher/Admin]
        E3[Cloud Server]
    end
    
    subgraph "Processes"
        P1[1.0<br/>User Authentication]
        P2[2.0<br/>Course Management]
        P3[3.0<br/>Video Playback]
        P4[4.0<br/>Quiz System]
        P5[5.0<br/>Sync Engine]
        P6[6.0<br/>Data Saver]
    end
    
    subgraph "Data Stores"
        D1[(D1<br/>User Credentials)]
        D2[(D2<br/>Course Data)]
        D3[(D3<br/>Progress Data)]
        D4[(D4<br/>Pending Queue)]
    end
    
    E1 -->|Login Request| P1
    P1 -->|Store Session| D1
    
    E1 -->|View Courses| P2
    P2 -->|Fetch from Cache| D2
    P2 -->|Update Progress| D3
    
    E1 -->|Play Video| P3
    P3 -->|Save Position| D3
    P3 -->|Queue if Offline| D4
    
    E1 -->|Take Quiz| P4
    P4 -->|Save Answers| D3
    P4 -->|Queue if Offline| D4
    
    P5 -->|Sync Data| E3
    E3 -->|Acknowledge| P5
    P5 -->|Mark Synced| D3
    P5 -->|Clear Queue| D4
    
    E1 -->|Toggle| P6
    P6 -->|Compress| P3
    
    style E1 fill:#4F46E5,color:#fff
    style P5 fill:#F59E0B,color:#fff
    style D4 fill:#EF4444,color:#fff
    style E3 fill:#10B981,color:#fff
```

---

## 🚀 Deployment & Scaling Strategy

### Production Architecture

```mermaid
flowchart TD
    subgraph "User Layer"
        U1[5 Million<br/>Rural Students]
        U2[1.5 Million<br/>Daily Active Users]
    end
    
    subgraph "Edge Layer"
        E1[CDN Edge Servers<br/>Video Content]
        E2[Telecom Tower<br/>Cache Nodes]
    end
    
    subgraph "Application Layer"
        A1[App Servers<br/>Auto-Scaling]
        A2[Load Balancer]
    end
    
    subgraph "Database Layer"
        D1[(Regional DB<br/>Shard 1)]
        D2[(Regional DB<br/>Shard 2)]
        D3[(Regional DB<br/>Shard N)]
    end
    
    subgraph "Cost Optimization"
        C1["Offline-First<br/>~70% API Reduction"]
        C2["Data Saver<br/>~50% Bandwidth"]
        C3["Spot Instances<br/>~60% Compute Cost"]
        C4["Total: $50k/month"]
    end
    
    U1 --> E1
    U2 --> E1
    U1 --> E2
    U2 --> E2
    
    E1 --> A2
    E2 --> A2
    A2 --> A1
    
    A1 --> D1
    A1 --> D2
    A1 --> D3
    
    C1 --> C4
    C2 --> C4
    C3 --> C4
    
    style U1 fill:#4F46E5,color:#fff
    style E1 fill:#F59E0B,color:#fff
    style A1 fill:#EC4899,color:#fff
    style D1 fill:#10B981,color:#fff
    style C4 fill:#EF4444,color:#fff
```

---

## 🛠️ Quick Setup

### Prerequisites
- Android Studio Ladybug (2024.3.1) or newer
- JDK 11 or higher
- Android SDK API 35+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/kushalkumarj2006/EduSync.git
cd EduSync
```

2. **Open in Android Studio**
```bash
File → Open → Select project folder
```

3. **Sync Gradle**
```bash
File → Sync Project with Gradle Files
```

4. **Run the app**
```bash
Select Pixel 6 API 35+ → Click Run ▶
```

5. **Launch Demo Mode**
- Click **"Launch Demo Mode (Instant Login)"**
- App works 100% offline immediately!

---

## 🎯 System Constraints Addressed

| Constraint | Solution |
|------------|----------|
| **Offline-First** | Room DB + Store-and-Forward |
| **50MB Daily Data** | Data Saver + Text-Only Mode |
| **5M Users** | Edge caching + Sharding strategy |
| **$50k/month** | Offline-first reduces API costs |
| **Resilient Identity** | SharedPreferences auth |
| **Bandwidth Efficiency** | Dynamic quality switching |

---

## 📱 Screenshots

<div align="center">
  
| Login | Dashboard | Course Detail |
|-------|-----------|---------------|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) | ![Course](screenshots/course.png) |

| Video Player | Quiz | Streak & Badges |
|--------------|------|-----------------|
| ![Video](screenshots/video.png) | ![Quiz](screenshots/quiz.png) | ![Streak](screenshots/streak.png) |

</div>

---

## 📚 Tech Stack Details

### Dependencies

```gradle
dependencies {
    // UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.activity.compose)
    
    // Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
}
```

---

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run Robolectric tests
./gradlew testDebugUnitTest
```

---

## 🚢 Deployment

### Build APK

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

### Build AAB (Play Store)

```bash
./gradlew bundleRelease
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 🙏 Acknowledgments

- **IEEE CS Bangalore Chapter** for System Siege 2026
- **Google** for Android development tools
- **JetBrains** for Kotlin and Compose
- **Open Source Community** for all the amazing libraries

---

## 📞 Contact

**Project Link:** [https://github.com/kushalkumarj2006/EduSync](https://github.com/kushalkumarj2006/EduSync)

---

<div align="center">
  
**Made with ❤️ for Rural India's Students**

[⬆ Back to Top](#-edusync---offline-first-education-platform)

</div>
