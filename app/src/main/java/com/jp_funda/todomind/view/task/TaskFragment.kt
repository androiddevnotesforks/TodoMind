package com.jp_funda.todomind.view.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.view.MainViewModel
import com.jp_funda.todomind.view.TaskViewModel
import com.jp_funda.todomind.view.components.ColumnWithTaskList
import com.jp_funda.todomind.view.components.NewTaskFAB
import com.jp_funda.todomind.view.components.filterTasksByStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Integer.max

@AndroidEntryPoint
class TaskFragment : Fragment() {

    // ViewModels
    private val taskViewModel: TaskViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance() = TaskFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        taskViewModel.refreshTaskListData()

        return ComposeView(requireContext()).apply {
            setContent {
                NewTaskFAB(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Task") },
                            backgroundColor = colorResource(id = R.color.deep_purple),
                            contentColor = Color.White,
                        )
                    },
                    onClick = {
                        NavHostFragment.findNavController(this@TaskFragment)
                            .navigate(R.id.action_navigation_task_to_navigation_task_detail)
                    }) {
                    TaskContent()
                }
            }
        }
    }

    @Composable
    fun TaskContent() {
        val observedTasks by taskViewModel.taskList.observeAsState()
        val selectedTabStatus by taskViewModel.selectedStatusTab.observeAsState(TaskStatus.InProgress)
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        // Show Undo snackbar if currentlyDeletedTask is not null
        LaunchedEffect(snackbarHostState) {
            mainViewModel.currentlyDeletedTask?.let {
                taskViewModel.showUndoDeleteSnackbar(
                    snackbarHostState = snackbarHostState,
                    deletedTask = it
                )
            }
            mainViewModel.currentlyDeletedTask = null
        }

        // Main Contents
        observedTasks?.let { tasks ->
            var showingTasks by remember { mutableStateOf(tasks) }

            showingTasks = filterTasksByStatus(
                status = TaskStatus.values().first { it == selectedTabStatus },
                tasks = tasks,
            )

            Column {
                ColumnWithTaskList(
                    selectedTabStatus = selectedTabStatus,
                    onTabChange = { status ->
                        taskViewModel.setSelectedStatusTab(status)
                    },
                    showingTasks = showingTasks,
                    onCheckChanged = { task ->
                        taskViewModel.updateTaskWithDelay(task)
                        scope.launch {
                            taskViewModel.showCheckBoxChangedSnackbar(
                                task,
                                snackbarHostState
                            )
                        }
                    },
                    onRowMove = { fromIndex, toIndex ->
                        // Replace task's reversedOrder property
                        if (max(fromIndex, toIndex) < showingTasks.size) {
                            val fromTask = showingTasks.sortedBy { task -> task.reversedOrder }
                                .reversed()[fromIndex]
                            val toTask = showingTasks.sortedBy { task -> task.reversedOrder }
                                .reversed()[toIndex]
                            taskViewModel.replaceReversedOrderOfTasks(fromTask, toTask)
                        }
                    },
                    onRowClick = { task ->
                        mainViewModel.editingTask = task
                        findNavController().navigate(R.id.action_navigation_task_to_navigation_task_detail)
                    }
                )

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Status update Snackbar
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.padding(bottom = 70.dp)
                    )
                }
            }
        } ?: run {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    color = colorResource(R.color.teal_200),
                    strokeWidth = 10.dp
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.h5,
                    color = Color.White
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.currentlyDeletedTask = null
    }
}