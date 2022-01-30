package com.jp_funda.todomind.view.task_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.view.components.ColorPickerDialog
import com.jp_funda.todomind.view.components.TimePickerDialog
import com.jp_funda.todomind.view.components.DatePickerDialog
import com.jp_funda.todomind.view.components.WhiteButton
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.ExperimentalMaterialApi as ExperimentalMaterialApi1

@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    companion object {
        fun newInstance() = TaskDetailFragment()
    }

    private val taskDetailViewModel by viewModels<TaskDetailViewModel>()

    @ExperimentalMaterialApi1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TaskDetailContent()
            }
        }
    }

    @ExperimentalMaterialApi1
    @Preview
    @Composable
    fun TaskDetailContent() {
        // Set up dialogs
        val dateDialogState = rememberMaterialDialogState()
        val timeDialogState = rememberMaterialDialogState()
        val colorDialogState = rememberMaterialDialogState()
        DatePickerDialog(dateDialogState, resources)
        TimePickerDialog(timeDialogState, resources)
        ColorPickerDialog(colorDialogState, resources, { it -> /* TODO */ })
        colorDialogState.show()
//        dateDialogState.show()
//        timeDialogState.show()

        // Set up TextFields color
        val colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color(resources.getColor(R.color.teal_200)),
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Page Title
            Text(
                text = "Create New Task",
                color = Color.White,
                style = MaterialTheme.typography.h5,
            ) // TODO change by create/edit

            // Title TextField
            TextField(
                colors = colors,
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = "Enter title",
                        color = Color.Gray,
                        style = MaterialTheme.typography.h6,
                    )
                }) // TODO update memory

            // Description TextField
            TextField(
                colors = colors,
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(text = "Add description", color = Color.Gray)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notes_24dp),
                        contentDescription = "Description",
                        tint = Color.White
                    )
                })

            // Date & Time
            Row(modifier = Modifier.fillMaxWidth()) {
                // Date
                TextField(
                    colors = colors,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(text = "Due date/time", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            tint = Color.White,
                            contentDescription = "Date",
                        )
                    },
                    readOnly = true,
                )

                // Time TODO show only when date is filled
                TextField(
                    colors = colors,
                    modifier = Modifier.fillMaxWidth(),
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(text = "Add time", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alarm_24dp),
                            tint = Color.White,
                            contentDescription = "Time",
                        )
                    },
                    readOnly = true,
                )
            }

            // Color
            TextField(
                colors = colors,
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(text = "Set color", color = Color.Gray)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_color_24dp),
                        tint = Color(resources.getColor(R.color.teal_200)), // TODO use task color
                        contentDescription = "Color",
                    )
                },
                readOnly = true,
            )

            // Status
            val statusOptions = TaskStatus.values()
            var expanded by remember { mutableStateOf(false) }
            var selectedStatus by remember { mutableStateOf(statusOptions[0]) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    colors = colors,
                    value = "Status - ${selectedStatus.name}",
                    onValueChange = {},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Status",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    readOnly = true,
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedStatus = option
                            expanded = false
                        }) {
                            Text(text = option.name)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                WhiteButton(text = "OK", onClick = { /*TODO*/ }, Icons.Default.Check)
                
                Spacer(modifier = Modifier.width(30.dp))

                WhiteButton(text = "Delete", onClick = { /*TODO*/ }, Icons.Default.Delete)
            }
        }
    }
}