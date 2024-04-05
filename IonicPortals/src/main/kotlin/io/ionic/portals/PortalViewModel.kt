package io.ionic.portals

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

open class PortalViewModel: ViewModel() {
    val state = MutableStateFlow<Portal?>(null)
}