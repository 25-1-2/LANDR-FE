# LANDR
<img width="940" alt="표지" src="https://github.com/user-attachments/assets/71ebc912-aa10-44c7-8b0a-da51db640d10" />

<img width="940" alt="시스템 구조도" src="https://github.com/user-attachments/assets/3cb2400a-8d76-4338-912d-3498edc6c42e" />

<img width="940" alt="앱 구조도" src="https://github.com/user-attachments/assets/246e1d1a-5487-460f-9d2b-aeacd2a0176b" />

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
