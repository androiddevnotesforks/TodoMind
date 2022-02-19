package com.jp_funda.todomind.view.task_detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.util.UrlUtil
import com.jp_funda.todomind.view.MainViewModel
import com.jp_funda.todomind.view.components.*
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@androidx.compose.material.ExperimentalMaterialApi
@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    companion object {
        fun newInstance() = TaskDetailFragment()
    }

    // ViewModels
    private val taskDetailViewModel by viewModels<TaskDetailViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Check whether to edit or create new task by mainViewModel editingTask
        mainViewModel.editingTask?.let { editingTask ->
            taskDetailViewModel.setEditingTask(editingTask)
        }

        // Set ActionBar title
        if (!taskDetailViewModel.isEditing) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "New Task"
        } else {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Editing"
        }

        return ComposeView(requireContext()).apply {
            setContent {
                TaskDetailContent()
            }
        }
    }

    @Preview
    @Composable
    fun TaskDetailContent() {
        val context = LocalContext.current
        // Set up data
        val observedTask by taskDetailViewModel.task.observeAsState()
        val ogpResult by taskDetailViewModel.ogpResult.observeAsState()

        observedTask?.let { task ->
            // Set up dialogs
            val dateDialogState = rememberMaterialDialogState()
            val timeDialogState = rememberMaterialDialogState()
            val colorDialogState = rememberMaterialDialogState()
            DatePickerDialog(dateDialogState) { selectedDate ->
                taskDetailViewModel.setDate(selectedDate)
            }
            TimePickerDialog(timeDialogState, resources) { selectedTime ->
                taskDetailViewModel.setTime(selectedTime)
            }
            ColorPickerDialog(colorDialogState) { selectedColor ->
                taskDetailViewModel.setColor(selectedColor.toArgb())
            }

            // Set up TextFields color
            val colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                disabledTextColor = Color.White,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color(ContextCompat.getColor(LocalContext.current, R.color.teal_200)),
            )

            LaunchedEffect(ogpResult) {
                if (!task.description.isNullOrEmpty()) {
                    taskDetailViewModel.extractUrlAndFetchOgp(task.description!!)
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title TextField
                TextField(
                    colors = colors,
                    modifier = Modifier.fillMaxWidth(),
                    value = task.title ?: "",
                    onValueChange = taskDetailViewModel::setTitle,
                    textStyle = MaterialTheme.typography.h6,
                    placeholder = {
                        Text(
                            text = "Enter title",
                            color = Color.Gray,
                            style = MaterialTheme.typography.h6,
                        )
                    },
                )

                // Description TextField
                TextField(
                    colors = colors,
                    modifier = Modifier.fillMaxWidth(),
                    value = task.description ?: "",
                    onValueChange = {
                        taskDetailViewModel.setDescription(it)
                        taskDetailViewModel.extractUrlAndFetchOgp(it)
                    },
                    textStyle = MaterialTheme.typography.body1,
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

                // OGP thumbnail
                ogpResult?.image?.let {
                    OgpThumbnail(ogpResult = ogpResult!!, context = context)
                }

                // Date & Time
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Date
                    TextField(
                        colors = colors,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .clickable { dateDialogState.show() },
                        value = if (task.dueDate != null)
                            SimpleDateFormat(
                                "EEE MM/dd",
                                Locale.getDefault()
                            ).format(task.dueDate!!) else "",
                        onValueChange = {},
                        placeholder = {
                            Text("Add date/time", color = Color.Gray)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                tint = Color.White,
                                contentDescription = "Date",
                            )
                        },
                        readOnly = true,
                        enabled = false,
                    )

                    // Time
                    TextField(
                        colors = colors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Log.d("Click", "lkds;")
                                timeDialogState.show()
                            },
                        value = if (task.dueDate != null)
                            SimpleDateFormat(
                                "hh:mm aaa",
                                Locale.getDefault()
                            ).format(task.dueDate!!) else "",
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
                        enabled = false,
                    )
                }

                // Color
                TextField(
                    colors = colors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { colorDialogState.show() },
                    value = if (task.color != null) "Color(Argb): " + task.color.toString() else "",
                    onValueChange = {},
                    placeholder = {
                        Text(text = "Set color", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_color_24dp),
                            tint = Color(
                                task.color ?: ContextCompat.getColor(
                                    LocalContext.current,
                                    R.color.teal_200
                                )
                            ),
                            contentDescription = "Color",
                        )
                    },
                    readOnly = true,
                    enabled = false,
                )

                // Status
                val statusOptions = TaskStatus.values()
                var expanded by remember { mutableStateOf(false) }
//            var selectedStatus by remember { mutableStateOf(statusOptions[0]) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        colors = colors,
                        value = "Status - ${task.statusEnum.name}",
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
                                taskDetailViewModel.setStatus(option)
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
                    WhiteButton(
                        text = "OK",
                        leadingIcon = Icons.Default.Check,
                    ) {
                        taskDetailViewModel.saveTask()
                        findNavController().popBackStack()
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    WhiteButton(
                        text = "Delete",
                        leadingIcon = Icons.Default.Delete,
                    ) {
                        // Delete task from DB(Edit mode) or Only Pop fragment(Create mode)
                        taskDetailViewModel.deleteTask(
                            task = task,
                            onSuccess = { findNavController().popBackStack() })
                        // Set CurrentlyDeletedTask at MainViewModel for undo snackbar
                        if (taskDetailViewModel.isEditing) {
                            mainViewModel.currentlyDeletedTask = task
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        taskDetailViewModel.isEditing = false
        mainViewModel.editingTask = null
    }
}