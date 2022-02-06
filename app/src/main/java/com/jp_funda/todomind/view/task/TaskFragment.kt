package com.jp_funda.todomind.view.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.view.MainViewModel
import com.jp_funda.todomind.view.components.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Integer.max

@AndroidEntryPoint
class TaskFragment : Fragment() {

    // ViewModels
    private val taskViewModel by viewModels<TaskViewModel>()
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
                NewTaskFAB(onClick = {
                    NavHostFragment.findNavController(this@TaskFragment)
                        .navigate(R.id.action_navigation_task_to_navigation_task_detail)
                }) { // TODO add navigation to new task screen
                    TaskContent()
                }
            }
        }
    }

    @Preview
    @Composable
    fun TaskContent() {
        val tasks by taskViewModel.taskList.observeAsState()
        val selectedTabIndex by taskViewModel.selectedTabIndex.observeAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var showingTasks by remember { mutableStateOf(tasks!!) }

        // Show Undo snackbar if currentlyDeletedTask is not null
        LaunchedEffect(snackbarHostState) {
            mainViewModel.currentlyDeletedTask?.let {
                taskViewModel.showUndoDeleteSnackbar(
                    snackbarHostState = snackbarHostState,
                    deletedTask = it
                )
            }
        }

        // Main Contents
        if (tasks!!.isNotEmpty()) {
            showingTasks = filterTasksByStatus(
                status = TaskStatus.values().first { it.ordinal == selectedTabIndex },
                tasks = tasks!!,
            )

            Column {

                TaskTab(selectedTabIndex!!, onTabChange = { status ->
                    taskViewModel.setSelectedTabIndex(status.ordinal)
                })

                TaskList(
                    listPadding = 20,
                    tasks = showingTasks,
                    onCheckChanged = { task ->
                        taskViewModel.updateTaskWithDelay(task)
                        scope.launch {
                            taskViewModel.showCheckBoxChangedSnackbar(
                                task,
                                snackbarHostState
                            )
                        }
                    },
                    onMove = { fromIndex, toIndex ->
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
            }

            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
                // Status update Snackbar
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 70.dp)
                )
            }


        } else {
            CircularProgressIndicator()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // To avoid using extra memory and null pointer exception after delete tasksList item reset tasksList
        taskViewModel.setTaskListEmpty()
        mainViewModel.currentlyDeletedTask = null
    }
}