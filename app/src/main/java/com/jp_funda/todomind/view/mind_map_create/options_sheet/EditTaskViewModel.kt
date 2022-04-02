package com.jp_funda.todomind.view.mind_map_create.options_sheet

import androidx.compose.material.ExperimentalMaterialApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.jp_funda.todomind.data.repositories.ogp.OgpRepository
import com.jp_funda.todomind.data.repositories.task.TaskRepository
import com.jp_funda.todomind.data.shared_preferences.SettingsPreferences
import com.jp_funda.todomind.view.task_detail.TaskEditableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalPagerApi
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    taskRepository: TaskRepository,
    ogpRepository: OgpRepository,
    settingsPreferences: SettingsPreferences,
) : TaskEditableViewModel(taskRepository, ogpRepository, settingsPreferences)