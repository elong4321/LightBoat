package com.elong.lightboat.download

import java.io.File

interface SystemFacade {
    fun downloadDir(type: String): File
}