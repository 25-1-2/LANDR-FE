<p align="center"><img width="940" alt="표지" src="https://github.com/user-attachments/assets/71ebc912-aa10-44c7-8b0a-da51db640d10" /></p>
<br/>

<div align="center">
  
  <h1>✨ LANDR ✨</h1>
  
  <h3><b>"효과적인 인강 학습, LANDR와 함께"</b><br/></h3>
  LANDR는 인터넷 강의 학습 관리 앱입니다
  
</div>

<br/><br/>

<div align="center">

<h3>🎯 <b>"오늘은 꼭 들어야지" 다짐만 하고 있나요?</b></h3>

<b>LANDR</b>가 <u>강의 계획</u>을 대신 생성해드립니다!  
지금 바로, <b>작심삼일</b>을 <b>작심완강</b>으로 바꿔보세요! 🚀

</div>

<br/><br/>


<p align="center">
    <img width="940" alt="홍보1" src="https://github.com/user-attachments/assets/6e1b6656-4d45-4987-a759-0c528c5aac36" />
    <img width="940" alt="홍보2" src="https://github.com/user-attachments/assets/a240e40c-c0b1-4289-b2ed-a2c8c038ff74" />
    <img width="940" alt="홍보3" src="https://github.com/user-attachments/assets/7680eace-c79b-4547-a5b7-0e53eb30b863" />
    <img width="940" alt="홍보4" src="https://github.com/user-attachments/assets/c2245084-d696-432a-94b1-4b582ff08360" />
</p>

## 소프트웨어 아키텍처
<p align="center"> <img width="940" alt="시스템 구조도" src="https://github.com/user-attachments/assets/3cb2400a-8d76-4338-912d-3498edc6c42e" /> </p>

## 앱 구조도
<p align="center"> <img width="940" alt="앱 구조도" src="https://github.com/user-attachments/assets/2a810c9f-34fc-46d6-a676-58c479c443a8" /> </p>

## ✨ 주요 기능

## 기술 스택
|Category|Technology Stack|
|:---:|:---|
|Architecture|<img src="https://img.shields.io/badge/Clean_Architecture-4CAF50?style=for-the-badge&logo=android&logoColor=white"> <img src="https://img.shields.io/badge/MVVM-2196F3?style=for-the-badge&logo=android&logoColor=white">|
|UI|<img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"> <img src="https://img.shields.io/badge/Material3-757575?style=for-the-badge&logo=materialdesign&logoColor=white">|
|DI|<img src="https://img.shields.io/badge/Hilt-FF6F00?style=for-the-badge&logo=android&logoColor=white">|
|Network|<img src="https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white"> <img src="https://img.shields.io/badge/OkHttp-3E4348?style=for-the-badge&logo=square&logoColor=white">|
|Local Storage|<img src="https://img.shields.io/badge/DataStore-1976D2?style=for-the-badge&logo=android&logoColor=white">|
|Authentication|<img src="https://img.shields.io/badge/Firebase_Auth-DD2C00?style=for-the-badge&logo=firebase&logoColor=white"> <img src="https://img.shields.io/badge/Google_Login-4285F4?style=for-the-badge&logo=google&logoColor=white">|
|Push Notification|<img src="https://img.shields.io/badge/Firebase_FCM-DD2C00?style=for-the-badge&logo=firebase&logoColor=white">|
|Navigation|<img src="https://img.shields.io/badge/Navigation_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white">|
|Async|<img src="https://img.shields.io/badge/Kotlin_Coroutines-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"> <img src="https://img.shields.io/badge/Flow-0095D5?style=for-the-badge&logo=kotlin&logoColor=white">|
|JSON|<img src="https://img.shields.io/badge/Gson-FF6F00?style=for-the-badge&logo=json&logoColor=white">|
|Animation|<img src="https://img.shields.io/badge/Lottie-FF6B6B?style=for-the-badge&logo=airbnb&logoColor=white">|
|Build Tool|<img src="https://img.shields.io/badge/Gradle_KTS-02303A?style=for-the-badge&logo=gradle&logoColor=white">|
|Language|<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">|
|Version Control|<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">|

## 프로젝트 구조
```
├── README.md
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
│
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── java/com/capston/
│       │   ├── res/
│       │   │   ├── drawable/
│       │   │   ├── font/
│       │   │   ├── layout/
│       │   │   ├── mipmap/
│       │   │   ├── values/
│       │   │   └── xml/
│       │   └── AndroidManifest.xml
│       ├── test/
│       └── androidTest/
│
├── data/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── java/com/capston/data/
│       │   │   ├── di/
│       │   │   ├── loading/
│       │   │   ├── local/
│       │   │   │   └── storage/
│       │   │   └── repository/
│       │   │       ├── remote/
│       │   │       │   ├── api/
│       │   │       │   ├── datasourcelmpl/
│       │   │       │   └── repositoryImpl/
│       │   │       └── local/
│       │   └── AndroidManifest.xml
│       ├── test/
│       └── androidTest/
│
├── domain/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   └── java/com/capston/domain/
│       │       ├── base/
│       │       ├── datasource/
│       │       ├── manager/
│       │       ├── model/
│       │       ├── repository/
│       │       ├── request/
│       │       ├── response/
│       │       │   ├── daily_schedule/
│       │       │   ├── enum_class/
│       │       │   ├── home/
│       │       │   ├── lecture/
│       │       │   ├── mypage/
│       │       │   ├── plan/
│       │       │   ├── recommend/
│       │       │   ├── study_group/
│       │       │   └── user/
│       │       └── usecase/
│       │           ├── daily_schedule/
│       │           ├── error/
│       │           ├── home/
│       │           ├── lecture/
│       │           ├── login/
│       │           ├── mypage/
│       │           ├── plan/
│       │           ├── recommend/
│       │           ├── study_group/
│       │           └── token/
│       └── test/
│
└── presentation/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── java/com/capston/presentation/
        │   │   ├── service/
        │   │   ├── theme/
        │   │   ├── ui/
        │   │   │   ├── common/
        │   │   │   ├── home/
        │   │   │   ├── login/
        │   │   │   ├── onboarding/
        │   │   │   └── search/
        │   │   └── viewmodel/
        │   ├── res/
        │   │   ├── drawable/
        │   │   ├── font/
        │   │   ├── layout/
        │   │   ├── mipmap/
        │   │   ├── values/
        │   │   └── xml/
        │   └── AndroidManifest.xml
        ├── test/
        └── androidTest/
```

## 안드로이드
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/zunione"><img width="200" src="" alt=""/><br /><sub><b>전희원</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/eccho03"><img width="200" src="https://github.com/user-attachments/assets/e2f75f85-d4f8-4b05-af57-342a7f5b03dc" alt=""/><br /><sub><b>조은채</b></sub></a><br /></td>
    </tr>
  </tbody>
</table>
