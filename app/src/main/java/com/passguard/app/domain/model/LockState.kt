package com.passguard.app.domain.model

sealed class LockState {
    object Locked : LockState()
    object Unlocked : LockState()
    object NoPinSet : LockState()
}
