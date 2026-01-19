@file:OptIn(ExperimentalMaterial3Api::class)
package com.plcoding.cryptotracker.settings.releases.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.core.presentation.util.ObserveAsEvents
import com.plcoding.cryptotracker.settings.releases.presentation.components.ReleaseAuthorCard
import com.plcoding.cryptotracker.settings.releases.presentation.components.ReleaseDownloadCard
import com.plcoding.cryptotracker.settings.releases.presentation.components.ReleaseHeaderSection
import com.plcoding.cryptotracker.settings.releases.presentation.components.ReleaseNotesCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReleaseRoot(
    viewModel: ReleaseViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ReleaseEvent.Error -> {

            }
            is ReleaseEvent.OnDownload -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(event.assets)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }
    }

    ReleaseScreen(
        onDownloadClick = { downloadUrl ->
            viewModel.onAction(ReleaseAction.OnDownloadClick(downloadUrl))
        },
        state = state.value
    )
}

@Composable
private fun ReleaseScreen(
    onDownloadClick: (String) -> Unit = {},
    state: ReleaseState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.releases.isNotEmpty() -> {
                val release = state.releases.first()

                release.assets[0].browser_download_url
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    ReleaseHeaderSection(release)

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ReleaseDownloadCard(
                            release = release,
                            onDownloadReleaseClick = {
                                onDownloadClick(
                                    release.assets[0].browser_download_url
                                )
                            }
                        )

                        ReleaseAuthorCard(release)

                        ReleaseNotesCard(release)

                        Text(
                            text = stringResource(R.string.release_footer),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No releases available",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Check back later for updates",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}