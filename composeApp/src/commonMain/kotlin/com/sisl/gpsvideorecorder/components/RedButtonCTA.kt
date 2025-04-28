package com.sisl.gpsvideorecorder.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sisl.gpsvideorecorder.PrimaryColor
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RedButtonCTA(
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit,
    buttonText: String = "",
    drawableIcon: DrawableResource = Res.drawable.compose_multiplatform,
) {
    Row(modifier = modifier) {
        Button(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            onClick = { onButtonClicked.invoke() },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            )
        ) {
            Text(
                text = buttonText,
                color = Color.White
            )


            Icon(
                painter = painterResource(drawableIcon),
                contentDescription = "next",
                tint = Color(0xFFFFFFFF)
            )
        }


    }

}