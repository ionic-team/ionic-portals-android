package io.ionic.portals

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Portal View Model
 */
class PortalViewModel: ViewModel() {

    /**
     * MutableStateFlow containing a Portal.
     */
    val portal = MutableStateFlow<Portal?>(null)
}