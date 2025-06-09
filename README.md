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

## 브랜치 구조

- **`main`** - 배포용 브랜치 (프로덕션)
- **`release`** - 릴리즈 준비 브랜치
- **`develop`** - 개발 통합 브랜치
- **`feature/*`** - 기능 개발 브랜치

## 📝 커밋 규칙

커밋 메시지는 다음 형식을 따릅니다:

```
TYPE(파일명) : 커밋 메시지
```

### 커밋 타입

| 타입 | 설명 | 예시 |
|------|------|------|
| `INIT` | 초기 프로젝트 생성 시 | `INIT(ALL) : 프로젝트 초기 설정` |
| `FEAT` | 새로운 기능 추가 | `FEAT(MainActivity.kt) : 사용자 로그인 기능 추가` |
| `FIX` | 버그, 오류 수정 | `FIX(LoginActivity.kt) 로그인 시 에러 처리 수정` |
| `REFACTO` | 전면 수정(코드 리펙토링) | `REFACTO((LoginActivity.kt) : 사용자 인증 로직 리팩토링` |
| `CHORE` | 코드 수정, 내부 파일 수정 | `CHORE(DATA) : 패키지 의존성 업데이트` |
| `TEST` | 테스트 코드 추가 및 수정 | `TEST((LoginActivity.kt) 로그인 컴포넌트 테스트 추가` |
| `RENAME` | 변수명, 파일명 수정 | `RENAME(MainActivity.kt) : 컴포넌트 파일명 변경` |

## 🔄 개발 워크플로우

### 1. 이슈 생성
- GitHub Issues에서 작업할 내용을 이슈로 등록
- 이슈 번호와 제목을 명확하게 작성

### 2. 개발 브랜치 생성 및 개발
```bash
# develop 브랜치에서 새 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b feature/issue-번호-기능명

# 개발 작업 수행
# 커밋 규칙에 따라 커밋
git add .
git commit -m "FEAT(MainActivity.kt) : 새로운 기능 구현"
```

### 3. Pull Request 생성
- 개발 완료 후 GitHub에서 PR 생성
- `feature/브랜치` → `develop` 브랜치로 PR
- 이슈 번호를 PR 제목이나 설명에 포함
- 코드 리뷰 요청

### 4. Develop 브랜치 Merge
- 코드 리뷰 완료 후 develop 브랜치에 merge
- 머지 후 feature 브랜치 삭제

## 📋 체크리스트

### PR 생성 전 체크사항
- [ ] 커밋 메시지가 규칙에 맞게 작성되었는가?
- [ ] 코드가 정상적으로 동작하는가?
- [ ] 테스트 코드가 작성되었는가?
- [ ] 관련 이슈가 연결되어 있는가?

### 머지 전 체크사항
- [ ] 코드 리뷰가 완료되었는가?
- [ ] 충돌이 해결되었는가?

## 🚀 릴리즈 프로세스
1. `develop` → `release` 브랜치 생성
2. 릴리즈 테스트 및 버그 수정
3. `release` → `main` 브랜치 머지
4. 버전 태그 생성 및 배포
