package com.chac.core.designsystem.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: 실제 폰트 리소스 적용 시 교체
private val Pretendard: FontFamily = FontFamily.Default
private val Montserrat: FontFamily = FontFamily.Default

object ChacTextStyles {

    val Headline01 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    )

    val Headline02 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 18.sp,
        lineHeight = 21.6.sp,
        letterSpacing = 0.sp,
    )

    val Title = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    )

    val SubTitle01 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 18.sp,
        lineHeight = 21.6.sp,
        letterSpacing = 0.sp,
    )

    val SubTitle02 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    )

    val SubTitle03 = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )

    val ContentTitle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        letterSpacing = 0.sp,
    )

    val Body = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 15.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )

    val Caption = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 12.sp,
        lineHeight = 14.4.sp,
        letterSpacing = 0.sp,
    )

    val Btn = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        letterSpacing = 0.sp,
    )

    val SubBtn = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 14.sp,
        lineHeight = 16.8.sp,
        letterSpacing = 0.sp,
    )

    val Number = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 14.sp,
        lineHeight = 20.3.sp,
        letterSpacing = 0.14.sp,
    )

    val ToastBody = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 14.sp,
        lineHeight = 20.3.sp,
        letterSpacing = 0.sp,
    )
}

/**
 * Material3 Typography로도 매핑하고 싶으면 이렇게 한 번 더 감싸서 쓰면 편해.
 * (원하는 스타일만 골라 매핑해도 OK)
 */
val ChacTypography = Typography(
    headlineLarge = ChacTextStyles.Headline01,
    headlineMedium = ChacTextStyles.Headline02,
    titleLarge = ChacTextStyles.Title,
    titleMedium = ChacTextStyles.SubTitle01,
    titleSmall = ChacTextStyles.SubTitle02,
    bodyLarge = ChacTextStyles.Body,
    bodyMedium = ChacTextStyles.ToastBody,
    labelLarge = ChacTextStyles.Btn,
    labelMedium = ChacTextStyles.SubBtn,
    labelSmall = ChacTextStyles.Caption,
)
