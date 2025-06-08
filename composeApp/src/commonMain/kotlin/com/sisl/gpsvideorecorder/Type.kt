package com.sisl.gpsvideorecorder

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.montserrat_bold
import gpsvideorecorder.composeapp.generated.resources.montserrat_medium
import gpsvideorecorder.composeapp.generated.resources.montserrat_regular
import gpsvideorecorder.composeapp.generated.resources.montserrat_semibold
import gpsvideorecorder.composeapp.generated.resources.nicomoji_regular
import org.jetbrains.compose.resources.Font
import androidx.compose.material3.Typography


@Composable
fun MyAppTypography(): Typography {

    val montserratBold = FontFamily(
        Font(Res.font.montserrat_bold, FontWeight.Bold)
    )

    val montserratRegular = FontFamily(
        Font(Res.font.montserrat_regular, FontWeight.Normal)
    )

    val montserratMedium = FontFamily(
        Font(Res.font.montserrat_medium, FontWeight.Medium)
    )

    val montserratSemiBold = FontFamily(
        Font(Res.font.montserrat_semibold, FontWeight.SemiBold)
    )

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = montserratRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        titleLarge = TextStyle(
            fontFamily = montserratBold,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        labelLarge = TextStyle(
            fontFamily = montserratMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ) ,
        labelMedium = TextStyle(
            fontFamily = montserratSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    )

}