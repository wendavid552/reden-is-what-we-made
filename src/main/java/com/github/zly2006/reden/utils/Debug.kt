package com.github.zly2006.reden.utils

import org.slf4j.LoggerFactory

private var REDEN_DEBUG_LOGGER = LoggerFactory.getLogger("Reden/Debug")
@JvmField
var isDebug = false
@JvmField
var debugLogger: (String) -> Unit = { if (isDebug) REDEN_DEBUG_LOGGER.debug(it) }
