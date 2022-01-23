package com.jp_funda.todomind.view.top

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jp_funda.todomind.view.components.*

class TopFragment : Fragment() {

    companion object {
        fun newInstance() = TopFragment()
    }

    private lateinit var viewModel: TopViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[TopViewModel::class.java]
//        return inflater.inflate(R.layout.top_fragment, container, false)
        return ComposeView(requireContext()).apply {
            setContent {
                TopContent()
            }
        }
    }

    @Composable
    fun TopContent() {
        LazyColumn {
            item {
                // Section Recent Mind Map
                RecentMindMapSection(fragment = this@TopFragment)

                // Section Tasks
                var selectedTabIndex by remember { mutableStateOf(0) }
                Text(
                    text = "Tasks",
                    modifier = Modifier.padding(start = 20.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.h6,
                )
                TaskTab()
            }
            // todo fill with data
            items(items = List(10) { "d" }) { str ->
                TaskRow(modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }

    // Top components
    @Composable
    fun AddButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier.clip(RoundedCornerShape(1000.dp)),
            colors = ButtonDefaults.buttonColors(Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = text)
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add",
                )
            }
        }
    }
}