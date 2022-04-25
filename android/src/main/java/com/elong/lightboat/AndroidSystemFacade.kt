package com.elong.lightboat

import android.content.Context
import com.elong.lightboat.download.SystemFacade
import java.io.File

class AndroidSystemFacade(val context: Context): SystemFacade {
    override fun downloadDir(type: String): File {
        //TODO: make sub dir for type
        return context.filesDir!!
    }
}