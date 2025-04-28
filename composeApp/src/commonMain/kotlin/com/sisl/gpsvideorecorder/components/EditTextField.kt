package com.sisl.gpsvideorecorder.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sisl.gpsvideorecorder.EditTextBackgroundColor
import com.sisl.gpsvideorecorder.MyAppTypography
import com.sisl.gpsvideorecorder.PrimaryColor

@Composable
fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    isSingleLine: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    keyboardType: KeyboardType = KeyboardType.Text, // New param
    imeAction: ImeAction = ImeAction.Done,
    drawableTrailingIcon: ImageVector? = null,
    drawableLeadingIcon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                hint,
                fontSize = 14.sp,
                fontFamily = MyAppTypography().labelMedium.fontFamily
            )
        },
        singleLine = isSingleLine,
        textStyle = textStyle,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            cursorColor = PrimaryColor,
            focusedContainerColor = EditTextBackgroundColor,
            unfocusedContainerColor = EditTextBackgroundColor
        ),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = drawableTrailingIcon?.let {
            {
                Icon(
                    imageVector = drawableTrailingIcon,
                    contentDescription = "trailing icon",
                    tint = PrimaryColor.copy(alpha = 0.7f)
                )
            }
        },
        leadingIcon = drawableLeadingIcon?.let {
            { Icon(imageVector = drawableLeadingIcon, contentDescription = "leading icon") }
        },
        visualTransformation = visualTransformation,
    )

}