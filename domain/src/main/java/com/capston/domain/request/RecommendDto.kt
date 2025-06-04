package com.capston.domain.request

import com.capston.domain.response.enum_class.Subject

class RecommendDto (
    val grade: String = "",
    val schoolRank: Int = 0, // 내신 등급
    val mockRank: Int = 0, // 모의고사 등급
    val focus: String = "", // 학습 방향 (수능 중심 / 내신 중심)
    val goal: String = "", // 학습 목표 (개념 정리 / 기출 분석 / 실전 문제풀이 / 빠른 요약 정리)
    val styles: List<String> = emptyList(),
    val subject: Subject = Subject.ENG
)