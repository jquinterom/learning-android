package com.jhon.dogedex.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun AuthField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onTextChanged: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    errorMessageId: Int? = null,
    errorSemantic: String = "",
    fieldSemantic: String = "",
) {
    Column(
        modifier = modifier,
    ) {
        if (errorMessageId != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = errorSemantic },
                text = stringResource(id = errorMessageId)
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = fieldSemantic },
            label = { Text(text = label) },
            value = value,
            onValueChange = {
                onTextChanged(it)
            },
            visualTransformation = visualTransformation,
            isError = errorMessageId != null
        )
    }
}