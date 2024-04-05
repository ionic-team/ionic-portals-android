package io.ionic.portals

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * ViewModel containing a Portal.
 */
open class PortalViewModel: ViewModel() {

    /**
     * MutableStateFlow for the Portal.
     */
    val state = MutableStateFlow<Portal?>(null)
}