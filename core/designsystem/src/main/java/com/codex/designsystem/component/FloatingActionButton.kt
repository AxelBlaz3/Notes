package com.codex.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun NotesFloatingActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes contentDescription: Int,
    @StringRes titleRes: Int?,
    onClick: () -> Unit,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .animateContentSize()
        ) {
            Icon(
                imageVector = icon, contentDescription = stringResource(id = contentDescription),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            if (titleRes != null) {
                Text(
                    text = stringResource(id = titleRes),
                    modifier = Modifier.padding(start = ButtonDefaults.IconSpacing),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}